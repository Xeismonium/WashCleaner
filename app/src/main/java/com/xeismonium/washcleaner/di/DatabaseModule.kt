package com.xeismonium.washcleaner.di

import android.content.Context
import androidx.room.Room
import com.xeismonium.washcleaner.data.local.database.MIGRATION_2_3
import com.xeismonium.washcleaner.data.local.database.WashCleanerDatabase
import com.xeismonium.washcleaner.data.local.database.dao.CustomerDao
import com.xeismonium.washcleaner.data.local.database.dao.ServiceDao
import com.xeismonium.washcleaner.data.local.database.dao.TransactionDao
import com.xeismonium.washcleaner.data.local.database.dao.TransactionServiceDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideWashCleanerDatabase(
        @ApplicationContext context: Context
    ): WashCleanerDatabase {
        return Room.databaseBuilder(
            context,
            WashCleanerDatabase::class.java,
            "wash_cleaner_database"
        )
            .addMigrations(MIGRATION_2_3)
            .fallbackToDestructiveMigration(true)
            .build()
    }

    @Provides
    @Singleton
    fun provideCustomerDao(database: WashCleanerDatabase): CustomerDao {
        return database.customerDao()
    }

    @Provides
    @Singleton
    fun provideServiceDao(database: WashCleanerDatabase): ServiceDao {
        return database.serviceDao()
    }

    @Provides
    @Singleton
    fun provideTransactionDao(database: WashCleanerDatabase): TransactionDao {
        return database.transactionDao()
    }

    @Provides
    @Singleton
    fun provideTransactionServiceDao(database: WashCleanerDatabase): TransactionServiceDao {
        return database.transactionServiceDao()
    }
}
