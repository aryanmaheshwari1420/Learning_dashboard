package com.example.learning_dashboard.presentation.coursedetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.learning_dashboard.domain.model.Lesson
import com.example.learning_dashboard.domain.repository.CourseRepository
import com.example.learning_dashboard.domain.util.Resource
import com.example.learning_dashboard.presentation.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CourseDetailViewModel @Inject constructor(
    private val courseRepository: CourseRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val courseId: Long = when (val arg = savedStateHandle.get<Any>(Screen.CourseDetails.ARG_COURSE_ID)) {
        is Long -> arg
        is Number -> arg.toLong()
        is String -> arg.toLongOrNull() ?: 1L
        else -> 1L
    }


    private val _uiState = MutableStateFlow<CourseDetailUiState>(CourseDetailUiState.Loading)
    val uiState: StateFlow<CourseDetailUiState> = _uiState.asStateFlow()

    init {
        loadCourseDetails()
    }

    fun loadCourseDetails() {
        viewModelScope.launch {
            courseRepository.getCourseDetails(courseId).collect { resource ->
                _uiState.value = when (resource) {
                    is Resource.Loading -> CourseDetailUiState.Loading
                    is Resource.Success -> {
                        CourseDetailUiState.Success(
                            course = resource.data.first,
                            lessons = resource.data.second
                        )
                    }
                    is Resource.Error -> {
                        CourseDetailUiState.Error(
                            resource.message.ifBlank { "Unable to load course details." }
                        )
                    }
                }
            }
        }
    }

    fun onToggleLesson(lesson: Lesson) {
        viewModelScope.launch {
            val newCompletionStatus = !lesson.isCompleted
            courseRepository.toggleLessonCompletion(
                courseId = courseId,
                lessonId = lesson.id,
                completed = newCompletionStatus
            )
        }
    }

    fun retry() {
        _uiState.value = CourseDetailUiState.Loading
        loadCourseDetails()
    }
}
