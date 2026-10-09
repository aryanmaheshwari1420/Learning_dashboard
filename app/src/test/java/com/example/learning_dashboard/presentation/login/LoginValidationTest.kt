package com.example.learning_dashboard.presentation.login

import com.example.learning_dashboard.domain.model.User
import com.example.learning_dashboard.domain.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginValidationTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeAuthRepository: FakeAuthRepository
    private lateinit var viewModel: LoginViewModel

    private class FakeAuthRepository : AuthRepository {
        var shouldSucceed = true
        private val userFlow = MutableStateFlow<User?>(null)

        override suspend fun login(email: String, password: String): Result<User> {
            return if (shouldSucceed) {
                val user = User(email, "mock_token")
                userFlow.value = user
                Result.success(user)
            } else {
                Result.failure(IllegalArgumentException("Invalid email or password"))
            }
        }

        override suspend fun logout(): Result<Unit> {
            userFlow.value = null
            return Result.success(Unit)
        }

        override fun getLoggedInUser(): Flow<User?> = userFlow
        override fun isUserLoggedIn(): Boolean = userFlow.value != null
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeAuthRepository = FakeAuthRepository()
        viewModel = LoginViewModel(fakeAuthRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `validateEmail returns error when email is empty`() {
        val error = viewModel.validateEmail("")
        assertEquals("Email is required", error)
    }

    @Test
    fun `validateEmail returns error for invalid email formats`() {
        assertEquals("Please enter a valid email format", viewModel.validateEmail("invalid-email"))
        assertEquals("Please enter a valid email format", viewModel.validateEmail("test@"))
        assertEquals("Please enter a valid email format", viewModel.validateEmail("@example.com"))
    }

    @Test
    fun `validateEmail returns null for valid email format`() {
        assertNull(viewModel.validateEmail("test@example.com"))
        assertNull(viewModel.validateEmail("user.name+tag@domain.co.uk"))
    }

    @Test
    fun `validatePassword returns error when password is empty`() {
        val error = viewModel.validatePassword("")
        assertEquals("Password is required", error)
    }

    @Test
    fun `validatePassword returns error when password has less than 6 characters`() {
        val error = viewModel.validatePassword("12345")
        assertEquals("Password must be at least 6 characters", error)
    }

    @Test
    fun `validatePassword returns null when password has 6 or more characters`() {
        assertNull(viewModel.validatePassword("123456"))
        assertNull(viewModel.validatePassword("password123"))
    }

    @Test
    fun `login fails client validation without calling repository if fields are empty`() {
        viewModel.login()
        val state = viewModel.uiState.value

        assertNotNull(state.emailError)
        assertNotNull(state.passwordError)
        assertFalse(state.isLoading)
        assertFalse(state.isLoginSuccessful)
    }

    @Test
    fun `login succeeds when valid credentials provided`() = runTest(testDispatcher) {
        viewModel.onEmailChanged("test@example.com")
        viewModel.onPasswordChanged("password123")

        viewModel.login()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertTrue(state.isLoginSuccessful)
        assertNull(state.errorMessage)
    }

    @Test
    fun `login fails when authentication rejected by repository`() = runTest(testDispatcher) {
        fakeAuthRepository.shouldSucceed = false
        viewModel.onEmailChanged("wrong@example.com")
        viewModel.onPasswordChanged("wrongpassword")

        viewModel.login()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertFalse(state.isLoginSuccessful)
        assertEquals("Invalid email or password", state.errorMessage)
    }
}
