package com.example.learning_dashboard.domain.util

import org.junit.Assert.assertEquals
import org.junit.Test

class ProgressCalculatorTest {

    @Test
    fun `calculateProgress returns correct percentage for standard values`() {
        assertEquals(
            50,
            ProgressCalculator.calculateProgress(
                completedLessons = 5,
                totalLessons = 10
            )
        )
    }

    @Test
    fun `calculateProgress handles 0 total lessons safely without dividing by zero`() {
        assertEquals(
            0,
            ProgressCalculator.calculateProgress(
                completedLessons = 0,
                totalLessons = 0
            )
        )
    }

    @Test
    fun `calculateProgress handles negative total lessons safely`() {
        assertEquals(
            0,
            ProgressCalculator.calculateProgress(
                completedLessons = 3,
                totalLessons = -5
            )
        )
    }

    @Test
    fun `calculateProgress handles 0 completed lessons`() {
        assertEquals(
            0,
            ProgressCalculator.calculateProgress(
                completedLessons = 0,
                totalLessons = 10
            )
        )
    }

    @Test
    fun `calculateProgress handles negative completed lessons`() {
        assertEquals(
            0,
            ProgressCalculator.calculateProgress(
                completedLessons = -2,
                totalLessons = 10
            )
        )
    }

    @Test
    fun `calculateProgress returns 100 when all lessons are completed`() {
        assertEquals(
            100,
            ProgressCalculator.calculateProgress(
                completedLessons = 10,
                totalLessons = 10
            )
        )
    }

    @Test
    fun `calculateProgress clamps to 100 when completed exceeds total`() {
        assertEquals(
            100,
            ProgressCalculator.calculateProgress(
                completedLessons = 15,
                totalLessons = 10
            )
        )
    }

    @Test
    fun `calculateProgress rounds properly to nearest integer`() {
        // 1/3 = 33.333% -> 33
        assertEquals(
            33,
            ProgressCalculator.calculateProgress(
                completedLessons = 1,
                totalLessons = 3
            )
        )

        // 2/3 = 66.666% -> 67
        assertEquals(
            67,
            ProgressCalculator.calculateProgress(
                completedLessons = 2,
                totalLessons = 3
            )
        )

        // 13/20 = 65%
        assertEquals(
            65,
            ProgressCalculator.calculateProgress(
                completedLessons = 13,
                totalLessons = 20
            )
        )
    }
}
