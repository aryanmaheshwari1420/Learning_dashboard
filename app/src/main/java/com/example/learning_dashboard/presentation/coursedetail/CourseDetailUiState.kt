package com.example.learning_dashboard.presentation.coursedetail

import com.example.learning_dashboard.domain.model.Course
import com.example.learning_dashboard.domain.model.Lesson

sealed interface CourseDetailUiState {
    data object Loading : CourseDetailUiState
    data class Success(
        val course: Course,
        val lessons: List<Lesson>
    ) : CourseDetailUiState
    data class Error(val message: String) : CourseDetailUiState
}
