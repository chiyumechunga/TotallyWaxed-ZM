package com.totallywaxed.data.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.totallywaxed.core.utils.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseAuthenticationDataSource @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val dispatcher: CoroutineDispatcher
) {

    val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser

    fun loginUser(email: String, password: String): Flow<Result<FirebaseUser>> = flow {
        emit(Result.Loading)
        try {
            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val user = authResult.user ?: throw Exception("Authentication failed")
            emit(Result.Success(user))
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Unknown error occurred"))
        }
    }.flowOn(dispatcher)

    fun registerUser(
        email: String,
        password: String,
        firstName: String,
        lastName: String
    ): Flow<Result<FirebaseUser>> = flow {
        emit(Result.Loading)
        try {
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val user = authResult.user ?: throw Exception("Registration failed")

            // Update user profile with name
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName("$firstName $lastName")
                .build()

            user.updateProfile(profileUpdates).await()
            emit(Result.Success(user))
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Registration failed"))
        }
    }.flowOn(dispatcher)

    fun resetPassword(email: String): Flow<Result<Boolean>> = flow {
        emit(Result.Loading)
        try {
            firebaseAuth.sendPasswordResetEmail(email).await()
            emit(Result.Success(true))
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Password reset failed"))
        }
    }.flowOn(dispatcher)

    fun logoutUser(): Flow<Result<Boolean>> = flow {
        emit(Result.Loading)
        try {
            firebaseAuth.signOut()
            emit(Result.Success(true))
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Logout failed"))
        }
    }.flowOn(dispatcher)

    fun getAuthState(): Flow<Result<FirebaseUser?>> = flow {
        emit(Result.Success(firebaseAuth.currentUser))
    }.flowOn(dispatcher)
}