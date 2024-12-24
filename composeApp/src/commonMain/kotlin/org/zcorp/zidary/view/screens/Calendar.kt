package org.zcorp.zidary.view.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.koinInject
import org.zcorp.zidary.utils.formatDateTime
import org.zcorp.zidary.utils.getTotalDaysInMonth
import org.zcorp.zidary.view.components.CalendarDay
import org.zcorp.zidary.view.components.DeleteConfirmationDialog
import org.zcorp.zidary.view.components.JournalEntryBottomSheet
import org.zcorp.zidary.view.components.JournalEntryCard
import org.zcorp.zidary.viewModel.CalendarScreenEvent
import org.zcorp.zidary.viewModel.CalendarVM
import org.zcorp.zidary.viewModel.JournalComposeVM
import org.zcorp.zidary.viewModel.SettingsManager

class Calendar: Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val viewModel = koinInject<CalendarVM>()
        val journalComposeVM = koinInject<JournalComposeVM>()
        val settingsManager = koinInject<SettingsManager>()
        val securitySettings by settingsManager.securitySettings.collectAsState()

        val navigator = LocalNavigator.currentOrThrow

        // State for selected date and month
        val state by viewModel.state.collectAsState()

        val sheetState = rememberModalBottomSheetState()
        val snackbarHostState = remember { SnackbarHostState() }
        var showDeleteConfirmation by remember { mutableStateOf(false) }
        var entryToDelete by remember { mutableStateOf(-1L) }

        LaunchedEffect(Unit) {
            viewModel.events.collect { event ->
                when (event) {
                    is CalendarScreenEvent.NavigateToEdit -> {
                        navigator.push(JournalEdit(
                            event.id,
                            journalComposeVM
                        ) { navigator.pop() })
                    }
                    is CalendarScreenEvent.NavigateToView -> {
                        navigator.push(JournalView(event.entry) { navigator.pop() })
                    }
                    is CalendarScreenEvent.EntryDeleted -> {
                        snackbarHostState.showSnackbar("Entry Deleted")
                    }
                    is CalendarScreenEvent.ShowError -> {
                        snackbarHostState.showSnackbar("Error: ${event.message}")
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            item {
                // Month selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        val newDate = LocalDate(
                            state.currentYear,
                            state.currentMonth,
                            1
                        ).minus(1, DateTimeUnit.MONTH)
                        viewModel.changeMonth(newDate.year, newDate.month)
                    }) {
                        Text("<", color = MaterialTheme.colorScheme.onBackground)
                    }

                    Text(
                        text = "${state.currentMonth.name} ${state.currentYear}",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.clickable {
                            viewModel.onDatePickerStatusChange(true)
                        }
                    )

                    IconButton(onClick = {
                        val newDate = LocalDate(
                            state.currentYear,
                            state.currentMonth,
                            1
                        ).plus(1, DateTimeUnit.MONTH)
                        viewModel.changeMonth(newDate.year, newDate.month)
                    }) {
                        Text(">", color = MaterialTheme.colorScheme.onBackground)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
            // Calendar grid
            item {
                // Weekday headers
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    daysOfWeek.forEach { dayName ->
                        Text(
                            text = dayName,
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.labelMedium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Calendar dates
                val firstDayOfMonth = LocalDate(state.currentYear, state.currentMonth, 1)
                val totalDaysInMonth = getTotalDaysInMonth(firstDayOfMonth)
                val firstDayOfWeek = firstDayOfMonth.dayOfWeek.ordinal + 1 // initial ordinal is 0, so we add one to it

                LazyVerticalGrid(
                    columns = GridCells.Fixed(7),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(350.dp)
                ) {
                    // Empty spaces before first day
                    items(firstDayOfWeek) {
                        Box(modifier = Modifier.padding(8.dp))
                    }

                    // Actual dates
                    items(totalDaysInMonth) { day ->
                        val date = LocalDate(state.currentYear, state.currentMonth, day + 1)
                        val isSelected = date == state.selectedDate
                        val hasEntries = state.datesWithEntries.contains(date)

//                        println("$date HasEntries: $hasEntries")

                        CalendarDay(
                            day = day + 1,
                            isSelected = isSelected,
                            hasEntries = hasEntries,
                            onClick = { viewModel.onSelectDate(date) }
                        )
                    }
                }
            }

            item {
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.onBackground.copy(0.5f)
                )
            }

            items(state.selectedDateEntries) { entry ->
                JournalEntryCard(
                    title = entry.title,
                    content = entry.body,
                    showPreview = !securitySettings.hideEntryPreviews,
                    onClick = { viewModel.onViewEntryClick(entry) },
                    onLongClick = { viewModel.onEntryLongPress(entry) },
                    datetime = formatDateTime(entry.entry_time.toLocalDateTime(TimeZone.currentSystemDefault())),
                )
            }
        }

        state.selectedEntry?.let { entry ->
            ModalBottomSheet(
                onDismissRequest = { viewModel.onDismissSheet() },
                sheetState = sheetState
            ) {
                JournalEntryBottomSheet(
                    entry = entry,
                    onDismiss = { viewModel.onDismissSheet() },
                    onOpen = { viewModel.onViewEntryClick(it) },
                    onEdit = { viewModel.onEditClick(entry.id) },
                    onDelete = {
                        entryToDelete = it
                        showDeleteConfirmation = true
                    }
                )
            }
        }

        // Delete Confirmation Dialog
        if (showDeleteConfirmation) {
            DeleteConfirmationDialog(
                onDismissButtonClick = {
                    showDeleteConfirmation = false
                    entryToDelete = -1L
                },
                confirmButtonOnClick = {
                    viewModel.deleteEntry(entryToDelete)
                    showDeleteConfirmation = false
                    entryToDelete = -1L
                }
            )
        }

        // Date Picker Dialog
        if (state.showDatePicker) {
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = state.selectedDate.toEpochDays().toLong() * 24 * 60 * 60 * 1000
            )

            DatePickerDialog(
                onDismissRequest = {  viewModel.onDatePickerStatusChange(false) },
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let {
                            val instant = Instant.fromEpochMilliseconds(it)
                            val date = instant.toLocalDateTime(TimeZone.currentSystemDefault()).date
                            viewModel.onSelectDate(date)
                            if (date.year != state.currentYear || date.month != state.currentMonth) {
                                viewModel.changeMonth(date.year, date.month)
                            }
                        }
                        viewModel.onDatePickerStatusChange(false)
                    }) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.onDatePickerStatusChange(false) }) {
                        Text("Cancel")
                    }
                }
            ) {
                DatePicker(
                    state = datePickerState
                )
            }
        }
    }

    companion object {
        private val daysOfWeek = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
    }
}