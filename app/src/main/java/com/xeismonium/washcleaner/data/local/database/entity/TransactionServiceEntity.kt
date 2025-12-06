package com.xeismonium.washcleaner.data.local.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "transaction_services",
    indices = [
        Index(value = ["transactionId"]),
        Index(value = ["serviceId"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = LaundryTransactionEntity::class,
            parentColumns = ["id"],
            childColumns = ["transactionId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ServiceEntity::class,
            parentColumns = ["id"],
            childColumns = ["serviceId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class TransactionServiceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val transactionId: Long,
    val serviceId: Long,
    val weightKg: Double,
    val subtotalPrice: Double
)
