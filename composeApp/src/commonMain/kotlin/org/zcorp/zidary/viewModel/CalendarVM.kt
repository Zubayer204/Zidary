package org.zcorp.zidary.viewModel

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.zcorp.zidary.db.JournalEntry
import org.zcorp.zidary.model.data.JournalFactory

class CalendarVM(private val journalFactory: JournalFactory) : ViewModel() {
    private val _state = MutableStateFlow(CalendarScreenState())
    val state = _state.asStateFlow()

    private var currentMonthJob: Job? = null
    private var selectedDateJob: Job? = null

    init {
        loadEntriesForDate(state.value.selectedDate)
        loadEntriesForMonth(state.value.currentYear, state.value.currentMonth)
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        onSelectDate(now)
    }

    fun onSelectDate(date: LocalDate) {
        viewModelScope.launch {
            _state.update { it.copy(selectedDate = date) }
            loadEntriesForDate(date)
        }
    }

//    fun hasEntriesForDate(date: LocalDate): Boolean {
//        return state.value.entries.any { entry ->
//            val entryDate = entry.entry_time.toLocalDateTime(TimeZone.currentSystemDefault()).date
//            entryDate == date
//        }
//    }

    fun changeMonth(year: Int, month: Month) {
        viewModelScope.launch {
            _state.update { it.copy(
                currentYear = year,
                currentMonth = month,
                isLoading = true
            ) }
            loadEntriesForMonth(year, month)
        }
    }

    private fun loadEntriesForMonth(year: Int, month: Month) {
        currentMonthJob?.cancel()
        currentMonthJob = viewModelScope.launch {
            journalFactory.getEntryDatesForMonth(year, month)
                .collect { dates ->
                    _state.update { it.copy(
                        datesWithEntries = dates,
                        isLoading = false
                    ) }
                }
        }
    }

    private fun loadEntriesForDate(date: LocalDate) {
        selectedDateJob?.cancel()
        selectedDateJob = viewModelScope.launch {
            journalFactory.getEntriesByDate(date)
                .collect { entries ->
                    _state.update { it.copy(
                        selectedDateEntries = entries
                    ) }
                }
        }
    }

    override fun onCleared() {
        super.onCleared()
        currentMonthJob?.cancel()
        selectedDateJob?.cancel()
    }
}

data class CalendarScreenState(
    val datesWithEntries: List<LocalDate> = emptyList(),
    val selectedDateEntries: List<JournalEntry> = emptyList(),
    val isLoading: Boolean = true,
    val currentYear: Int = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault()).year,
    val currentMonth: Month = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault()).month,
    val selectedDate: LocalDate = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault()).date,
    )
