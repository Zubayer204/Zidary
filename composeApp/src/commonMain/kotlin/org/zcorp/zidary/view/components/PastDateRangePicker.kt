package org.zcorp.zidary.view.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DateRangePickerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.datetime.Clock

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangePickerDialogue(
    onDismiss: () -> Unit,
    onConfirm: (Long?, Long?) -> Unit,
) {
    val dateRangePickerState = rememberDateRangePickerState(
        selectableDates = PastSelectableDates
    )
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = { onConfirm(
                    dateRangePickerState.selectedStartDateMillis,
                    dateRangePickerState.selectedEndDateMillis
                ) },
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Text("Cancel")
            }
        },
        modifier = Modifier.fillMaxSize()
    ) {
        DateRangePicker(
            title = {
                DateRangePickerDefaults.DateRangePickerTitle(
                    displayMode = dateRangePickerState.displayMode,
                    modifier = Modifier.padding(0.dp)
                )
            },
            headline = {
                DateRangePickerDefaults.DateRangePickerHeadline(
                    dateRangePickerState.selectedStartDateMillis,
                    dateRangePickerState.selectedEndDateMillis,
                    displayMode = dateRangePickerState.displayMode,
                    DatePickerDefaults.dateFormatter(),
                    modifier = Modifier.padding(0.dp)
                )
            },
            state = dateRangePickerState,
            showModeToggle = false,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
private object PastSelectableDates: SelectableDates {
    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
        return utcTimeMillis <= Clock.System.now().toEpochMilliseconds()
    }
}