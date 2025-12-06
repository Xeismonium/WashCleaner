package com.xeismonium.washcleaner.di

import com.xeismonium.washcleaner.data.repository.CustomerRepository
import com.xeismonium.washcleaner.data.repository.CustomerRepositoryImpl
import com.xeismonium.washcleaner.data.repository.ServiceRepository
import com.xeismonium.washcleaner.data.repository.ServiceRepositoryImpl
import com.xeismonium.washcleaner.data.repository.TransactionRepository
import com.xeismonium.washcleaner.data.repository.TransactionRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCustomerRepository(
        customerRepositoryImpl: CustomerRepositoryImpl
    ): CustomerRepository

    @Binds
    @Singleton
    abstract fun bindServiceRepository(
        serviceRepositoryImpl: ServiceRepositoryImpl
    ): ServiceRepository

    @Binds
    @Singleton
    abstract fun bindTransactionRepository(
        transactionRepositoryImpl: TransactionRepositoryImpl
    ): TransactionRepository
}
