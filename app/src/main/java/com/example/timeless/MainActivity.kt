package com.example.timeless

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.timeless.ui.theme.TimeLESSTheme
import kotlinx.coroutines.delay
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val systemDarkMode = androidx.compose.foundation.isSystemInDarkTheme()
            var isDarkMode by rememberSaveable { mutableStateOf(systemDarkMode) }
            TimeLESSTheme(darkTheme = isDarkMode) {
                MainScreen(
                    isDarkMode = isDarkMode,
                    onToggleDarkMode = { isDarkMode = !isDarkMode }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    isDarkMode: Boolean,
    onToggleDarkMode: () -> Unit,
    viewModel: TimerViewModel = viewModel()
) {
    val timeDisplay by viewModel.timeDisplay.collectAsState()
    val targetDateTime by viewModel.targetDateTime.collectAsState()
    var showDatePicker by remember { mutableStateOf(false) }

    if (showDatePicker || targetDateTime == null) {
        DateSelectionScreen(
            viewModel = viewModel,
            targetDateTime = targetDateTime,
            onDateSelected = { showDatePicker = false }
        )
    } else {
        LaunchedEffect(targetDateTime) {
            viewModel.updateTimeDifference()
            while (true) {
                val nowMs = System.currentTimeMillis()
                val delayMs = 1000 - (nowMs % 1000)
                delay(delayMs)
                viewModel.updateTimeDifference()
            }
        }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = { Text("TimeLESS") },
                    actions = {
                        IconButton(onClick = onToggleDarkMode) {
                            Icon(
                                imageVector = if (isDarkMode) Icons.Filled.LightMode else Icons.Filled.DarkMode,
                                contentDescription = "Toggle dark mode"
                            )
                        }
                    }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "${timeDisplay.years}y ${timeDisplay.months}m ${timeDisplay.days}d",
                    style = TextStyle(fontSize = 36.sp)
                )
                Text(
                    text = "${timeDisplay.hours}h ${timeDisplay.minutes}m ${timeDisplay.seconds}s",
                    style = TextStyle(fontSize = 36.sp)
                )
                Button(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.padding(top = 32.dp)
                ) {
                    Text("Change Date")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateSelectionScreen(
    viewModel: TimerViewModel,
    targetDateTime: LocalDateTime?,
    onDateSelected: () -> Unit = {}
) {
    val deviceZone = ZoneId.systemDefault()
    val initialSelectedDateMillis = remember(targetDateTime) {
        targetDateTime
            ?.toLocalDate()
            ?.atStartOfDay(deviceZone)
            ?.toInstant()
            ?.toEpochMilli()
    }
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialSelectedDateMillis)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Select Target Date") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors()
            )
            Button(
                onClick = {
                    val selectedMillis = datePickerState.selectedDateMillis
                    if (selectedMillis != null) {
                        val instant = Instant.ofEpochMilli(selectedMillis)
                        val selectedDate = instant.atZone(deviceZone).toLocalDate()
                        val selectedDateTime = selectedDate.atTime(23, 59, 59)
                        viewModel.setTargetDateTime(selectedDateTime)
                        viewModel.updateTimeDifference()
                        onDateSelected()
                    }
                },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("Confirm Date")
            }
        }
    }
}