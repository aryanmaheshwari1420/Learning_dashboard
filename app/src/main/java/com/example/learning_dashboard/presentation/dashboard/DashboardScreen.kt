package com.example.learning_dashboard.presentation.dashboard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SignalWifi4Bar
import androidx.compose.material.icons.filled.SignalWifiOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.learning_dashboard.presentation.common.CourseCard
import com.example.learning_dashboard.presentation.common.EmptyState
import com.example.learning_dashboard.presentation.common.ErrorState
import com.example.learning_dashboard.presentation.common.LoadingState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onCourseClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isOfflineSimulated by viewModel.isOfflineSimulated.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Course Dashboard",
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    // Offline simulation toggle for evaluator convenience
                    IconButton(onClick = viewModel::toggleNetworkSimulation) {
                        Icon(
                            imageVector = if (isOfflineSimulated) Icons.Filled.SignalWifiOff else Icons.Filled.SignalWifi4Bar,
                            contentDescription = if (isOfflineSimulated) "Network Disabled (Simulated)" else "Network Online",
                            tint = if (isOfflineSimulated) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                        )
                    }

                    // Refresh action
                    IconButton(onClick = viewModel::retry) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh Courses"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val state = uiState) {
                is DashboardUiState.Loading -> {
                    LoadingState()
                }

                is DashboardUiState.Empty -> {
                    EmptyState(
                        message = "No courses available.",
                        onAction = viewModel::retry,
                        actionLabel = "Refresh"
                    )
                }

                is DashboardUiState.Error -> {
                    ErrorState(
                        message = state.message,
                        onRetry = viewModel::retry
                    )
                }

                is DashboardUiState.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 12.dp)
                    ) {
                        items(
                            items = state.courses,
                            key = { course -> course.id }
                        ) { course ->
                            CourseCard(
                                course = course,
                                onContinueClick = { onCourseClick(course.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}
