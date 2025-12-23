package com.xeismonium.washcleaner.data.repository

import com.xeismonium.washcleaner.data.local.database.dao.TransactionDao
import com.xeismonium.washcleaner.data.local.database.dao.TransactionServiceDao
import com.xeismonium.washcleaner.data.local.database.entity.LaundryTransactionEntity
import com.xeismonium.washcleaner.data.local.database.entity.TransactionServiceEntity
import com.xeismonium.washcleaner.data.local.database.entity.TransactionWithFullDetails
import com.xeismonium.washcleaner.data.local.database.entity.TransactionWithServices
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

data class TransactionWithServicesData(
    val transaction: LaundryTransactionEntity,
    val services: List<ServiceItem>
) {
    data class ServiceItem(
        val serviceId: Long,
        val weightKg: Double,
        val subtotalPrice: Double
    )
}

interface TransactionRepository {
    // Query methods
    fun getAll(): Flow<List<LaundryTransactionEntity>>
    fun getById(id: Long): Flow<LaundryTransactionEntity?>
    suspend fun getByIdOnce(id: Long): LaundryTransactionEntity?
    fun getByCustomerId(customerId: Long): Flow<List<LaundryTransactionEntity>>
    fun getByStatus(status: String): Flow<List<LaundryTransactionEntity>>
    fun searchByCustomerName(keyword: String): Flow<List<LaundryTransactionEntity>>
    fun getByDateRange(startDate: Long, endDate: Long): Flow<List<LaundryTransactionEntity>>
    suspend fun getRecent(limit: Int = 10): List<LaundryTransactionEntity>

    // Statistics queries
    fun getCountByStatus(status: String): Flow<Int>
    fun getTotalPriceByStatus(status: String): Flow<Double?>
    fun getTotalPriceByDateRange(startDate: Long, endDate: Long): Flow<Double?>

    // Payment queries
    fun getUnpaidTransactions(): Flow<List<LaundryTransactionEntity>>
    fun getPartiallyPaidTransactions(): Flow<List<LaundryTransactionEntity>>
    fun getFullyPaidTransactions(): Flow<List<LaundryTransactionEntity>>
    fun getUnpaidCount(): Flow<Int>
    fun getPartiallyPaidCount(): Flow<Int>
    fun getFullyPaidCount(): Flow<Int>
    fun getTotalOutstandingAmount(): Flow<Double?>
    fun getTotalPaidAmount(): Flow<Double?>

    // Payment operations
    suspend fun updatePaymentAmount(id: Long, paidAmount: Double): Int
    suspend fun addPayment(id: Long, additionalAmount: Double): Int

    // Transaction with services relations
    fun getTransactionWithServices(id: Long): Flow<TransactionWithServices?>
    fun getAllTransactionsWithServices(): Flow<List<TransactionWithServices>>
    fun getTransactionWithFullDetails(id: Long): Flow<TransactionWithFullDetails?>
    fun getServicesForTransaction(transactionId: Long): Flow<List<TransactionServiceEntity>>

    // Insert/Update operations
    suspend fun insert(transaction: LaundryTransactionEntity): Long
    suspend fun upsert(transaction: LaundryTransactionEntity): Long
    suspend fun update(transaction: LaundryTransactionEntity): Int
    suspend fun updateStatus(id: Long, status: String): Int

    // Multi-service transaction operations
    suspend fun insertTransactionWithServices(data: TransactionWithServicesData): Long
    suspend fun updateTransactionWithServices(transactionId: Long, data: TransactionWithServicesData): Int
    suspend fun addServiceToTransaction(transactionService: TransactionServiceEntity): Long
    suspend fun removeServiceFromTransaction(transactionServiceId: Long): Int

    // Delete operations
    suspend fun delete(transaction: LaundryTransactionEntity): Int
    suspend fun deleteById(id: Long): Int
}

@Singleton
class TransactionRepositoryImpl @Inject constructor(
    private val transactionDao: TransactionDao,
    private val transactionServiceDao: TransactionServiceDao
) : TransactionRepository {

    // Query methods
    override fun getAll(): Flow<List<LaundryTransactionEntity>> =
        transactionDao.getAll()

    override fun getById(id: Long): Flow<LaundryTransactionEntity?> =
        transactionDao.getById(id)

    override suspend fun getByIdOnce(id: Long): LaundryTransactionEntity? =
        transactionDao.getByIdOnce(id)

    override fun getByCustomerId(customerId: Long): Flow<List<LaundryTransactionEntity>> =
        transactionDao.getByCustomerId(customerId)

    override fun getByStatus(status: String): Flow<List<LaundryTransactionEntity>> =
        transactionDao.getByStatus(status)

    override fun searchByCustomerName(keyword: String): Flow<List<LaundryTransactionEntity>> =
        transactionDao.searchByCustomerName(keyword)

    override fun getByDateRange(startDate: Long, endDate: Long): Flow<List<LaundryTransactionEntity>> =
        transactionDao.getByDateRange(startDate, endDate)

    override suspend fun getRecent(limit: Int): List<LaundryTransactionEntity> =
        transactionDao.getRecent(limit)

    // Statistics queries
    override fun getCountByStatus(status: String): Flow<Int> =
        transactionDao.getCountByStatus(status)

    override fun getTotalPriceByStatus(status: String): Flow<Double?> =
        transactionDao.getTotalPriceByStatus(status)

    override fun getTotalPriceByDateRange(startDate: Long, endDate: Long): Flow<Double?> =
        transactionDao.getTotalPriceByDateRange(startDate, endDate)

    // Payment queries
    override fun getUnpaidTransactions(): Flow<List<LaundryTransactionEntity>> =
        transactionDao.getUnpaidTransactions()

    override fun getPartiallyPaidTransactions(): Flow<List<LaundryTransactionEntity>> =
        transactionDao.getPartiallyPaidTransactions()

    override fun getFullyPaidTransactions(): Flow<List<LaundryTransactionEntity>> =
        transactionDao.getFullyPaidTransactions()

    override fun getUnpaidCount(): Flow<Int> =
        transactionDao.getUnpaidCount()

    override fun getPartiallyPaidCount(): Flow<Int> =
        transactionDao.getPartiallyPaidCount()

    override fun getFullyPaidCount(): Flow<Int> =
        transactionDao.getFullyPaidCount()

    override fun getTotalOutstandingAmount(): Flow<Double?> =
        transactionDao.getTotalOutstandingAmount()

    override fun getTotalPaidAmount(): Flow<Double?> =
        transactionDao.getTotalPaidAmount()

    // Payment operations
    override suspend fun updatePaymentAmount(id: Long, paidAmount: Double): Int =
        transactionDao.updatePaymentAmount(id, paidAmount)

    override suspend fun addPayment(id: Long, additionalAmount: Double): Int =
        transactionDao.addPayment(id, additionalAmount)

    // Transaction with services relations
    override fun getTransactionWithServices(id: Long): Flow<TransactionWithServices?> =
        transactionDao.getTransactionWithServices(id)

    override fun getAllTransactionsWithServices(): Flow<List<TransactionWithServices>> =
        transactionDao.getAllTransactionsWithServices()

    override fun getTransactionWithFullDetails(id: Long): Flow<TransactionWithFullDetails?> =
        transactionDao.getTransactionWithFullDetails(id)

    override fun getServicesForTransaction(transactionId: Long): Flow<List<TransactionServiceEntity>> =
        transactionServiceDao.getByTransactionId(transactionId)

    // Insert/Update operations
    override suspend fun insert(transaction: LaundryTransactionEntity): Long =
        transactionDao.insert(transaction)

    override suspend fun upsert(transaction: LaundryTransactionEntity): Long =
        transactionDao.upsert(transaction)

    override suspend fun update(transaction: LaundryTransactionEntity): Int =
        transactionDao.update(transaction)

    override suspend fun updateStatus(id: Long, status: String): Int =
        transactionDao.updateStatus(id, status)

    // Multi-service transaction operations
    override suspend fun insertTransactionWithServices(data: TransactionWithServicesData): Long {
        // Insert transaction first
        val transactionId = transactionDao.insert(data.transaction)

        // Then insert all services
        val transactionServices = data.services.map { service ->
            TransactionServiceEntity(
                transactionId = transactionId,
                serviceId = service.serviceId,
                weightKg = service.weightKg,
                subtotalPrice = service.subtotalPrice
            )
        }
        transactionServiceDao.insertAll(transactionServices)

        return transactionId
    }

    override suspend fun updateTransactionWithServices(
        transactionId: Long,
        data: TransactionWithServicesData
    ): Int {
        // Update transaction
        val result = transactionDao.update(data.transaction.copy(id = transactionId))

        // Delete existing services
        transactionServiceDao.deleteByTransactionId(transactionId)

        // Insert new services
        val transactionServices = data.services.map { service ->
            TransactionServiceEntity(
                transactionId = transactionId,
                serviceId = service.serviceId,
                weightKg = service.weightKg,
                subtotalPrice = service.subtotalPrice
            )
        }
        transactionServiceDao.insertAll(transactionServices)

        return result
    }

    override suspend fun addServiceToTransaction(transactionService: TransactionServiceEntity): Long =
        transactionServiceDao.insert(transactionService)

    override suspend fun removeServiceFromTransaction(transactionServiceId: Long): Int =
        transactionServiceDao.deleteById(transactionServiceId)

    // Delete operations
    override suspend fun delete(transaction: LaundryTransactionEntity): Int =
        transactionDao.delete(transaction)

    override suspend fun deleteById(id: Long): Int =
        transactionDao.deleteById(id)
}
