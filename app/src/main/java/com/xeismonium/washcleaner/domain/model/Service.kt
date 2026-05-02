package com.xeismonium.washcleaner.domain.model

enum class ServiceUnit {
    KG, PCS
}

data class Service(
    val id: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val unit: ServiceUnit = ServiceUnit.KG,
    val isActive: Boolean = true
)
