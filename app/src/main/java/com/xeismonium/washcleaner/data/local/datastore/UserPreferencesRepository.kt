package com.xeismonium.washcleaner.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.xeismonium.washcleaner.ui.screen.settings.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserPreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private object PreferencesKeys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val LAST_SCREEN_ROUTE = stringPreferencesKey("last_screen_route")
    }

    val themeMode: Flow<ThemeMode> = dataStore.data
        .map { preferences ->
            val themeName = preferences[PreferencesKeys.THEME_MODE] ?: ThemeMode.SYSTEM.name
            try {
                ThemeMode.valueOf(themeName)
            } catch (e: IllegalArgumentException) {
                ThemeMode.SYSTEM
            }
        }

    val lastScreenRoute: Flow<String?> = dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.LAST_SCREEN_ROUTE]
        }

    suspend fun setThemeMode(themeMode: ThemeMode) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME_MODE] = themeMode.name
        }
    }

    suspend fun setLastScreenRoute(route: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.LAST_SCREEN_ROUTE] = route
        }
    }
}
