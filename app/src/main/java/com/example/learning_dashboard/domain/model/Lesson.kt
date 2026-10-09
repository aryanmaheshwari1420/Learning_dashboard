package com.example.learning_dashboard.domain.model

data class Lesson(
    val id: Long,
    val courseId: Long,
    val title: String,
    val isCompleted: Boolean,
    val order: Int = 0
)
