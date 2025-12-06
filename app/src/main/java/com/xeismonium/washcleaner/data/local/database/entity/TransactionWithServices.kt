package com.xeismonium.washcleaner.data.local.database.entity

import androidx.room.Embedded
import androidx.room.Relation

data class TransactionWithServices(
    @Embedded val transaction: LaundryTransactionEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "transactionId"
    )
    val transactionServices: List<TransactionServiceEntity>
)

data class TransactionServiceWithDetails(
    @Embedded val transactionService: TransactionServiceEntity,
    @Relation(
        parentColumn = "serviceId",
        entityColumn = "id"
    )
    val service: ServiceEntity?
)

data class TransactionWithFullDetails(
    @Embedded val transaction: LaundryTransactionEntity,
    @Relation(
        parentColumn = "customerId",
        entityColumn = "id"
    )
    val customer: CustomerEntity?,
    @Relation(
        parentColumn = "id",
        entityColumn = "transactionId"
    )
    val transactionServices: List<TransactionServiceEntity>
)
