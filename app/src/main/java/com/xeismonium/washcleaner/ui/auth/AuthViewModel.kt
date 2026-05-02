package com.xeismonium.washcleaner.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xeismonium.washcleaner.domain.model.User
import com.xeismonium.washcleaner.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class Success(val user: User) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
    object Unauthenticated : AuthUiState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun checkLoginStatus() {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val result = authRepository.getCurrentUser()
            result.onSuccess { user ->
                if (user != null) {
                    if (user.isActive) {
                        _uiState.value = AuthUiState.Success(user)
                    } else {
                        // User is not active, force logout
                        authRepository.logout()
                        _uiState.value = AuthUiState.Error("Your account has been deactivated.")
                    }
                } else {
                    _uiState.value = AuthUiState.Unauthenticated
                }
            }.onFailure { exception ->
                _uiState.value = AuthUiState.Error(exception.message ?: "Failed to check login status")
            }
        }
    }

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = AuthUiState.Error("Email and password cannot be empty")
            return
        }

        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val result = authRepository.login(email, password)
            result.onSuccess { user ->
                if (user.isActive) {
                    _uiState.value = AuthUiState.Success(user)
                } else {
                    authRepository.logout()
                    _uiState.value = AuthUiState.Error("Your account is inactive.")
                }
            }.onFailure { exception ->
                _uiState.value = AuthUiState.Error(exception.message ?: "Login failed")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _uiState.value = AuthUiState.Unauthenticated
        }
    }
    
    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }
}
