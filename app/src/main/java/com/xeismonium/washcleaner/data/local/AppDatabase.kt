package com.xeismonium.washcleaner.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.xeismonium.washcleaner.data.local.dao.CustomerDao
import com.xeismonium.washcleaner.data.local.dao.OrderDao
import com.xeismonium.washcleaner.data.local.dao.ServiceDao
import com.xeismonium.washcleaner.data.local.dao.UserDao
import com.xeismonium.washcleaner.data.local.entity.CustomerEntity
import com.xeismonium.washcleaner.data.local.entity.OrderEntity
import com.xeismonium.washcleaner.data.local.entity.ServiceEntity
import com.xeismonium.washcleaner.data.local.entity.UserEntity

@Database(
    entities = [
        UserEntity::class,
        CustomerEntity::class,
        ServiceEntity::class,
        OrderEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun orderDao(): OrderDao
    abstract fun customerDao(): CustomerDao
    abstract fun serviceDao(): ServiceDao
    abstract fun userDao(): UserDao
}
