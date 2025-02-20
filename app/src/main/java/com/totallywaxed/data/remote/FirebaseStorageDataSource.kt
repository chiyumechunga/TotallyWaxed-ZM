package com.totallywaxed.data.remote

import com.google.android.gms.common.api.Api
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.totallywaxed.domain.model.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseStorageDataSource @Inject constructor(
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
) {
    // Reference shortcuts
    private val appointmentsRef get() = database.getReference("appointments")
    private val usersRef get() = database.getReference("users")
    private val servicesRef get() = database.getReference("services")
    private val schedulesRef get() = database.getReference("schedules")
    private val settingsRef get() = database.getReference("settings")
    private val logsRef get() = database.getReference("logs")

    // region Appointments
    suspend fun createAppointment(appointment: Appointment): String {
        val newRef = appointmentsRef.push()
        newRef.setValue(appointment).await()
        return newRef.key ?: throw DatabaseException("Failed to create appointment")
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

    suspend fun updateAppointmentStatus(appointmentId: String, newStatus: String) {
        appointmentsRef.child(appointmentId).child("status").setValue(newStatus).await()
    }
    // endregion

    // region Users
    suspend fun getClient(clientId: String): Api.Client? {
        return usersRef.child("clients").child(clientId).get().await().getValue<Api.Client>()
    }

    suspend fun updateClientPreferences(clientId: String, preferences: List<String>) {
        usersRef.child("clients").child(clientId).child("preferredServices")
            .setValue(preferences).await()
    }

    fun observeAdmins(): Flow<List<AdminUser>> = callbackFlow {
        val listener = usersRef.child("admins").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val admins = snapshot.children.mapNotNull { it.getValue<AdminUser>() }
                trySend(admins)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        })
        awaitClose { usersRef.child("admins").removeEventListener(listener) }
    }
    // endregion

    // region Services
    suspend fun getServiceCategories(): List<ServiceCategory> {
        return servicesRef.child("categories").get().await()
            .children.mapNotNull { it.getValue<ServiceCategory>() }
    }

    suspend fun getActiveServices(): List<ServiceItem> {
        return servicesRef.child("items").get().await()
            .children.mapNotNull { it.getValue<ServiceItem>() }
            .filter { it.isActive }
    }
    // endregion

    // region Schedules
    suspend fun getBusinessHours(): Map<String, BusinessDay> {
        return schedulesRef.child("business_hours").get().await()
            .getValue<Map<String, BusinessDay>>() ?: emptyMap()
    }

    suspend fun addBlockedDate(blockedDate: BlockedDate): String {
        val newRef = schedulesRef.child("blocked_dates").push()
        newRef.setValue(blockedDate).await()
        return newRef.key ?: throw DatabaseException("Failed to create blocked date")
    }
    // endregion

    // region Settings
    suspend fun getBusinessSettings(): BusinessSettings {
        return settingsRef.child("business").get().await()
            .getValue<BusinessSettings>() ?: throw DatabaseException("Settings not found")
    }

    suspend fun updateNotificationSettings(settings: NotificationSettings) {
        settingsRef.child("notifications").setValue(settings).await()
    }
    // endregion

    // region Logs
    suspend fun logAction(action: SystemLog) {
        logsRef.push().setValue(action).await()
    }
    // endregion

    class DatabaseException(message: String) : Exception(message)
}