package com.example.learning_dashboard.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.learning_dashboard.domain.model.Course

@Entity(tableName = "courses")
data class CourseEntity(
    @PrimaryKey
    val id: Long,
    val title: String,
    val instructor: String,
    val progress: Int,
    val totalLessons: Int
) {
    fun toDomainModel(completedLessonsCount: Int = 0): Course {
        return Course(
            id = id,
            title = title,
            instructor = instructor,
            progress = progress,
            totalLessons = totalLessons,
            completedLessons = completedLessonsCount
        )
    }
}
