package com.xeismonium.washcleaner.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.xeismonium.washcleaner.data.local.database.dao.CustomerDao
import com.xeismonium.washcleaner.data.local.database.dao.ServiceDao
import com.xeismonium.washcleaner.data.local.database.dao.TransactionDao
import com.xeismonium.washcleaner.data.local.database.dao.TransactionServiceDao
import com.xeismonium.washcleaner.data.local.database.entity.CustomerEntity
import com.xeismonium.washcleaner.data.local.database.entity.LaundryTransactionEntity
import com.xeismonium.washcleaner.data.local.database.entity.ServiceEntity
import com.xeismonium.washcleaner.data.local.database.entity.TransactionServiceEntity

@Database(
    entities = [
        CustomerEntity::class,
        ServiceEntity::class,
        LaundryTransactionEntity::class,
        TransactionServiceEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class WashCleanerDatabase : RoomDatabase() {
    abstract fun customerDao(): CustomerDao
    abstract fun serviceDao(): ServiceDao
    abstract fun transactionDao(): TransactionDao
    abstract fun transactionServiceDao(): TransactionServiceDao
}
