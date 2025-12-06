package com.xeismonium.washcleaner.ui.screen.service

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xeismonium.washcleaner.data.local.database.entity.ServiceEntity
import com.xeismonium.washcleaner.data.repository.ServiceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ServiceUiState(
    val services: List<ServiceEntity> = emptyList(),
    val selectedService: ServiceEntity? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = ""
)

sealed class ServiceEvent {
    object Success : ServiceEvent()
    data class Error(val message: String) : ServiceEvent()
}

@HiltViewModel
class ServiceViewModel @Inject constructor(
    private val serviceRepository: ServiceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ServiceUiState())
    val uiState: StateFlow<ServiceUiState> = _uiState.asStateFlow()

    private val _events = MutableStateFlow<ServiceEvent?>(null)
    val events: StateFlow<ServiceEvent?> = _events.asStateFlow()

    init {
        loadServices()
    }

    private fun loadServices() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            serviceRepository.getAll()
                .catch { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
                .collect { services ->
                    val filtered = if (_uiState.value.searchQuery.isBlank()) {
                        services
                    } else {
                        services.filter {
                            it.name.contains(_uiState.value.searchQuery, ignoreCase = true)
                        }
                    }

                    _uiState.value = _uiState.value.copy(
                        services = filtered,
                        isLoading = false,
                        error = null
                    )
                }
        }
    }

    fun setSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        loadServices()
    }

    fun loadServiceById(id: Long) {
        viewModelScope.launch {
            serviceRepository.getById(id)
                .catch { e ->
                    _uiState.value = _uiState.value.copy(error = e.message)
                }
                .collect { service ->
                    _uiState.value = _uiState.value.copy(selectedService = service)
                }
        }
    }

    fun addService(name: String, price: Int, unit: String) {
        viewModelScope.launch {
            try {
                val service = ServiceEntity(
                    name = name,
                    price = price,
                    unit = unit,
                    isActive = true
                )
                serviceRepository.insert(service)
                _events.value = ServiceEvent.Success
            } catch (e: Exception) {
                _events.value = ServiceEvent.Error(e.message ?: "Unknown error")
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun updateService(id: Long, name: String, price: Int, unit: String, isActive: Boolean) {
        viewModelScope.launch {
            try {
                val service = ServiceEntity(
                    id = id,
                    name = name,
                    price = price,
                    unit = unit,
                    isActive = isActive
                )
                serviceRepository.update(service)
                _events.value = ServiceEvent.Success
            } catch (e: Exception) {
                _events.value = ServiceEvent.Error(e.message ?: "Unknown error")
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun toggleServiceStatus(service: ServiceEntity) {
        viewModelScope.launch {
            try {
                serviceRepository.updateActiveStatus(service.id, !service.isActive)
                _events.value = ServiceEvent.Success
            } catch (e: Exception) {
                _events.value = ServiceEvent.Error(e.message ?: "Unknown error")
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun deleteService(id: Long) {
        viewModelScope.launch {
            try {
                serviceRepository.deleteById(id)
                _events.value = ServiceEvent.Success
            } catch (e: Exception) {
                _events.value = ServiceEvent.Error(e.message ?: "Unknown error")
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun clearEvent() {
        _events.value = null
    }

    fun refresh() {
        loadServices()
    }
}
