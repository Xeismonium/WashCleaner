package com.xeismonium.washcleaner.ui.auth

import app.cash.turbine.test
import com.xeismonium.washcleaner.MainDispatcherRule
import com.xeismonium.washcleaner.domain.model.User
import com.xeismonium.washcleaner.domain.model.UserRole
import com.xeismonium.washcleaner.domain.repository.AuthRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
class AuthViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: AuthViewModel
    private val authRepository = mockk<AuthRepository>()

    private val testUser = User(
        id = "1",
        email = "test@example.com",
        name = "Test User",
        role = UserRole.OWNER,
        isActive = true
    )

    @Before
    fun setup() {
        viewModel = AuthViewModel(authRepository)
    }

    @Test
    fun `checkLoginStatus success when user is active`() = runTest {
        coEvery { authRepository.getCurrentUser() } returns Result.success(testUser)

        viewModel.checkLoginStatus()

        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is AuthUiState.Success)
            assertEquals(testUser, (state as AuthUiState.Success).user)
        }
    }

    @Test
    fun `checkLoginStatus unauthenticated when user is null`() = runTest {
        coEvery { authRepository.getCurrentUser() } returns Result.success(null)

        viewModel.checkLoginStatus()

        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is AuthUiState.Unauthenticated)
        }
    }

    @Test
    fun `login with valid credentials success`() = runTest {
        coEvery { authRepository.login("test@example.com", "password") } returns Result.success(testUser)

        viewModel.login("test@example.com", "password")

        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is AuthUiState.Success)
            assertEquals(testUser, (state as AuthUiState.Success).user)
        }
    }

    @Test
    fun `login with invalid email returns error`() = runTest {
        viewModel.login("invalid-email", "password")

        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is AuthUiState.Error)
            assertEquals("Invalid email format", (state as AuthUiState.Error).message)
        }
    }

    @Test
    fun `register success`() = runTest {
        coEvery { authRepository.register("test@example.com", "password", "Test User") } returns Result.success(testUser)

        viewModel.register("test@example.com", "password", "Test User")

        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is AuthUiState.RegisterSuccess)
        }
    }

    @Test
    fun `resetPassword success`() = runTest {
        coEvery { authRepository.sendPasswordResetEmail("test@example.com") } returns Result.success(Unit)

        viewModel.resetPassword("test@example.com")

        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is AuthUiState.PasswordResetSent)
        }
    }
}
