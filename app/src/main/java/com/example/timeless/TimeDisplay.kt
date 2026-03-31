package com.example.timeless

import java.time.LocalDateTime
import java.time.Duration

data class TimeDisplay(
    val years: Long = 0,
    val months: Long = 0,
    val days: Long = 0,
    val hours: Long = 0,
    val minutes: Long = 0,
    val seconds: Long = 0
)

fun calculateTimeDisplay(startDateTime: LocalDateTime, endDateTime: LocalDateTime): TimeDisplay {
    val (start, end) = if (endDateTime.isAfter(startDateTime)) {
        startDateTime to endDateTime
    } else {
        endDateTime to startDateTime
    }

    var current = start
    var years = 0L
    var months = 0L
    var days = 0L

    while (current.plusYears(1) <= end) {
        years++
        current = current.plusYears(1)
    }

    while (current.plusMonths(1) <= end) {
        months++
        current = current.plusMonths(1)
    }

    while (current.plusDays(1) <= end) {
        days++
        current = current.plusDays(1)
    }

    val remainingDuration = Duration.between(current, end)
    val totalSeconds = remainingDuration.seconds

    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60

    return TimeDisplay(
        years = years,
        months = months,
        days = days,
        hours = hours,
        minutes = minutes,
        seconds = seconds
    )
}
