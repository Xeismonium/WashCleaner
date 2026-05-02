package com.xeismonium.washcleaner.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.xeismonium.washcleaner.domain.model.Customer

@Entity(tableName = "customers")
data class CustomerEntity(
    @PrimaryKey val id: String,
    val name: String,
    val phone: String,
    val address: String,
    val createdAt: Long
) {
    fun toDomain(): Customer = Customer(
        id = id,
        name = name,
        phone = phone,
        address = address,
        createdAt = createdAt
    )

    companion object {
        fun fromDomain(customer: Customer): CustomerEntity = CustomerEntity(
            id = customer.id,
            name = customer.name,
            phone = customer.phone,
            address = customer.address,
            createdAt = customer.createdAt
        )
    }
}
