package com.xeismonium.washcleaner.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.xeismonium.washcleaner.data.local.dao.UserDao
import com.xeismonium.washcleaner.data.local.entity.UserEntity
import com.xeismonium.washcleaner.data.remote.FirestoreDataSource
import com.xeismonium.washcleaner.domain.model.User
import com.xeismonium.washcleaner.domain.model.UserRole
import com.xeismonium.washcleaner.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val userDao: UserDao,
    private val firestoreDataSource: FirestoreDataSource
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<User> = runCatching {
        val authResult = auth.signInWithEmailAndPassword(email, password).await()
        val firebaseUser = authResult.user ?: throw Exception("Login failed: User is null")
        
        val userDoc = firestoreDataSource.getDocument("users", firebaseUser.uid).getOrThrow()
        
        val user = User(
            id = firebaseUser.uid,
            email = firebaseUser.email ?: "",
            name = userDoc.getString("name") ?: "",
            role = UserRole.valueOf(userDoc.getString("role") ?: "STAFF"),
            isActive = userDoc.getBoolean("isActive") ?: true
        )
        
        userDao.insertUser(UserEntity.fromDomain(user))
        user
    }

    override suspend fun register(email: String, password: String, name: String): Result<User> = runCatching {
        val authResult = auth.createUserWithEmailAndPassword(email, password).await()
        val firebaseUser = authResult.user ?: throw Exception("Registration failed: User is null")
        
        val user = User(
            id = firebaseUser.uid,
            email = email,
            name = name,
            role = UserRole.OWNER, // D-01: First user is owner
            isActive = true
        )
        
        val userData = mapOf(
            "name" to name,
            "email" to email,
            "role" to user.role.name,
            "isActive" to user.isActive
        )
        
        firestoreDataSource.setDocument("users", firebaseUser.uid, userData).getOrThrow()
        userDao.insertUser(UserEntity.fromDomain(user))
        user
    }

    override suspend fun logout(): Result<Unit> = runCatching {
        auth.signOut()
        userDao.deleteAllUsers()
    }

    override suspend fun getCurrentUser(): Result<User?> = runCatching {
        val uid = auth.currentUser?.uid ?: return@runCatching null
        userDao.getUserById(uid)?.toDomain()
    }

    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> = runCatching {
        auth.sendPasswordResetEmail(email).await()
    }

    override fun getSessionFlow(): Flow<User?> {
        return userDao.getCurrentUserFlow().map { it?.toDomain() }
    }
}
