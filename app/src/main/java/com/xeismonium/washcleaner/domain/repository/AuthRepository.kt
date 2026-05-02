package com.xeismonium.washcleaner.domain.repository

import com.xeismonium.washcleaner.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<User>
    suspend fun logout(): Result<Unit>
    suspend fun getCurrentUser(): Result<User?>
    fun getSessionFlow(): Flow<User?>
}
