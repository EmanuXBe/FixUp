package edu.javeriana.fixup.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.google.firebase.firestore.FirebaseFirestore
import edu.javeriana.fixup.data.network.api.FixUpApiService
import edu.javeriana.fixup.data.datasource.interfaces.*
import edu.javeriana.fixup.data.datasource.impl.*
import dagger.Provides
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    abstract fun bindAuthDataSource(
        authDataSourceImpl: AuthDataSourceImpl
    ): AuthDataSource

    @Binds
    abstract fun bindUserDataSource(
        userDataSourceImpl: UserDataSourceImpl
    ): UserDataSource

    @Binds
    abstract fun bindArticleDataSource(
        articleDataSourceImpl: ArticleDataSourceImpl
    ): ArticleDataSource

    @Binds
    abstract fun bindFeedDataSource(
        feedDataSourceImpl: FeedFirestoreDataSourceImpl
    ): FeedDataSource

    @Binds
    abstract fun bindRentDataSource(
        rentDataSourceImpl: RentDataSourceImpl
    ): RentDataSource

    @Binds
    abstract fun bindChatDataSource(
        chatDataSourceImpl: ChatDataSourceImpl
    ): ChatDataSource

    @Binds
    abstract fun bindProfileDataSource(
        profileDataSourceImpl: ProfileDataSourceImpl
    ): ProfileDataSource

    @Binds
    abstract fun bindCheckoutDataSource(
        checkoutDataSourceImpl: CheckoutDataSourceImpl
    ): CheckoutDataSource

    companion object {
        @Provides
        @Singleton
        fun provideReviewDataSource(
            firestore: FirebaseFirestore,
            apiService: FixUpApiService
        ): ReviewDataSource {
            // SWITCH DE ENTORNO: Comenta/Descomenta para cambiar la fuente de datos
            // return ReviewExpressDataSourceImpl(apiService)
            return ReviewFirebaseDataSourceImpl(firestore)
        }
    }
}
