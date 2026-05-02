package com.xeismonium.washcleaner.domain.repository

import com.xeismonium.washcleaner.domain.model.StoreSettings
import com.xeismonium.washcleaner.domain.model.User
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    suspend fun getStoreSettings(): Result<StoreSettings>
    suspend fun updateStoreSettings(settings: StoreSettings): Result<Unit>
    fun getStaff(): Flow<Result<List<User>>>
    suspend fun addStaff(user: User): Result<Unit>
}
