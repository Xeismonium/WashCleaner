package com.xeismonium.washcleaner.domain.model

enum class UserRole {
    OWNER, STAFF
}

data class User(
    val id: String = "",
    val email: String = "",
    val name: String = "",
    val role: UserRole = UserRole.STAFF,
    val isActive: Boolean = true
)
