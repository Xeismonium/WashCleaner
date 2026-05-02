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
    object RegisterSuccess : AuthUiState()
    object PasswordResetSent : AuthUiState()
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
        if (!isValidEmail(email)) {
            _uiState.value = AuthUiState.Error("Invalid email format")
            return
        }
        if (password.isBlank()) {
            _uiState.value = AuthUiState.Error("Password cannot be empty")
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

    fun register(email: String, password: String, name: String) {
        if (!isValidEmail(email)) {
            _uiState.value = AuthUiState.Error("Invalid email format")
            return
        }
        if (password.length < 6) {
            _uiState.value = AuthUiState.Error("Password must be at least 6 characters")
            return
        }
        if (name.isBlank()) {
            _uiState.value = AuthUiState.Error("Name cannot be empty")
            return
        }

        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val result = authRepository.register(email, password, name)
            result.onSuccess {
                _uiState.value = AuthUiState.RegisterSuccess
            }.onFailure { exception ->
                _uiState.value = AuthUiState.Error(exception.message ?: "Registration failed")
            }
        }
    }

    fun resetPassword(email: String) {
        if (!isValidEmail(email)) {
            _uiState.value = AuthUiState.Error("Invalid email format")
            return
        }

        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val result = authRepository.sendPasswordResetEmail(email)
            result.onSuccess {
                _uiState.value = AuthUiState.PasswordResetSent
            }.onFailure { exception ->
                _uiState.value = AuthUiState.Error(exception.message ?: "Failed to send reset email")
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
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
