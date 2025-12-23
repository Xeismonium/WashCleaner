package com.xeismonium.washcleaner.data.local.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "transactions",
    indices = [
        Index(value = ["customerId"]),
        Index(value = ["status"]),
        Index(value = ["dateIn"]),
        Index(value = ["customerName"]),
        Index(value = ["paidAmount"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = CustomerEntity::class,
            parentColumns = ["id"],
            childColumns = ["customerId"],
            onDelete = ForeignKey.SET_NULL
        )
    ]
)
data class LaundryTransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val customerId: Long?,
    val customerName: String?,

    val totalPrice: Double,

    val dateIn: Long,
    val dateOut: Long?,
    val estimatedDate: Long?,

    val status: String,
    val paidAmount: Double = 0.0
) {
    // Computed property for backward compatibility
    val isPaid: Boolean
        get() = paidAmount >= totalPrice
}