package com.xeismonium.washcleaner.domain.repository

import com.xeismonium.washcleaner.domain.model.Service
import kotlinx.coroutines.flow.Flow

interface ServiceRepository {
    fun getServices(): Flow<Result<List<Service>>>
    suspend fun upsertService(service: Service): Result<Unit>
    suspend fun deleteService(id: String): Result<Unit>
}
