package com.xeismonium.washcleaner.data.repository

import com.xeismonium.washcleaner.data.local.database.dao.CustomerDao
import com.xeismonium.washcleaner.data.local.database.entity.CustomerEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

interface CustomerRepository {
    // Query methods using Flow for reactive UI updates
    fun getAll(): Flow<List<CustomerEntity>>
    fun getById(id: Long): Flow<CustomerEntity?>
    fun searchByName(keyword: String): Flow<List<CustomerEntity>>

    // One-time read operations
    suspend fun getByIdOnce(id: Long): CustomerEntity?

    // Insert operations
    suspend fun insert(customer: CustomerEntity): Long

    // Upsert operations
    suspend fun upsert(customer: CustomerEntity): Long

    // Update operations
    suspend fun update(customer: CustomerEntity): Int

    // Delete operations
    suspend fun delete(customer: CustomerEntity): Int
    suspend fun deleteById(id: Long): Int
}

@Singleton
class CustomerRepositoryImpl @Inject constructor(
    private val customerDao: CustomerDao
) : CustomerRepository {

    // Query methods using Flow for reactive UI updates
    override fun getAll(): Flow<List<CustomerEntity>> =
        customerDao.getAll()

    override fun getById(id: Long): Flow<CustomerEntity?> =
        customerDao.getById(id)

    override fun searchByName(keyword: String): Flow<List<CustomerEntity>> =
        customerDao.searchByName(keyword)

    // One-time read operations
    override suspend fun getByIdOnce(id: Long): CustomerEntity? =
        customerDao.getByIdOnce(id)

    // Insert operations
    override suspend fun insert(customer: CustomerEntity): Long =
        customerDao.insert(customer)

    // Upsert operations
    override suspend fun upsert(customer: CustomerEntity): Long =
        customerDao.upsert(customer)

    // Update operations
    override suspend fun update(customer: CustomerEntity): Int =
        customerDao.update(customer)

    // Delete operations
    override suspend fun delete(customer: CustomerEntity): Int =
        customerDao.delete(customer)

    override suspend fun deleteById(id: Long): Int =
        customerDao.deleteById(id)
}
