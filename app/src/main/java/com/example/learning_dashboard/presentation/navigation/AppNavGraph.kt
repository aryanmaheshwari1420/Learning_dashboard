package com.example.learning_dashboard.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.learning_dashboard.presentation.coursedetail.CourseDetailScreen
import com.example.learning_dashboard.presentation.coursedetail.CourseDetailViewModel
import com.example.learning_dashboard.presentation.dashboard.DashboardScreen
import com.example.learning_dashboard.presentation.dashboard.DashboardViewModel
import com.example.learning_dashboard.presentation.login.LoginScreen
import com.example.learning_dashboard.presentation.login.LoginViewModel

@Composable
fun AppNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Login.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(route = Screen.Login.route) {
            val loginViewModel: LoginViewModel = hiltViewModel()
            LoginScreen(
                viewModel = loginViewModel,
                onLoginSuccess = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(route = Screen.Dashboard.route) {
            val dashboardViewModel: DashboardViewModel = hiltViewModel()
            DashboardScreen(
                viewModel = dashboardViewModel,
                onCourseClick = { courseId ->
                    navController.navigate(Screen.CourseDetails.createRoute(courseId))
                }
            )
        }

        composable(
            route = Screen.CourseDetails.route,
            arguments = listOf(
                navArgument(Screen.CourseDetails.ARG_COURSE_ID) {
                    type = NavType.LongType
                    defaultValue = 1L
                }
            )
        ) {
            val courseDetailViewModel: CourseDetailViewModel = hiltViewModel()
            CourseDetailScreen(
                viewModel = courseDetailViewModel,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}
