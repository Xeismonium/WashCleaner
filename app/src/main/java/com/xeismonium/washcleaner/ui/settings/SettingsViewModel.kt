package com.xeismonium.washcleaner.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xeismonium.washcleaner.domain.model.Service
import com.xeismonium.washcleaner.domain.model.StoreSettings
import com.xeismonium.washcleaner.domain.repository.AuthRepository
import com.xeismonium.washcleaner.domain.repository.ServiceRepository
import com.xeismonium.washcleaner.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.*
import javax.inject.Inject

data class SettingsUiState(
    val isLoading: Boolean = false,
    val storeSettings: StoreSettings = StoreSettings(),
    val services: List<Service> = emptyList(),
    val error: String? = null,
    val isSuccess: Boolean = false,
    val isLoggedOut: Boolean = false
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val serviceRepository: ServiceRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    private val rupiahFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID"))

    init {
        loadSettings()
        loadServices()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            settingsRepository.getStoreSettings()
                .onSuccess { settings ->
                    _uiState.update { it.copy(isLoading = false, storeSettings = settings) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                }
        }
    }

    private fun loadServices() {
        serviceRepository.getServices()
            .onEach { result ->
                result.onSuccess { services ->
                    _uiState.update { it.copy(services = services) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(error = error.message) }
                }
            }
            .launchIn(viewModelScope)
    }

    fun updateStoreSettings(name: String, address: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val newSettings = StoreSettings(storeName = name, address = address)
            settingsRepository.updateStoreSettings(newSettings)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, storeSettings = newSettings, isSuccess = true) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                }
        }
    }

    fun upsertService(name: String, price: Double, id: String? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val service = Service(
                id = id ?: UUID.randomUUID().toString(),
                name = name,
                price = price
            )
            serviceRepository.upsertService(service)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                }
        }
    }

    fun deleteService(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            serviceRepository.deleteService(id)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.logout()
                .onSuccess {
                    _uiState.update { it.copy(isLoggedOut = true) }
                }
        }
    }

    fun formatPrice(price: Double): String {
        return rupiahFormat.format(price)
    }

    fun clearMessage() {
        _uiState.update { it.copy(error = null, isSuccess = false) }
    }
}
