package com.xeismonium.washcleaner.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.xeismonium.washcleaner.domain.model.User
import com.xeismonium.washcleaner.domain.model.UserRole

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val email: String,
    val name: String,
    val role: UserRole,
    val isActive: Boolean
) {
    fun toDomain(): User = User(
        id = id,
        email = email,
        name = name,
        role = role,
        isActive = isActive
    )

    companion object {
        fun fromDomain(user: User): UserEntity = UserEntity(
            id = user.id,
            email = user.email,
            name = user.name,
            role = user.role,
            isActive = user.isActive
        )
    }
}
