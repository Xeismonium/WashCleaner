package com.xeismonium.washcleaner.data.local.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.xeismonium.washcleaner.data.local.database.entity.ServiceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ServiceDao {
    // Query methods
    @Query("SELECT * FROM services ORDER BY name ASC")
    fun getAll(): Flow<List<ServiceEntity>>

    @Query("SELECT * FROM services WHERE isActive = 1 ORDER BY name ASC")
    fun getAllActive(): Flow<List<ServiceEntity>>

    @Query("SELECT * FROM services WHERE id = :id")
    fun getById(id: Long): Flow<ServiceEntity?>

    @Query("SELECT * FROM services WHERE id = :id")
    suspend fun getByIdOnce(id: Long): ServiceEntity?

    @Query("SELECT * FROM services WHERE name LIKE '%' || :keyword || '%' ORDER BY name ASC")
    fun searchByName(keyword: String): Flow<List<ServiceEntity>>

    // Insert/Update operations
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(service: ServiceEntity): Long

    @Upsert
    suspend fun upsert(service: ServiceEntity): Long

    @Update
    suspend fun update(service: ServiceEntity): Int

    @Query("UPDATE services SET isActive = :isActive WHERE id = :id")
    suspend fun updateActiveStatus(id: Long, isActive: Boolean): Int

    // Delete operations
    @Delete
    suspend fun delete(service: ServiceEntity): Int

    @Query("DELETE FROM services WHERE id = :id")
    suspend fun deleteById(id: Long): Int
}