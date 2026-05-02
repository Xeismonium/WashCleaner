package com.xeismonium.washcleaner.domain.repository

import com.xeismonium.washcleaner.domain.model.Customer
import kotlinx.coroutines.flow.Flow

interface CustomerRepository {
    fun getCustomers(): Flow<Result<List<Customer>>>
    suspend fun getCustomerById(id: String): Result<Customer?>
    suspend fun searchCustomers(query: String): Result<List<Customer>>
    suspend fun upsertCustomer(customer: Customer): Result<Unit>
}
