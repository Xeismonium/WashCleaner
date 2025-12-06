package com.xeismonium.washcleaner.data.local.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.xeismonium.washcleaner.data.local.database.entity.TransactionServiceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionServiceDao {
    // Essential queries
    @Query("SELECT * FROM transaction_services WHERE transactionId = :transactionId")
    fun getByTransactionId(transactionId: Long): Flow<List<TransactionServiceEntity>>

    @Query("SELECT * FROM transaction_services WHERE id = :id")
    fun getById(id: Long): Flow<TransactionServiceEntity?>

    @Query("SELECT * FROM transaction_services WHERE id = :id")
    suspend fun getByIdOnce(id: Long): TransactionServiceEntity?

    // Insert operations
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(transactionService: TransactionServiceEntity): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAll(transactionServices: List<TransactionServiceEntity>): List<Long>

    // Update operations
    @Update
    suspend fun update(transactionService: TransactionServiceEntity): Int

    @Update
    suspend fun updateAll(transactionServices: List<TransactionServiceEntity>): Int

    // Delete operations
    @Delete
    suspend fun delete(transactionService: TransactionServiceEntity): Int

    @Query("DELETE FROM transaction_services WHERE transactionId = :transactionId")
    suspend fun deleteByTransactionId(transactionId: Long): Int

    @Query("DELETE FROM transaction_services WHERE id = :id")
    suspend fun deleteById(id: Long): Int
}
