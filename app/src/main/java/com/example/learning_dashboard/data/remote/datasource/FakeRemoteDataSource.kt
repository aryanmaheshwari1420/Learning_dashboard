package com.example.learning_dashboard.data.remote.datasource

import com.example.learning_dashboard.data.remote.dto.CourseDto
import com.example.learning_dashboard.data.remote.dto.LessonDto
import com.example.learning_dashboard.data.remote.dto.LoginRequestDto
import com.example.learning_dashboard.data.remote.dto.LoginResponseDto
import kotlinx.coroutines.delay
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeRemoteDataSource @Inject constructor() : RemoteDataSource {

    private var simulateNetworkError: Boolean = false

    private val courses = listOf(
        CourseDto(
            id = 1L,
            title = "Python Programming",
            instructor = "John Smith",
            progress = 65,
            lessons = 20
        ),
        CourseDto(
            id = 2L,
            title = "Generative AI",
            instructor = "Sarah Williams",
            progress = 40,
            lessons = 16
        ),
        CourseDto(
            id = 3L,
            title = "Full Stack Development",
            instructor = "David Brown",
            progress = 25,
            lessons = 28
        )
    )

    private val lessons = listOf(
        // Course 1: Python Programming
        LessonDto(id = 101L, courseId = 1L, title = "Introduction", isCompleted = true, order = 1),
        LessonDto(id = 102L, courseId = 1L, title = "Variables & Data Types", isCompleted = true, order = 2),
        LessonDto(id = 103L, courseId = 1L, title = "Functions", isCompleted = false, order = 3),
        LessonDto(id = 104L, courseId = 1L, title = "Object Oriented Programming", isCompleted = false, order = 4),
        LessonDto(id = 105L, courseId = 1L, title = "Collections & Iterators", isCompleted = false, order = 5),

        // Course 2: Generative AI
        LessonDto(id = 201L, courseId = 2L, title = "Introduction to LLMs", isCompleted = true, order = 1),
        LessonDto(id = 202L, courseId = 2L, title = "Prompt Engineering Techniques", isCompleted = true, order = 2),
        LessonDto(id = 203L, courseId = 2L, title = "Vector Databases & Embeddings", isCompleted = false, order = 3),
        LessonDto(id = 204L, courseId = 2L, title = "Fine-Tuning Foundations", isCompleted = false, order = 4),
        LessonDto(id = 205L, courseId = 2L, title = "RAG Pipelines & Agents", isCompleted = false, order = 5),

        // Course 3: Full Stack Development
        LessonDto(id = 301L, courseId = 3L, title = "Modern Frontend Architecture", isCompleted = true, order = 1),
        LessonDto(id = 302L, courseId = 3L, title = "RESTful API Design", isCompleted = false, order = 2),
        LessonDto(id = 303L, courseId = 3L, title = "Database Modeling & SQL", isCompleted = false, order = 3),
        LessonDto(id = 304L, courseId = 3L, title = "Authentication & JWT Security", isCompleted = false, order = 4),
        LessonDto(id = 305L, courseId = 3L, title = "Docker & CI/CD Deployment", isCompleted = false, order = 5)
    )

    override suspend fun getCourses(): List<CourseDto> {
        delay(600) // Simulate network latency
        if (simulateNetworkError) {
            throw IOException("Simulated network failure: Unable to reach server")
        }
        return courses
    }

    override suspend fun getLessons(courseId: Long): List<LessonDto> {
        delay(300)
        if (simulateNetworkError) {
            throw IOException("Simulated network failure: Unable to reach server")
        }
        return lessons.filter { it.courseId == courseId }
    }

    override suspend fun getAllLessons(): List<LessonDto> {
        delay(400)
        if (simulateNetworkError) {
            throw IOException("Simulated network failure: Unable to reach server")
        }
        return lessons
    }

    override suspend fun login(request: LoginRequestDto): LoginResponseDto {
        delay(800) // Simulate network latency
        if (simulateNetworkError) {
            throw IOException("Simulated network failure: Unable to reach server")
        }
        val cleanEmail = request.email.trim()
        val cleanPassword = request.password.trim()

        if (cleanEmail.equals("test@example.com", ignoreCase = true) && cleanPassword == "password123") {
            return LoginResponseDto(
                token = "mock_jwt_token_${System.currentTimeMillis()}",
                email = cleanEmail,
                message = "Login successful"
            )
        } else {
            throw IllegalArgumentException("Invalid email or password")
        }
    }

    override fun setSimulateNetworkError(simulate: Boolean) {
        this.simulateNetworkError = simulate
    }

    override fun isSimulatingNetworkError(): Boolean {
        return simulateNetworkError
    }
}
