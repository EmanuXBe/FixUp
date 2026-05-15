package edu.javeriana.fixup.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import edu.javeriana.fixup.BuildConfig
import edu.javeriana.fixup.data.util.AppConstants
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        val auth = FirebaseAuth.getInstance()
        if (BuildConfig.DEBUG) {
            try {
                auth.useEmulator(AppConstants.EMULATOR_HOST, AppConstants.AUTH_PORT)
            } catch (e: IllegalStateException) { }
            // Clear any real-Firebase session so the emulator GRPC stream
            // doesn't fail with INVALID_REFRESH_TOKEN on every write.
            auth.signOut()
        }
        return auth
    }

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        val firestore = FirebaseFirestore.getInstance()
        if (BuildConfig.DEBUG) {
            try {
                firestore.firestoreSettings = FirebaseFirestoreSettings.Builder()
                    .setPersistenceEnabled(false)
                    .build()
            } catch (e: IllegalStateException) { }
            try {
                firestore.useEmulator(AppConstants.EMULATOR_HOST, AppConstants.FIRESTORE_PORT)
            } catch (e: IllegalStateException) { }
        }
        return firestore
    }

    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage {
        val storage = FirebaseStorage.getInstance()
        if (BuildConfig.DEBUG) {
            try {
                storage.useEmulator(AppConstants.EMULATOR_HOST, AppConstants.STORAGE_PORT)
            } catch (e: IllegalStateException) {
                // Already configured, likely in tests
            }
        }
        return storage
    }
}