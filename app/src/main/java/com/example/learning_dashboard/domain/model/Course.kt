package com.example.learning_dashboard.domain.model

data class Course(
    val id: Long,
    val title: String,
    val instructor: String,
    val progress: Int,
    val totalLessons: Int,
    val completedLessons: Int = 0
)
