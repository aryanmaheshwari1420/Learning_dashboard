package com.example.learning_dashboard.presentation.navigation

sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Dashboard : Screen("dashboard")
    data object CourseDetails : Screen("course/{courseId}") {
        const val ARG_COURSE_ID = "courseId"
        fun createRoute(courseId: Long): String = "course/$courseId"
    }
}
