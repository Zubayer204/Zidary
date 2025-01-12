package org.zcorp.zidary.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.datetime.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimeSelector(
    initialDateTime: Instant,
    onDateTimeSelected: (Instant) -> Unit,
    color: Color,
) {
    val initialLocalDateTime = initialDateTime.toLocalDateTime(TimeZone.currentSystemDefault())
    var selectedDate by remember(initialLocalDateTime) { mutableStateOf(initialLocalDateTime.date) }
    var selectedTime by remember(initialLocalDateTime) { mutableStateOf(initialLocalDateTime.time) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier,
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Date selection button
        Text(
            selectedDate.toString(),
            color = color,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.clickable {
                showDatePicker = true
            }
        )

        // Time selection button
        Text(
            selectedTime.formatTime(),
            color = color,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.clickable {
                showTimePicker = true
            }
        )

        // Down arrow icon
        Icon(
            Icons.Default.ArrowDownward,
            "DateTimePicker",
            tint = color,
            modifier = Modifier
                .size(16.dp)
                .offset(0.dp, 2.dp)
                .clickable {
                    showTimePicker = true
                }
        )

        // Date Picker Dialog
        if (showDatePicker) {
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = selectedDate.toEpochDays()
                    .toLong() * 24 * 60 * 60 * 1000
            )

            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let {
                            val instant = Instant.fromEpochMilliseconds(it)
                            val date = instant.toLocalDateTime(TimeZone.currentSystemDefault()).date
                            selectedDate = date
                            onDateTimeSelected(
                                LocalDateTime(selectedDate, selectedTime).toInstant(
                                    TimeZone.currentSystemDefault()
                                )
                            )
                        }
                        showDatePicker = false
                    }) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text("Cancel")
                    }
                }
            ) {
                DatePicker(
                    state = datePickerState
                )
            }
        }

        // Time Picker Dialog
        if (showTimePicker) {
            val timePickerState = rememberTimePickerState(
                initialHour = selectedTime.hour,
                initialMinute = selectedTime.minute
            )

            AdvancedTimePickerDialog(
                onDismissRequest = { showTimePicker = false },
                onConfirm = {
                    selectedTime = LocalTime(
                        hour = timePickerState.hour,
                        minute = timePickerState.minute
                    )
                    onDateTimeSelected(
                        LocalDateTime(
                            selectedDate,
                            selectedTime
                        ).toInstant(TimeZone.currentSystemDefault())
                    )
                    showTimePicker = false
                },
                onDismiss = { showTimePicker = false }
            ) {
                TimePicker(
                    state = timePickerState
                )
            }
        }
    }
}

@Composable
private fun AdvancedTimePickerDialog(
    title: String = "Select Time",
    onDismissRequest: () -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    content: @Composable () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp,
            modifier =
            Modifier
                .width(IntrinsicSize.Min)
                .height(IntrinsicSize.Min)
                .background(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = MaterialTheme.colorScheme.surface
                ),
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    text = title,
                    style = MaterialTheme.typography.labelMedium
                )
                content()
                Row(
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth()
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    TextButton(onClick = onConfirm) { Text("OK") }
                }
            }
        }
    }
}

private fun LocalTime.formatTime(): String {
    return "${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}"
}