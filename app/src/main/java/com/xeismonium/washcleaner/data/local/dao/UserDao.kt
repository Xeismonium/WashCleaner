package com.xeismonium.washcleaner.data.local.dao

import androidx.room.*
import com.xeismonium.washcleaner.data.local.entity.UserEntity
import com.xeismonium.washcleaner.domain.model.UserRole
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getUserById(id: String): UserEntity?

    @Query("SELECT * FROM users LIMIT 1")
    fun getCurrentUserFlow(): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE role = :role ORDER BY name ASC")
    fun getUsersByRole(role: UserRole): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserEntity>)

    @Query("DELETE FROM users WHERE id = :id")
    suspend fun deleteUser(id: String)

    @Query("DELETE FROM users")
    suspend fun deleteAllUsers()
}
