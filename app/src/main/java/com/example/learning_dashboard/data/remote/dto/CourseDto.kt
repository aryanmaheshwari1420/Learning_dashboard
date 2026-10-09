package com.example.learning_dashboard.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CourseDto(
    @SerializedName("id")
    val id: Long,
    @SerializedName("title")
    val title: String,
    @SerializedName("instructor")
    val instructor: String,
    @SerializedName("progress")
    val progress: Int,
    @SerializedName("lessons")
    val lessons: Int
)
