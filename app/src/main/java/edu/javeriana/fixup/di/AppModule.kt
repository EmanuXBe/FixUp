package edu.javeriana.fixup.di

import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import edu.javeriana.fixup.data.datasource.AuthDataSource
import edu.javeriana.fixup.data.repository.AuthRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideAuthDataSource(auth: FirebaseAuth): AuthDataSource = AuthDataSource(auth)

    @Provides
    @Singleton
    fun provideAuthRepository(dataSource: AuthDataSource): AuthRepository = AuthRepository(dataSource)
}