package com.example.learning_dashboard.domain.repository

import com.example.learning_dashboard.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<User>
    suspend fun logout(): Result<Unit>
    fun getLoggedInUser(): Flow<User?>
    fun isUserLoggedIn(): Boolean
}
