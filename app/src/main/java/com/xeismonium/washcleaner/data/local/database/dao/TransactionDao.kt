package com.xeismonium.washcleaner.data.local.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import com.xeismonium.washcleaner.data.local.database.entity.LaundryTransactionEntity
import com.xeismonium.washcleaner.data.local.database.entity.TransactionWithFullDetails
import com.xeismonium.washcleaner.data.local.database.entity.TransactionWithServices
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    // Query methods
    @Query("SELECT * FROM transactions ORDER BY dateIn DESC")
    fun getAll(): Flow<List<LaundryTransactionEntity>>

    @Query("SELECT * FROM transactions WHERE id = :id")
    fun getById(id: Long): Flow<LaundryTransactionEntity?>

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getByIdOnce(id: Long): LaundryTransactionEntity?

    @Query("SELECT * FROM transactions WHERE customerId = :customerId ORDER BY dateIn DESC")
    fun getByCustomerId(customerId: Long): Flow<List<LaundryTransactionEntity>>

    @Query("SELECT * FROM transactions WHERE status = :status ORDER BY dateIn DESC")
    fun getByStatus(status: String): Flow<List<LaundryTransactionEntity>>

    @Query("SELECT * FROM transactions WHERE customerName LIKE '%' || :keyword || '%' ORDER BY dateIn DESC")
    fun searchByCustomerName(keyword: String): Flow<List<LaundryTransactionEntity>>

    @Query("SELECT * FROM transactions WHERE dateIn BETWEEN :startDate AND :endDate ORDER BY dateIn DESC")
    fun getByDateRange(startDate: Long, endDate: Long): Flow<List<LaundryTransactionEntity>>

    @Query("SELECT * FROM transactions ORDER BY dateIn DESC LIMIT :limit")
    suspend fun getRecent(limit: Int = 10): List<LaundryTransactionEntity>

    // Statistics queries for dashboard and reports
    @Query("SELECT COUNT(*) FROM transactions WHERE status = :status")
    fun getCountByStatus(status: String): Flow<Int>

    @Query("SELECT SUM(totalPrice) FROM transactions WHERE status = :status")
    fun getTotalPriceByStatus(status: String): Flow<Double?>

    @Query("SELECT SUM(totalPrice) FROM transactions WHERE dateIn BETWEEN :startDate AND :endDate")
    fun getTotalPriceByDateRange(startDate: Long, endDate: Long): Flow<Double?>

    // Transaction with relations
    @Transaction
    @Query("SELECT * FROM transactions WHERE id = :id")
    fun getTransactionWithServices(id: Long): Flow<TransactionWithServices?>

    @Transaction
    @Query("SELECT * FROM transactions ORDER BY dateIn DESC")
    fun getAllTransactionsWithServices(): Flow<List<TransactionWithServices>>

    @Transaction
    @Query("SELECT * FROM transactions WHERE id = :id")
    fun getTransactionWithFullDetails(id: Long): Flow<TransactionWithFullDetails?>

    // Insert/Update operations
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(transaction: LaundryTransactionEntity): Long

    @Upsert
    suspend fun upsert(transaction: LaundryTransactionEntity): Long

    @Update
    suspend fun update(transaction: LaundryTransactionEntity): Int

    @Query("UPDATE transactions SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Long, status: String): Int

    // Payment queries
    @Query("SELECT * FROM transactions WHERE paidAmount = 0 ORDER BY dateIn DESC")
    fun getUnpaidTransactions(): Flow<List<LaundryTransactionEntity>>

    @Query("SELECT * FROM transactions WHERE paidAmount > 0 AND paidAmount < totalPrice ORDER BY dateIn DESC")
    fun getPartiallyPaidTransactions(): Flow<List<LaundryTransactionEntity>>

    @Query("SELECT * FROM transactions WHERE paidAmount >= totalPrice ORDER BY dateIn DESC")
    fun getFullyPaidTransactions(): Flow<List<LaundryTransactionEntity>>

    @Query("SELECT COUNT(*) FROM transactions WHERE paidAmount = 0")
    fun getUnpaidCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM transactions WHERE paidAmount > 0 AND paidAmount < totalPrice")
    fun getPartiallyPaidCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM transactions WHERE paidAmount >= totalPrice")
    fun getFullyPaidCount(): Flow<Int>

    @Query("SELECT SUM(totalPrice - paidAmount) FROM transactions WHERE paidAmount < totalPrice")
    fun getTotalOutstandingAmount(): Flow<Double?>

    @Query("SELECT SUM(paidAmount) FROM transactions")
    fun getTotalPaidAmount(): Flow<Double?>

    @Query("UPDATE transactions SET paidAmount = :paidAmount WHERE id = :id")
    suspend fun updatePaymentAmount(id: Long, paidAmount: Double): Int

    @Query("UPDATE transactions SET paidAmount = paidAmount + :additionalAmount WHERE id = :id")
    suspend fun addPayment(id: Long, additionalAmount: Double): Int

    // Delete operations
    @Delete
    suspend fun delete(transaction: LaundryTransactionEntity): Int

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteById(id: Long): Int
}
