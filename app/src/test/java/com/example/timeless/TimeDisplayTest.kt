package com.example.timeless

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDateTime

class TimeDisplayTest {

    @Test
    fun returnsZeroWhenTargetIsBeforeStart() {
        val start = LocalDateTime.of(2026, 3, 27, 12, 0, 0)
        val target = LocalDateTime.of(2026, 3, 27, 11, 59, 59)

        val result = calculateTimeDisplay(start, target)

        assertEquals(TimeDisplay(), result)
    }

    @Test
    fun calculatesMixedDateAndTimeDifference() {
        val start = LocalDateTime.of(2026, 1, 1, 10, 15, 30)
        val target = LocalDateTime.of(2027, 3, 4, 14, 45, 50)

        val result = calculateTimeDisplay(start, target)

        assertEquals(1, result.years)
        assertEquals(2, result.months)
        assertEquals(3, result.days)
        assertEquals(4, result.hours)
        assertEquals(30, result.minutes)
        assertEquals(20, result.seconds)
    }

    @Test
    fun handlesEndOfMonthRollover() {
        val start = LocalDateTime.of(2026, 1, 31, 23, 0, 0)
        val target = LocalDateTime.of(2026, 3, 1, 1, 0, 0)

        val result = calculateTimeDisplay(start, target)

        assertEquals(0, result.years)
        assertEquals(1, result.months)
        assertEquals(0, result.days)
        assertEquals(2, result.hours)
        assertEquals(0, result.minutes)
        assertEquals(0, result.seconds)
    }
}
