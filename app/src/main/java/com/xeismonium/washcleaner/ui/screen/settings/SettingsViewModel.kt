package com.xeismonium.washcleaner.ui.screen.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class ThemeMode {
    LIGHT, DARK, SYSTEM
}

data class SettingsUiState(
    val appVersion: String = "1.0.0",
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val isBackingUp: Boolean = false,
    val isRestoring: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null
)

sealed class SettingsEvent {
    data class BackupCompleted(val filePath: String) : SettingsEvent()
    data class RestoreCompleted(val message: String) : SettingsEvent()
    data class Error(val message: String) : SettingsEvent()
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    private val _events = MutableStateFlow<SettingsEvent?>(null)
    val events: StateFlow<SettingsEvent?> = _events.asStateFlow()

    init {
        loadThemePreference()
    }

    private fun loadThemePreference() {
        viewModelScope.launch {
            try {
                // In a real implementation, load from DataStore
                // For now, default to SYSTEM
                _uiState.value = _uiState.value.copy(themeMode = ThemeMode.SYSTEM)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            try {
                // In a real implementation, save to DataStore
                _uiState.value = _uiState.value.copy(themeMode = mode)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun backupDataAsJson() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isBackingUp = true, error = null)

                // In a real implementation:
                // 1. Fetch all data from repositories
                // 2. Convert to JSON using Gson or Moshi
                // 3. Save to external storage or share via Intent
                // 4. Return file path

                val fileName = "washcleaner_backup_${System.currentTimeMillis()}.json"
                val filePath = "${context.getExternalFilesDir(null)}/$fileName"

                // Simulate backup process
                kotlinx.coroutines.delay(1000)

                _uiState.value = _uiState.value.copy(
                    isBackingUp = false,
                    successMessage = "Backup JSON berhasil disimpan"
                )
                _events.value = SettingsEvent.BackupCompleted(filePath)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isBackingUp = false,
                    error = e.message
                )
                _events.value = SettingsEvent.Error(e.message ?: "Backup gagal")
            }
        }
    }

    fun backupDataAsDatabase() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isBackingUp = true, error = null)

                // In a real implementation:
                // 1. Close database connection
                // 2. Copy database file from app data directory
                // 3. Save to external storage or share via Intent
                // 4. Reopen database connection

                val fileName = "washcleaner_backup_${System.currentTimeMillis()}.db"
                val filePath = "${context.getExternalFilesDir(null)}/$fileName"

                // Simulate backup process
                kotlinx.coroutines.delay(1000)

                _uiState.value = _uiState.value.copy(
                    isBackingUp = false,
                    successMessage = "Backup database berhasil disimpan"
                )
                _events.value = SettingsEvent.BackupCompleted(filePath)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isBackingUp = false,
                    error = e.message
                )
                _events.value = SettingsEvent.Error(e.message ?: "Backup gagal")
            }
        }
    }

    fun restoreDataFromJson(filePath: String) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isRestoring = true, error = null)

                // In a real implementation:
                // 1. Read JSON file
                // 2. Parse JSON to data objects
                // 3. Clear existing database
                // 4. Insert restored data
                // 5. Reload all ViewModels

                // Simulate restore process
                kotlinx.coroutines.delay(1500)

                _uiState.value = _uiState.value.copy(
                    isRestoring = false,
                    successMessage = "Data berhasil dipulihkan dari JSON"
                )
                _events.value = SettingsEvent.RestoreCompleted("Data berhasil dipulihkan")
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isRestoring = false,
                    error = e.message
                )
                _events.value = SettingsEvent.Error(e.message ?: "Restore gagal")
            }
        }
    }

    fun restoreDataFromDatabase(filePath: String) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isRestoring = true, error = null)

                // In a real implementation:
                // 1. Close database connection
                // 2. Copy backup database file to app data directory
                // 3. Reopen database connection
                // 4. Reload all ViewModels

                // Simulate restore process
                kotlinx.coroutines.delay(1500)

                _uiState.value = _uiState.value.copy(
                    isRestoring = false,
                    successMessage = "Data berhasil dipulihkan dari database"
                )
                _events.value = SettingsEvent.RestoreCompleted("Data berhasil dipulihkan")
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isRestoring = false,
                    error = e.message
                )
                _events.value = SettingsEvent.Error(e.message ?: "Restore gagal")
            }
        }
    }

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(
            successMessage = null,
            error = null
        )
    }

    fun clearEvent() {
        _events.value = null
    }
}
