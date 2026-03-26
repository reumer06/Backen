package com.example.timeless

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDateTime
import java.time.ZoneId

class TimerViewModel : ViewModel() {
    private val _timeDisplay = MutableStateFlow(TimeDisplay())
    val timeDisplay: StateFlow<TimeDisplay> = _timeDisplay

    private val _targetDateTime = MutableStateFlow<LocalDateTime?>(null)
    val targetDateTime: StateFlow<LocalDateTime?> = _targetDateTime

    private val istZone = ZoneId.of("Asia/Kolkata")

    fun setTargetDateTime(dateTime: LocalDateTime) {
        _targetDateTime.value = dateTime
    }

    fun updateTimeDifference() {
        val target = _targetDateTime.value
        if (target != null) {
            val now = LocalDateTime.now(istZone)
            _timeDisplay.value = calculateTimeDisplay(now, target)
        }
    }
}
