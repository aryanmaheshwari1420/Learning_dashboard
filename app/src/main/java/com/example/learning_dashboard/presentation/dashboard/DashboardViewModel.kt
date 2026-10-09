package com.example.learning_dashboard.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.learning_dashboard.domain.repository.CourseRepository
import com.example.learning_dashboard.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val courseRepository: CourseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<DashboardUiState>(DashboardUiState.Loading)
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    private val _isOfflineSimulated = MutableStateFlow(courseRepository.isSimulatingNetworkError())
    val isOfflineSimulated: StateFlow<Boolean> = _isOfflineSimulated.asStateFlow()

    private var loadCoursesJob: Job? = null

    init {
        loadCourses()
    }

    fun loadCourses() {
        loadCoursesJob?.cancel()
        loadCoursesJob = viewModelScope.launch {
            courseRepository.getCourses().collect { resource ->
                _uiState.value = when (resource) {
                    is Resource.Loading -> {
                        // Keep current success state during background re-fetch if available
                        val current = _uiState.value
                        if (current is DashboardUiState.Success) current else DashboardUiState.Loading
                    }
                    is Resource.Success -> {
                        if (resource.data.isEmpty()) {
                            DashboardUiState.Empty
                        } else {
                            DashboardUiState.Success(resource.data)
                        }
                    }
                    is Resource.Error -> {
                        DashboardUiState.Error(
                            resource.message.ifBlank { "Unable to load courses." }
                        )
                    }
                }
            }
        }
    }

    fun retry() {
        _uiState.value = DashboardUiState.Loading
        loadCourses()
    }

    fun toggleNetworkSimulation() {
        val newState = !_isOfflineSimulated.value
        courseRepository.setSimulateNetworkError(newState)
        _isOfflineSimulated.value = newState
    }
}
