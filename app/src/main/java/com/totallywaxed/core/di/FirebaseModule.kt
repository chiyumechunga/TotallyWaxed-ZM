package com.totallywaxed.core.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.totallywaxed.core.common.FirebaseAuthUtils
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AppointmentsRef

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class UsersRef

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ServicesRef

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class SchedulesRef

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class SettingsRef

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class LogsRef

@Module
@InstallIn(SingletonComponent::class)
class FirebaseModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = Firebase.auth

    @Provides
    @Singleton
    fun provideFirebaseDatabase(): FirebaseDatabase = Firebase.database.apply {
        setPersistenceEnabled(true)
    }

    @Provides
    @Singleton
    fun provideRootDatabaseReference(database: FirebaseDatabase): DatabaseReference = database.reference

    @Provides
    @Singleton
    fun provideFirebaseAuthUtils(
        auth: FirebaseAuth,
        database: DatabaseReference // ✅ Inject root reference
    ): FirebaseAuthUtils = FirebaseAuthUtils(auth, database)

    // Keep only essential qualifiers
    @Provides
    @Singleton
    @AppointmentsRef
    fun provideAppointmentsReference(database: DatabaseReference): DatabaseReference =
        database.child("appointments")

    @Provides
    @Singleton
    @UsersRef
    fun provideUsersReference(database: DatabaseReference): DatabaseReference =
        database.child("users")
}