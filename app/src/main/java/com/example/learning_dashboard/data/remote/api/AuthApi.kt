package com.example.learning_dashboard.data.remote.api

import com.example.learning_dashboard.data.remote.dto.LoginRequestDto
import com.example.learning_dashboard.data.remote.dto.LoginResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Standard Retrofit service interface representing remote authentication endpoints.
 */
interface AuthApi {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequestDto): LoginResponseDto
}
