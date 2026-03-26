package edu.javeriana.fixup.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import edu.javeriana.fixup.data.datasource.*

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    abstract fun bindAuthDataSource(
        authDataSourceImpl: AuthDataSourceImpl
    ): AuthDataSource

    @Binds
    abstract fun bindFeedDataSource(
        feedDataSourceImpl: FeedDataSourceImpl
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
}
