package com.xeismonium.washcleaner.data.remote

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    suspend fun setDocument(
        collectionPath: String,
        documentId: String,
        data: Map<String, Any>
    ): Result<Unit> = runCatching {
        val finalData = data.toMutableMap()
        val now = FieldValue.serverTimestamp()
        finalData["createdAt"] = now
        finalData["updatedAt"] = now
        
        firestore.collection(collectionPath)
            .document(documentId)
            .set(finalData)
            .await()
    }

    suspend fun getDocument(
        collectionPath: String,
        documentId: String
    ): Result<DocumentSnapshot> = runCatching {
        firestore.collection(collectionPath)
            .document(documentId)
            .get()
            .await()
    }

    suspend fun updateDocument(
        collectionPath: String,
        documentId: String,
        data: Map<String, Any>
    ): Result<Unit> = runCatching {
        val finalData = data.toMutableMap()
        finalData["updatedAt"] = FieldValue.serverTimestamp()
        
        firestore.collection(collectionPath)
            .document(documentId)
            .update(finalData)
            .await()
    }

    suspend fun deleteDocument(
        collectionPath: String,
        documentId: String
    ): Result<Unit> = runCatching {
        firestore.collection(collectionPath)
            .document(documentId)
            .delete()
            .await()
    }

    fun collectionListener(
        collectionPath: String
    ): Flow<Result<List<DocumentSnapshot>>> = callbackFlow {
        val registration = firestore.collection(collectionPath)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.failure(error))
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    trySend(Result.success(snapshot.documents))
                }
            }
        awaitClose { registration.remove() }
    }

    suspend fun incrementAndGetCounter(
        collectionPath: String,
        documentId: String,
        field: String
    ): Result<Long> = runCatching {
        firestore.runTransaction { transaction ->
            val docRef = firestore.collection(collectionPath).document(documentId)
            val snapshot = transaction.get(docRef)
            val currentValue = snapshot.getLong(field) ?: 0L
            val newValue = currentValue + 1
            
            val updates = mapOf(
                field to newValue,
                "updatedAt" to FieldValue.serverTimestamp()
            )
            
            if (!snapshot.exists()) {
                transaction.set(docRef, updates + ("createdAt" to FieldValue.serverTimestamp()))
            } else {
                transaction.update(docRef, updates)
            }
            
            newValue
        }.await()
    }
}
