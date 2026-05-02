package com.xeismonium.washcleaner.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xeismonium.washcleaner.domain.model.User
import com.xeismonium.washcleaner.domain.model.UserRole
import com.xeismonium.washcleaner.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

data class StaffUiState(
    val isLoading: Boolean = false,
    val staffList: List<User> = emptyList(),
    val error: String? = null,
    val isSuccess: Boolean = false
)

@HiltViewModel
class StaffViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StaffUiState())
    val uiState: StateFlow<StaffUiState> = _uiState.asStateFlow()

    init {
        loadStaff()
    }

    private fun loadStaff() {
        settingsRepository.getStaff()
            .onEach { result ->
                result.onSuccess { staff ->
                    _uiState.update { it.copy(staffList = staff) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(error = error.message) }
                }
            }
            .launchIn(viewModelScope)
    }

    fun addStaff(name: String, email: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val newUser = User(
                id = UUID.randomUUID().toString(),
                name = name,
                email = email,
                role = UserRole.STAFF,
                isActive = true
            )
            settingsRepository.addStaff(newUser)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                }
        }
    }

    fun toggleStaffActivation(user: User) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val updatedUser = user.copy(isActive = !user.isActive)
            settingsRepository.addStaff(updatedUser)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                }
        }
    }

    fun deleteStaff(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            settingsRepository.deleteStaff(id)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                }
        }
    }

    fun clearMessage() {
        _uiState.update { it.copy(error = null, isSuccess = false) }
    }
}
