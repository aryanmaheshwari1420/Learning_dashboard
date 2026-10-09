package com.example.learning_dashboard.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.learning_dashboard.domain.model.Lesson

@Entity(
    tableName = "lessons",
    indices = [Index(value = ["courseId"])]
)
data class LessonEntity(

    @PrimaryKey
    val id: Long,
    val courseId: Long,
    val title: String,
    val isCompleted: Boolean,
    val order: Int
) {
    fun toDomainModel(): Lesson {
        return Lesson(
            id = id,
            courseId = courseId,
            title = title,
            isCompleted = isCompleted,
            order = order
        )
    }
}
