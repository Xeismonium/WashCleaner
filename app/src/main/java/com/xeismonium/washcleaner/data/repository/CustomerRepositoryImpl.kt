package com.xeismonium.washcleaner.data.repository

import com.xeismonium.washcleaner.data.local.dao.CustomerDao
import com.xeismonium.washcleaner.data.local.entity.CustomerEntity
import com.xeismonium.washcleaner.data.remote.FirestoreDataSource
import com.xeismonium.washcleaner.domain.model.Customer
import com.xeismonium.washcleaner.domain.repository.CustomerRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CustomerRepositoryImpl @Inject constructor(
    private val customerDao: CustomerDao,
    private val firestoreDataSource: FirestoreDataSource
) : CustomerRepository {

    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    init {
        startSync()
    }

    private fun startSync() {
        firestoreDataSource.collectionListener("customers")
            .onEach { result ->
                result.onSuccess { documents ->
                    val entities = documents.map { doc ->
                        CustomerEntity(
                            id = doc.id,
                            name = doc.getString("name") ?: "",
                            phone = doc.getString("phone") ?: "",
                            address = doc.getString("address") ?: "",
                            createdAt = doc.getTimestamp("createdAt")?.toDate()?.time ?: 0L
                        )
                    }
                    customerDao.insertCustomers(entities)
                }
            }
            .launchIn(repositoryScope)
    }

    override fun getCustomers(): Flow<Result<List<Customer>>> {
        return customerDao.getCustomersFlow()
            .map { entities -> Result.success(entities.map { it.toDomain() }) }
            .catch { emit(Result.failure(it)) }
    }

    override suspend fun getCustomerById(id: String): Result<Customer?> = runCatching {
        customerDao.getCustomerById(id)?.toDomain()
    }

    override suspend fun searchCustomers(query: String): Result<List<Customer>> = runCatching {
        customerDao.searchCustomers(query).first().map { it.toDomain() }
    }

    override suspend fun upsertCustomer(customer: Customer): Result<Unit> = runCatching {
        val id = customer.id.ifBlank { UUID.randomUUID().toString() }
        val createdAt = if (customer.createdAt == 0L) System.currentTimeMillis() else customer.createdAt
        val updatedCustomer = customer.copy(id = id, createdAt = createdAt)
        
        // Write to local first
        customerDao.insertCustomer(CustomerEntity.fromDomain(updatedCustomer))
        
        // Write to remote
        val data = mapOf(
            "name" to updatedCustomer.name,
            "phone" to updatedCustomer.phone,
            "address" to updatedCustomer.address
            // createdAt and updatedAt are handled by FirestoreDataSource
        )
        firestoreDataSource.setDocument("customers", id, data).getOrThrow()
    }

    override suspend fun deleteCustomer(id: String): Result<Unit> = runCatching {
        val entity = customerDao.getCustomerById(id)
        if (entity != null) {
            customerDao.deleteCustomer(entity)
        }
        firestoreDataSource.deleteDocument("customers", id).getOrThrow()
    }
}
