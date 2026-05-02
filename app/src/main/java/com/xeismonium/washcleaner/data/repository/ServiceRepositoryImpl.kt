package com.xeismonium.washcleaner.data.repository

import com.xeismonium.washcleaner.data.local.dao.ServiceDao
import com.xeismonium.washcleaner.data.local.entity.ServiceEntity
import com.xeismonium.washcleaner.data.remote.FirestoreDataSource
import com.xeismonium.washcleaner.domain.model.Service
import com.xeismonium.washcleaner.domain.model.ServiceUnit
import com.xeismonium.washcleaner.domain.repository.ServiceRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ServiceRepositoryImpl @Inject constructor(
    private val serviceDao: ServiceDao,
    private val firestoreDataSource: FirestoreDataSource
) : ServiceRepository {

    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    init {
        startSync()
    }

    private fun startSync() {
        firestoreDataSource.collectionListener("services")
            .onEach { result ->
                result.onSuccess { documents ->
                    val entities = documents.map { doc ->
                        ServiceEntity(
                            id = doc.id,
                            name = doc.getString("name") ?: "",
                            price = doc.getDouble("price") ?: 0.0,
                            unit = ServiceUnit.valueOf(doc.getString("unit") ?: "KG"),
                            isActive = doc.getBoolean("isActive") ?: true
                        )
                    }
                    serviceDao.insertServices(entities)
                }
            }
            .launchIn(repositoryScope)
    }

    override fun getServices(): Flow<Result<List<Service>>> {
        return serviceDao.getServicesFlow()
            .map { entities -> Result.success(entities.map { it.toDomain() }) }
            .catch { emit(Result.failure(it)) }
    }

    override suspend fun upsertService(service: Service): Result<Unit> = runCatching {
        val id = service.id.ifBlank { UUID.randomUUID().toString() }
        val updatedService = service.copy(id = id)
        
        serviceDao.insertService(ServiceEntity.fromDomain(updatedService))
        
        val data = mapOf(
            "name" to updatedService.name,
            "price" to updatedService.price,
            "unit" to updatedService.unit.name,
            "isActive" to updatedService.isActive
        )
        firestoreDataSource.setDocument("services", id, data).getOrThrow()
    }

    override suspend fun deleteService(id: String): Result<Unit> = runCatching {
        val entity = serviceDao.getServiceById(id)
        if (entity != null) {
            serviceDao.deleteService(entity)
        }
        firestoreDataSource.deleteDocument("services", id).getOrThrow()
    }
}
