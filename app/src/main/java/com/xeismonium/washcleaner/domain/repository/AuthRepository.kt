package com.xeismonium.washcleaner.domain.repository

import com.xeismonium.washcleaner.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<User>
    suspend fun register(email: String, password: String, name: String): Result<User>
    suspend fun logout(): Result<Unit>
    suspend fun getCurrentUser(): Result<User?>
    suspend fun sendPasswordResetEmail(email: String): Result<Unit>
    fun getSessionFlow(): Flow<User?>
}
