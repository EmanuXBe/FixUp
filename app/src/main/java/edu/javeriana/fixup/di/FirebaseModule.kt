package edu.javeriana.fixup.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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
            auth.useEmulator(AppConstants.EMULATOR_HOST, AppConstants.AUTH_PORT)
        }
        return auth
    }

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        val firestore = FirebaseFirestore.getInstance()
        if (BuildConfig.DEBUG) {
            firestore.useEmulator(AppConstants.EMULATOR_HOST, AppConstants.FIRESTORE_PORT)
        }
        return firestore
    }

    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage {
        val storage = FirebaseStorage.getInstance()
        if (BuildConfig.DEBUG) {
            storage.useEmulator(AppConstants.EMULATOR_HOST, AppConstants.STORAGE_PORT)
        }
        return storage
    }
}