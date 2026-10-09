package com.example.learning_dashboard.data.remote.api

import com.example.learning_dashboard.data.remote.dto.CourseDto
import com.example.learning_dashboard.data.remote.dto.LessonDto
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Standard Retrofit service interface representing remote course endpoints.
 */
interface CourseApi {
    @GET("courses")
    suspend fun getCourses(): List<CourseDto>

    @GET("courses/{courseId}/lessons")
    suspend fun getLessons(@Path("courseId") courseId: Long): List<LessonDto>

    @GET("lessons")
    suspend fun getAllLessons(): List<LessonDto>
}
