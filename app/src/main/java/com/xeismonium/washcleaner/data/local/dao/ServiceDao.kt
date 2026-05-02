package com.xeismonium.washcleaner.data.local.dao

import androidx.room.*
import com.xeismonium.washcleaner.data.local.entity.ServiceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ServiceDao {
    @Query("SELECT * FROM services ORDER BY name ASC")
    fun getServicesFlow(): Flow<List<ServiceEntity>>

    @Query("SELECT * FROM services WHERE isActive = 1 ORDER BY name ASC")
    fun getActiveServices(): Flow<List<ServiceEntity>>

    @Query("SELECT * FROM services WHERE id = :id")
    suspend fun getServiceById(id: String): ServiceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertService(service: ServiceEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServices(services: List<ServiceEntity>)

    @Update
    suspend fun updateService(service: ServiceEntity)

    @Delete
    suspend fun deleteService(service: ServiceEntity)
}
