package com.xeismonium.washcleaner.data.repository

import com.xeismonium.washcleaner.data.local.dao.UserDao
import com.xeismonium.washcleaner.data.local.entity.UserEntity
import com.xeismonium.washcleaner.data.remote.FirestoreDataSource
import com.xeismonium.washcleaner.domain.model.StoreSettings
import com.xeismonium.washcleaner.domain.model.User
import com.xeismonium.washcleaner.domain.model.UserRole
import com.xeismonium.washcleaner.domain.repository.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val firestoreDataSource: FirestoreDataSource
) : SettingsRepository {

    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    init {
        startSync()
    }

    private fun startSync() {
        firestoreDataSource.collectionListener("users")
            .onEach { result ->
                result.onSuccess { documents ->
                    val entities = documents.map { doc ->
                        UserEntity(
                            id = doc.id,
                            email = doc.getString("email") ?: "",
                            name = doc.getString("name") ?: "",
                            role = UserRole.valueOf(doc.getString("role") ?: "STAFF"),
                            isActive = doc.getBoolean("isActive") ?: true
                        )
                    }
                    userDao.insertUsers(entities)
                }
            }
            .launchIn(repositoryScope)
    }

    override suspend fun getStoreSettings(): Result<StoreSettings> = runCatching {
        val doc = firestoreDataSource.getDocument("settings", "store").getOrThrow()
        StoreSettings(
            storeName = doc.getString("storeName") ?: "",
            address = doc.getString("address") ?: ""
        )
    }

    override suspend fun updateStoreSettings(settings: StoreSettings): Result<Unit> = runCatching {
        val data = mapOf(
            "storeName" to settings.storeName,
            "address" to settings.address
        )
        firestoreDataSource.setDocument("settings", "store", data).getOrThrow()
    }

    override fun getStaff(): Flow<Result<List<User>>> {
        return userDao.getUsersByRole(UserRole.STAFF)
            .map { entities -> Result.success(entities.map { it.toDomain() }) }
            .catch { emit(Result.failure(it)) }
    }

    override suspend fun addStaff(user: User): Result<Unit> = runCatching {
        userDao.insertUser(UserEntity.fromDomain(user))
        
        val data = mapOf(
            "email" to user.email,
            "name" to user.name,
            "role" to user.role.name,
            "isActive" to user.isActive
        )
        firestoreDataSource.setDocument("users", user.id, data).getOrThrow()
    }

    override suspend fun deleteStaff(id: String): Result<Unit> = runCatching {
        userDao.deleteUser(id)
        firestoreDataSource.deleteDocument("users", id).getOrThrow()
    }
}
