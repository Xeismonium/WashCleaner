package com.xeismonium.washcleaner.di

import android.content.Context
import androidx.room.Room
import com.xeismonium.washcleaner.data.local.AppDatabase
import com.xeismonium.washcleaner.data.local.dao.CustomerDao
import com.xeismonium.washcleaner.data.local.dao.OrderDao
import com.xeismonium.washcleaner.data.local.dao.ServiceDao
import com.xeismonium.washcleaner.data.local.dao.UserDao
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
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "wash_cleaner.db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideUserDao(db: AppDatabase): UserDao = db.userDao()

    @Provides
    fun provideCustomerDao(db: AppDatabase): CustomerDao = db.customerDao()

    @Provides
    fun provideServiceDao(db: AppDatabase): ServiceDao = db.serviceDao()

    @Provides
    fun provideOrderDao(db: AppDatabase): OrderDao = db.orderDao()
}
