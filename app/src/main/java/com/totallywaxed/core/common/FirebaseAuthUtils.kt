package com.totallywaxed.core.common

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.ServerValue
import com.totallywaxed.domain.model.Role

class FirebaseAuthUtils {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference

    // User registration with role-based database storage
    fun registerUser(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        middleName: String?,
        phone: String?,
        role: Role,
        onComplete: (Boolean, String?) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { authTask ->
                if (authTask.isSuccessful) {
                    val user = authTask.result?.user
                    user?.let {
                        // Update user profile with display name
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName("$firstName $lastName")
                            .build()

                        it.updateProfile(profileUpdates)
                            .addOnCompleteListener { profileTask ->
                                if (profileTask.isSuccessful) {
                                    // Save additional user data to Realtime Database
                                    saveUserToDatabase(
                                        uid = it.uid,
                                        email = email,
                                        firstName = firstName,
                                        lastName = lastName,
                                        middleName = middleName,
                                        phone = phone,
                                        role = role,
                                        onComplete = onComplete
                                    )
                                } else {
                                    onComplete(false, profileTask.exception?.message)
                                }
                            }
                    } ?: onComplete(false, "User creation failed")
                } else {
                    onComplete(false, authTask.exception?.message)
                }
            }
    }

    private fun saveUserToDatabase(
        uid: String,
        email: String,
        firstName: String,
        lastName: String,
        middleName: String?,
        phone: String?,
        role: Role,
        onComplete: (Boolean, String?) -> Unit
    ) {
        val userData = when (role) {
            Role.CLIENT -> mapOf(
                "uid" to uid,
                "email" to email,
                "firstName" to firstName,
                "middleName" to middleName,
                "lastName" to lastName,
                "isActive" to true,
                "phone" to phone,
                "createdAt" to ServerValue.TIMESTAMP,
                "updatedAt" to ServerValue.TIMESTAMP,
                "role" to role.name
            )
            else -> mapOf(
                "uid" to uid,
                "email" to email,
                "firstName" to firstName,
                "middleName" to middleName,
                "lastName" to lastName,
                "isActive" to true,
                "phone" to phone,
                "lastLoginAt" to ServerValue.TIMESTAMP,
                "createdAt" to ServerValue.TIMESTAMP,
                "updatedAt" to ServerValue.TIMESTAMP,
                "role" to role.name
            )
        }

        val databasePath = when (role) {
            Role.CLIENT -> "users/clients/$uid"
            else -> "users/admins/$uid"
        }

        database.child(databasePath).setValue(userData)
            .addOnCompleteListener { dbTask ->
                if (dbTask.isSuccessful) {
                    onComplete(true, null)
                } else {
                    // Rollback user creation if database save fails
                    auth.currentUser?.delete()
                    onComplete(false, dbTask.exception?.message)
                }
            }
    }

    // User login with role-based data retrieval
    fun loginUser(email: String, password: String, onComplete: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Update last login time for admins
                    val user = auth.currentUser
                    user?.let {
                        if (it.email?.endsWith("@totallywaxed.com") == true) {
                            updateAdminLoginTime(it.uid)
                        }
                    }
                    onComplete(true, null)
                } else {
                    onComplete(false, task.exception?.message)
                }
            }
    }

    private fun updateAdminLoginTime(uid: String) {
        database.child("users/admins/$uid/lastLoginAt")
            .setValue(ServerValue.TIMESTAMP)
    }

    // Password reset functionality
    fun resetPassword(email: String, onComplete: (Boolean, String?) -> Unit) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                onComplete(task.isSuccessful, task.exception?.message)
            }
    }

    // Get current authenticated user
    fun getCurrentUser(): FirebaseUser? = auth.currentUser

    // User sign out
    fun signOut() {
        auth.signOut()
    }

    // Fetch user data from database
    fun fetchUserData(uid: String, role: Role, onComplete: (Map<String, Any>?) -> Unit) {
        val databasePath = when (role) {
            Role.CLIENT -> "users/clients/$uid"
            else -> "users/admins/$uid"
        }

        database.child(databasePath).get()
            .addOnSuccessListener { snapshot ->
                onComplete(snapshot.value as? Map<String, Any>)
            }
            .addOnFailureListener {
                onComplete(null)
            }
    }

    // Update user profile data
    fun updateUserProfile(
        uid: String,
        role: Role,
        updates: Map<String, Any>,
        onComplete: (Boolean, String?) -> Unit
    ) {
        val databasePath = when (role) {
            Role.CLIENT -> "users/clients/$uid"
            else -> "users/admins/$uid"
        }

        val updatedData = updates.toMutableMap().apply {
            put("updatedAt", ServerValue.TIMESTAMP)
        }

        database.child(databasePath).updateChildren(updatedData)
            .addOnCompleteListener { task ->
                onComplete(task.isSuccessful, task.exception?.message)
            }
    }
}