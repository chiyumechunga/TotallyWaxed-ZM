package com.totallywaxed.data.remote

import com.google.android.gms.common.api.Api
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.totallywaxed.domain.model.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Singleton
class FirebaseRealtimeDataSource @Inject constructor(
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
) {
    // Region: Appointments
    private val appointmentsRef get() = database.getReference("appointments")

    suspend fun getAppointments(): List<Appointment> = suspendCancellableCoroutine { continuation ->
        appointmentsRef.get().addOnSuccessListener { snapshot ->
            val appointments = snapshot.children.mapNotNull { it.getValue<Appointment>() }
            continuation.resume(appointments)
        }.addOnFailureListener { exception ->
            continuation.resumeWithException(DatabaseException("Failed to fetch appointments", exception))
        }
    }

    fun observeAppointments(): Flow<List<Appointment>> = callbackFlow {
        val listener = appointmentsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val appointments = snapshot.children.mapNotNull { it.getValue<Appointment>() }
                trySend(appointments)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        })
        awaitClose { appointmentsRef.removeEventListener(listener) }
    }

    suspend fun updateAppointmentStatus(appointmentId: String, newStatus: String) =
        suspendCancellableCoroutine { continuation ->
            appointmentsRef.child(appointmentId).child("status")
                .setValue(newStatus)
                .addOnSuccessListener { continuation.resume(Unit) }
                .addOnFailureListener { exception ->
                    continuation.resumeWithException(
                        DatabaseException("Failed to update appointment status", exception)
                    )
                }
        }

    // Region: Services
    private val servicesRef get() = database.getReference("services")

    suspend fun getServiceCategories(): List<ServiceCategory> =
        suspendCancellableCoroutine { continuation ->
            servicesRef.child("categories").get()
                .addOnSuccessListener { snapshot ->
                    val categories = snapshot.children.mapNotNull { it.getValue<ServiceCategory>() }
                    continuation.resume(categories)
                }
                .addOnFailureListener { exception ->
                    continuation.resumeWithException(
                        DatabaseException("Failed to fetch service categories", exception)
                    )
                }
        }

    suspend fun getActiveServices(): List<ServiceItem> =
        suspendCancellableCoroutine { continuation ->
            servicesRef.child("items").get()
                .addOnSuccessListener { snapshot ->
                    val services = snapshot.children.mapNotNull { it.getValue<ServiceItem>() }
                        .filter { it.isActive }
                    continuation.resume(services)
                }
                .addOnFailureListener { exception ->
                    continuation.resumeWithException(
                        DatabaseException("Failed to fetch active services", exception)
                    )
                }
        }

    // Region: Users
    private val usersRef get() = database.getReference("users")

    suspend fun getClient(clientId: String): Api.Client? =
        suspendCancellableCoroutine { continuation ->
            usersRef.child("clients").child(clientId).get()
                .addOnSuccessListener { snapshot ->
                    continuation.resume(snapshot.getValue<Api.Client>())
                }
                .addOnFailureListener { exception ->
                    continuation.resumeWithException(
                        DatabaseException("Failed to fetch client", exception)
                    )
                }
        }

    suspend fun updateClientPreferences(clientId: String, preferences: List<String>) =
        suspendCancellableCoroutine { continuation ->
            usersRef.child("clients").child(clientId)
                .child("preferredServices")
                .setValue(preferences)
                .addOnSuccessListener { continuation.resume(Unit) }
                .addOnFailureListener { exception ->
                    continuation.resumeWithException(
                        DatabaseException("Failed to update client preferences", exception)
                    )
                }
        }

    // Helper class for exceptions
    inner class DatabaseException(message: String, cause: Throwable? = null) :
        Exception(message, cause)
}