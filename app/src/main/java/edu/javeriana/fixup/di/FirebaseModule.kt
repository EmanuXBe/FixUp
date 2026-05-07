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
        // Solo conectamos al emulador si realmente lo necesitas. 
        // Para usar datos reales, las siguientes líneas deben estar comentadas:
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
            // El host 10.0.2.2 es fundamental para que el emulador de Android vea el localhost de la PC.
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