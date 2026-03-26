package com.example.timeless

import java.time.LocalDateTime
import java.time.Duration
import java.time.temporal.ChronoUnit

data class TimeDisplay(
    val years: Long = 0,
    val months: Long = 0,
    val days: Long = 0,
    val hours: Long = 0,
    val minutes: Long = 0,
    val seconds: Long = 0
)

fun calculateTimeDisplay(startDateTime: LocalDateTime, endDateTime: LocalDateTime): TimeDisplay {
    if (endDateTime <= startDateTime) {
        return TimeDisplay()
    }

    var current = startDateTime
    var years = 0L
    var months = 0L
    var days = 0L

    while (current.plusYears(1) <= endDateTime) {
        years++
        current = current.plusYears(1)
    }

    while (current.plusMonths(1) <= endDateTime) {
        months++
        current = current.plusMonths(1)
    }

    while (current.plusDays(1) <= endDateTime) {
        days++
        current = current.plusDays(1)
    }

    val remainingDuration = Duration.between(current, endDateTime)
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
