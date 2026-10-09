package com.example.learning_dashboard.data.repository

import com.example.learning_dashboard.data.remote.datasource.RemoteDataSource
import com.example.learning_dashboard.data.remote.dto.LoginRequestDto
import com.example.learning_dashboard.domain.model.User
import com.example.learning_dashboard.domain.repository.AuthRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : AuthRepository {

    private val _loggedInUser = MutableStateFlow<User?>(null)

    override suspend fun login(email: String, password: String): Result<User> = withContext(ioDispatcher) {
        try {
            // Note: Password is only sent in payload for authentication; NEVER stored anywhere.
            val response = remoteDataSource.login(
                LoginRequestDto(email = email, password = password)
            )
            val user = User(
                email = response.email,
                token = response.token
            )
            _loggedInUser.value = user
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout(): Result<Unit> = withContext(ioDispatcher) {
        _loggedInUser.value = null
        Result.success(Unit)
    }

    override fun getLoggedInUser(): Flow<User?> {
        return _loggedInUser.asStateFlow()
    }

    override fun isUserLoggedIn(): Boolean {
        return _loggedInUser.value != null
    }
}
