package com.example.learning_dashboard.domain.util

import kotlin.math.roundToInt

object ProgressCalculator {
    /**
     * Calculates the progress percentage rounded to the nearest integer.
     * Safely handles edge cases like 0 or negative total lessons.
     *
     * @param completedLessons Number of lessons completed
     * @param totalLessons Total number of lessons in the course
     * @return Integer progress percentage bounded between 0 and 100
     */
    fun calculateProgress(completedLessons: Int, totalLessons: Int): Int {
        if (totalLessons <= 0) return 0
        if (completedLessons <= 0) return 0
        if (completedLessons >= totalLessons) return 100

        val ratio = completedLessons.toDouble() / totalLessons.toDouble()
        return (ratio * 100).roundToInt().coerceIn(0, 100)
    }
}
