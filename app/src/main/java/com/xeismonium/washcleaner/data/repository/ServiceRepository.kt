package com.xeismonium.washcleaner.data.repository

import com.xeismonium.washcleaner.data.local.database.dao.ServiceDao
import com.xeismonium.washcleaner.data.local.database.entity.ServiceEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

interface ServiceRepository {
    // Query methods using Flow for reactive UI updates
    fun getAll(): Flow<List<ServiceEntity>>
    fun getAllActive(): Flow<List<ServiceEntity>>
    fun getById(id: Long): Flow<ServiceEntity?>
    fun searchByName(keyword: String): Flow<List<ServiceEntity>>

    // One-time read operations
    suspend fun getByIdOnce(id: Long): ServiceEntity?

    // Insert operations
    suspend fun insert(service: ServiceEntity): Long

    // Upsert operations
    suspend fun upsert(service: ServiceEntity): Long

    // Update operations
    suspend fun update(service: ServiceEntity): Int
    suspend fun updateActiveStatus(id: Long, isActive: Boolean): Int

    // Delete operations
    suspend fun delete(service: ServiceEntity): Int
    suspend fun deleteById(id: Long): Int
}

@Singleton
class ServiceRepositoryImpl @Inject constructor(
    private val serviceDao: ServiceDao
) : ServiceRepository {

    // Query methods using Flow for reactive UI updates
    override fun getAll(): Flow<List<ServiceEntity>> =
        serviceDao.getAll()

    override fun getAllActive(): Flow<List<ServiceEntity>> =
        serviceDao.getAllActive()

    override fun getById(id: Long): Flow<ServiceEntity?> =
        serviceDao.getById(id)

    override fun searchByName(keyword: String): Flow<List<ServiceEntity>> =
        serviceDao.searchByName(keyword)

    // One-time read operations
    override suspend fun getByIdOnce(id: Long): ServiceEntity? =
        serviceDao.getByIdOnce(id)

    // Insert operations
    override suspend fun insert(service: ServiceEntity): Long =
        serviceDao.insert(service)

    // Upsert operations
    override suspend fun upsert(service: ServiceEntity): Long =
        serviceDao.upsert(service)

    // Update operations
    override suspend fun update(service: ServiceEntity): Int =
        serviceDao.update(service)

    override suspend fun updateActiveStatus(id: Long, isActive: Boolean): Int =
        serviceDao.updateActiveStatus(id, isActive)

    // Delete operations
    override suspend fun delete(service: ServiceEntity): Int =
        serviceDao.delete(service)

    override suspend fun deleteById(id: Long): Int =
        serviceDao.deleteById(id)
}
