package com.example.timeless

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import com.example.timeless.DisplayMode
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import java.time.DateTimeException

class DateVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = if (text.text.length >= 8) text.text.substring(0, 8) else text.text
        var out = ""
        for (i in trimmed.indices) {
            out += trimmed[i]
            if (i == 3 || i == 5) {
                if (i < trimmed.length - 1) {
                    out += "-"
                }
            }
        }

        val numberOffsetTranslator = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 3) return offset
                if (offset <= 5) return offset + 1
                if (offset <= 8) return offset + 2
                return 10
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 4) return offset
                if (offset <= 7) return offset - 1
                if (offset <= 10) return offset - 2
                return 8
            }
        }

        return TransformedText(AnnotatedString(out), numberOffsetTranslator)
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val systemDarkMode = isSystemInDarkTheme()
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
    var showMenu by remember { mutableStateOf(false) }

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
                val delayMs = 1000 - (nowMs % 1000 + 1)
                delay(delayMs.coerceAtLeast(500))
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
                        IconButton(onClick = { showMenu = true }) {
                            Icon(
                                imageVector = Icons.Filled.Menu,
                                contentDescription = "Display Mode"
                            )
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Days") },
                                onClick = {
                                    viewModel.setDisplayMode(DisplayMode.DAY)
                                    showMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Hours") },
                                onClick = {
                                    viewModel.setDisplayMode(DisplayMode.HOUR)
                                    showMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Minutes") },
                                onClick = {
                                    viewModel.setDisplayMode(DisplayMode.MINUTE)
                                    showMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Seconds") },
                                onClick = {
                                    viewModel.setDisplayMode(DisplayMode.SECOND)
                                    showMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Nanoseconds") },
                                onClick = {
                                    viewModel.setDisplayMode(DisplayMode.NANOSECOND)
                                    showMenu = false
                                }
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
                when (timeDisplay.displayMode) {
                    DisplayMode.DAY -> Text(text = "${timeDisplay.days}d", style = TextStyle(fontSize = 36.sp))
                    DisplayMode.HOUR -> Text(text = "${timeDisplay.hours}h", style = TextStyle(fontSize = 36.sp))
                    DisplayMode.MINUTE -> Text(text = "${timeDisplay.minutes}m", style = TextStyle(fontSize = 36.sp))
                    DisplayMode.SECOND -> Text(text = "${timeDisplay.seconds}s", style = TextStyle(fontSize = 36.sp))
                    DisplayMode.NANOSECOND -> Text(text = "${timeDisplay.nanoseconds}ns", style = TextStyle(fontSize = 36.sp))
                }
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
                    if (newText.length <= 8) {
                        text = newText.filter { it.isDigit() }
                        isError = false
                    }
                },
                label = { Text("YYYY-MM-DD") },
                placeholder = { Text("Enter date as YYYY-MM-DD") },
                singleLine = true,
                isError = isError,
                visualTransformation = DateVisualTransformation()
            )
            Button(
                onClick = {
                    try {
                        val year = text.substring(0, 4).toInt()
                        val month = text.substring(4, 6).toInt()
                        val day = text.substring(6, 8).toInt()

                        val localDate = LocalDate.of(year, month, day)
                        val selectedDateTime = localDate.atTime(0, 0, 0)

                        viewModel.setTargetDateTime(selectedDateTime)
                        viewModel.updateTimeDifference()
                        onDateSelected()
                    } catch (e: Exception) {
                        isError = true
                    }
                },
                enabled = text.length == 8,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("Confirm Date")
            }
        }
    }
}