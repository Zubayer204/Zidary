package org.zcorp.zidary.view.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import org.zcorp.zidary.formatDateTime
import org.zcorp.zidary.getTotalDaysInMonth
import org.zcorp.zidary.view.components.CalendarDay
import org.zcorp.zidary.view.components.JournalEntryCard
import org.zcorp.zidary.view.theme.AppTypography
import org.zcorp.zidary.viewModel.CalendarVM

class Calendar(private val viewModel: CalendarVM): Screen {
    @Composable
    override fun Content() {
        val typography = AppTypography()

        // State for selected date and month
        val state by viewModel.state.collectAsState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
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
                    style = typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
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

            // Calendar grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp)
            ) {
                // Weekday headers
                items(7) { dayIndex ->
                    val dayName = daysOfWeek[dayIndex]
                    Text(
                        text = dayName,
                        modifier = Modifier.padding(8.dp),
                        style = typography.labelMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                // Calendar dates
                val firstDayOfMonth = LocalDate(state.currentYear, state.currentMonth, 1)
                val totalDaysInMonth = getTotalDaysInMonth(firstDayOfMonth)
                val firstDayOfWeek = firstDayOfMonth.dayOfWeek.ordinal + 1 // initial ordinal is 0, so we add one to it

                // Empty spaces before first day
                items(firstDayOfWeek) {
                    Box(modifier = Modifier.padding(8.dp))
                }

                // Actual dates
                println("datesWithEntries: ${state.datesWithEntries}")
                items(totalDaysInMonth) { day ->
                    println("125 ${state.currentYear} ${state.currentMonth} $day")
                    val date = LocalDate(state.currentYear, state.currentMonth, day + 1)
                    val isSelected = date == state.selectedDate
                    val hasEntries = state.datesWithEntries.contains(date)

                    println("$date HasEntries: $hasEntries")

                    CalendarDay(
                        day = day + 1,
                        isSelected = isSelected,
                        hasEntries = hasEntries,
                        onClick = { viewModel.onSelectDate(date) }
                    )
                }
            }

            HorizontalDivider(
                color = MaterialTheme.colorScheme.onBackground.copy(0.5f)
            )

            LazyColumn {
                items(state.selectedDateEntries) { entry ->
                    JournalEntryCard(
                        title = entry.title,
                        content = entry.body,
                        datetime = formatDateTime(entry.entry_time.toLocalDateTime(TimeZone.currentSystemDefault())),
                        typography = typography
                    )
                }
            }
        }
    }

    companion object {
        private val daysOfWeek = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
    }
}