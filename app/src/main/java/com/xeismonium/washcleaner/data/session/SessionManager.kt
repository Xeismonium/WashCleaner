package com.xeismonium.washcleaner.data.session

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.xeismonium.washcleaner.domain.model.UserRole
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "session_prefs")

@Singleton
class SessionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.dataStore

    companion object {
        private val USER_ID = stringPreferencesKey("user_id")
        private val NAME = stringPreferencesKey("name")
        private val EMAIL = stringPreferencesKey("email")
        private val ROLE = stringPreferencesKey("role")
    }

    data class SessionData(
        val userId: String,
        val name: String,
        val email: String,
        val role: UserRole
    )

    val sessionFlow: Flow<SessionData?> = dataStore.data.map { preferences ->
        val userId = preferences[USER_ID]
        if (userId == null) {
            null
        } else {
            SessionData(
                userId = userId,
                name = preferences[NAME] ?: "",
                email = preferences[EMAIL] ?: "",
                role = UserRole.valueOf(preferences[ROLE] ?: UserRole.STAFF.name)
            )
        }
    }

    suspend fun saveSession(userId: String, name: String, email: String, role: UserRole) {
        dataStore.edit { preferences ->
            preferences[USER_ID] = userId
            preferences[NAME] = name
            preferences[EMAIL] = email
            preferences[ROLE] = role.name
        }
    }

    suspend fun clearSession() {
        dataStore.edit { it.clear() }
    }

    suspend fun isLoggedIn(): Boolean {
        return sessionFlow.first() != null
    }
}
