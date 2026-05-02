package com.xeismonium.washcleaner.domain.model

data class Customer(
    val id: String = "",
    val name: String = "",
    val phone: String = "",
    val address: String = "",
    val createdAt: Long = 0L
)
