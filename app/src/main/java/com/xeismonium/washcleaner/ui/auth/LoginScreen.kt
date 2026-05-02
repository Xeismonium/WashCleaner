package com.xeismonium.washcleaner.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun LoginScreen(
    onNavigateToMain: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState) {
        when (uiState) {
            is AuthUiState.Success -> onNavigateToMain()
            is AuthUiState.Error -> {
                snackbarHostState.showSnackbar((uiState as AuthUiState.Error).message)
                viewModel.resetState()
            }
            else -> {}
        }
    }

    LoginContent(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onLoginClick = { email, password ->
            viewModel.login(email, password)
        },
        onNavigateToRegister = onNavigateToRegister,
        onNavigateToForgotPassword = onNavigateToForgotPassword
    )
}

@Composable
fun LoginContent(
    uiState: AuthUiState,
    snackbarHostState: SnackbarHostState,
    onLoginClick: (String, String) -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Welcome Back",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            OutlinedTextField(
                value = email,
                onValueChange = { 
                    email = it
                    emailError = null
                },
                label = { Text("Email") },
                isError = emailError != null,
                supportingText = { emailError?.let { Text(it) } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = password,
                onValueChange = { 
                    password = it
                    passwordError = null
                },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                isError = passwordError != null,
                supportingText = { passwordError?.let { Text(it) } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            TextButton(
                onClick = onNavigateToForgotPassword,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Forgot Password?")
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = {
                    var isValid = true
                    if (email.isBlank()) {
                        emailError = "Email is required"
                        isValid = false
                    }
                    if (password.isBlank()) {
                        passwordError = "Password is required"
                        isValid = false
                    }
                    if (isValid) {
                        onLoginClick(email, password)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState !is AuthUiState.Loading
            ) {
                if (uiState is AuthUiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Login")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = onNavigateToRegister) {
                Text("Don't have an account? ")
                Text(
                    text = "Register",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginPreview() {
    MaterialTheme {
        LoginContent(
            uiState = AuthUiState.Idle,
            snackbarHostState = remember { SnackbarHostState() },
            onLoginClick = { _, _ -> },
            onNavigateToRegister = {},
            onNavigateToForgotPassword = {}
        )
    }
}
