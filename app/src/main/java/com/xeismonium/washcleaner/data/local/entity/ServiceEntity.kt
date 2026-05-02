package com.xeismonium.washcleaner.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.xeismonium.washcleaner.domain.model.Service
import com.xeismonium.washcleaner.domain.model.ServiceUnit

@Entity(tableName = "services")
data class ServiceEntity(
    @PrimaryKey val id: String,
    val name: String,
    val price: Double,
    val unit: ServiceUnit,
    val isActive: Boolean
) {
    fun toDomain(): Service = Service(
        id = id,
        name = name,
        price = price,
        unit = unit,
        isActive = isActive
    )

    companion object {
        fun fromDomain(service: Service): ServiceEntity = ServiceEntity(
            id = service.id,
            name = service.name,
            price = service.price,
            unit = service.unit,
            isActive = service.isActive
        )
    }
}
