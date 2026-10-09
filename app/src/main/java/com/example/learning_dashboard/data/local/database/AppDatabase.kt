package com.example.learning_dashboard.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.learning_dashboard.data.local.dao.CourseDao
import com.example.learning_dashboard.data.local.dao.LessonDao
import com.example.learning_dashboard.data.local.entity.CourseEntity
import com.example.learning_dashboard.data.local.entity.LessonEntity

@Database(
    entities = [
        CourseEntity::class,
        LessonEntity::class
    ],
    version = 2,
    exportSchema = false
)

abstract class AppDatabase : RoomDatabase() {
    abstract fun courseDao(): CourseDao
    abstract fun lessonDao(): LessonDao
}
