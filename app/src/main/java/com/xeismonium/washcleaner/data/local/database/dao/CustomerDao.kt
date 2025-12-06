package com.xeismonium.washcleaner.data.local.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.xeismonium.washcleaner.data.local.database.entity.CustomerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomerDao {
    // Query methods
    @Query("SELECT * FROM customers ORDER BY name ASC")
    fun getAll(): Flow<List<CustomerEntity>>

    @Query("SELECT * FROM customers WHERE id = :id")
    fun getById(id: Long): Flow<CustomerEntity?>

    @Query("SELECT * FROM customers WHERE id = :id")
    suspend fun getByIdOnce(id: Long): CustomerEntity?

    @Query("SELECT * FROM customers WHERE name LIKE '%' || :keyword || '%' ORDER BY name ASC")
    fun searchByName(keyword: String): Flow<List<CustomerEntity>>

    // Insert/Update operations
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(customer: CustomerEntity): Long

    @Upsert
    suspend fun upsert(customer: CustomerEntity): Long

    @Update
    suspend fun update(customer: CustomerEntity): Int

    // Delete operations
    @Delete
    suspend fun delete(customer: CustomerEntity): Int

    @Query("DELETE FROM customers WHERE id = :id")
    suspend fun deleteById(id: Long): Int
}