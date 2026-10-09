package com.example.learning_dashboard.data.remote.datasource

import com.example.learning_dashboard.data.remote.dto.CourseDto
import com.example.learning_dashboard.data.remote.dto.LessonDto
import com.example.learning_dashboard.data.remote.dto.LoginRequestDto
import com.example.learning_dashboard.data.remote.dto.LoginResponseDto

interface RemoteDataSource {
    suspend fun getCourses(): List<CourseDto>
    suspend fun getLessons(courseId: Long): List<LessonDto>
    suspend fun getAllLessons(): List<LessonDto>
    suspend fun login(request: LoginRequestDto): LoginResponseDto

    // Network simulation toggle for offline demo
    fun setSimulateNetworkError(simulate: Boolean)
    fun isSimulatingNetworkError(): Boolean
}
