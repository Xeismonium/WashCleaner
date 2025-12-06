package com.xeismonium.washcleaner.data.local.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "services",
    indices = [
        Index(value = ["name"]),
        Index(value = ["isActive"])
    ]
)
data class ServiceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val price: Int,
    val unit: String = "kg",
    val isActive: Boolean
)