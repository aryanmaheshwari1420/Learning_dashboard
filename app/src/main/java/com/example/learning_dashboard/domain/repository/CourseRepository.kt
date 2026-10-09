package com.example.learning_dashboard.domain.repository

import com.example.learning_dashboard.domain.model.Course
import com.example.learning_dashboard.domain.model.Lesson
import com.example.learning_dashboard.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface CourseRepository {
    /**
     * Exposes courses with an offline-first caching policy:
     * - Emits Loading initially
     * - Tries to fetch fresh remote data and sync to Room
     * - Emits Room cache data continuously via Flow
     * - If network fails, falls back gracefully to Room cache
     */
    fun getCourses(): Flow<Resource<List<Course>>>

    /**
     * Explicit refresh trigger.
     */
    suspend fun refreshCourses(): Result<Unit>

    /**
     * Exposes course and its lessons by ID.
     */
    fun getCourseDetails(courseId: Long): Flow<Resource<Pair<Course, List<Lesson>>>>

    /**
     * Marks a lesson as completed and recalculates course progress.
     */
    suspend fun markLessonCompleted(courseId: Long, lessonId: Long): Result<Unit>

    /**
     * Toggles a lesson's completed status and recalculates course progress.
     */
    suspend fun toggleLessonCompletion(courseId: Long, lessonId: Long, completed: Boolean): Result<Unit>

    /**
     * Network error simulation toggle for offline demo.
     */
    fun setSimulateNetworkError(simulate: Boolean)
    fun isSimulatingNetworkError(): Boolean
}
