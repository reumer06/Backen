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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.OutlinedTextField
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
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

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
                    title = { },
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
    var text by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = text,
                onValueChange = { newText ->
                    if (newText.length <= 10) {
                        val currentText = text
                        text = when {
                            // Add first hyphen: YYYY -> YYYY-
                            newText.length == 5 && currentText.length == 4 && newText.last() != '-' ->
                                newText.substring(0, 4) + "-" + newText.last()
                            // Add second hyphen: YYYY-MM -> YYYY-MM-
                            newText.length == 8 && currentText.length == 7 && newText.last() != '-' ->
                                newText.substring(0, 7) + "-" + newText.last()
                            else -> newText
                        }
                        isError = false
                    }
                },
                label = { Text("YYYY-MM-DD") },
                placeholder = { Text("Enter date as YYYY-MM-DD") },
                singleLine = true,
                isError = isError
            )
            Button(
                onClick = {
                    try {
                        val formatter = DateTimeFormatter.ISO_LOCAL_DATE
                        val localDate = LocalDate.parse(text, formatter)
                        val selectedDateTime = localDate.atTime(0, 0, 0)

                        viewModel.setTargetDateTime(selectedDateTime)
                        viewModel.updateTimeDifference()
                        onDateSelected()
                    } catch (e: DateTimeParseException) {
                        isError = true
                    }
                },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("Confirm Date")
            }
        }
    }
}