package com.example.learning_dashboard.data.remote.dto

import com.google.gson.annotations.SerializedName

data class LessonDto(
    @SerializedName("id")
    val id: Long,
    @SerializedName("courseId")
    val courseId: Long,
    @SerializedName("title")
    val title: String,
    @SerializedName("isCompleted")
    val isCompleted: Boolean,
    @SerializedName("order")
    val order: Int
)
