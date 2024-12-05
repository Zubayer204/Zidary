package org.zcorp.zidary.viewModel

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
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

    private val _events = Channel<CalendarScreenEvent>()
    val events = _events.receiveAsFlow()

    private var currentMonthJob: Job? = null
    private var selectedDateJob: Job? = null

    init {
        loadEntriesForDate(state.value.selectedDate)
        loadEntriesForMonth(state.value.currentYear, state.value.currentMonth)
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        onSelectDate(now)
    }

    fun onDatePickerStatusChange(show: Boolean) {
        _state.update { it.copy(showDatePicker = show) }
    }

    fun onEntryLongPress(entry: JournalEntry) {
        _state.update { it.copy(selectedEntry = entry) }
    }

    fun onDismissSheet() {
        _state.update { it.copy(selectedEntry = null) }
    }

    fun onEditClick(id: Long) {
        viewModelScope.launch {
            try {
                _events.send(CalendarScreenEvent.NavigateToEdit(id))
            } catch (e: Exception) {
                _events.send(CalendarScreenEvent.ShowError("Failed to navigate to edit: ${e.message}"))
            }
        }
    }

    fun onViewEntryClick(entry: JournalEntry) {
        viewModelScope.launch {
            try {
                _events.send(CalendarScreenEvent.NavigateToView(entry))
            } catch (e: Exception) {
                _events.send(CalendarScreenEvent.ShowError("Failed to navigate to view: ${e.message}"))
            }
        }
    }

    fun onSelectDate(date: LocalDate) {
        viewModelScope.launch {
            _state.update { it.copy(selectedDate = date) }
            loadEntriesForDate(date)
        }
    }

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

    fun deleteEntry(id: Long) {
        viewModelScope.launch {
            try {
                journalFactory.delete(id)
                _state.update { it.copy(selectedEntry = null) }
                _events.send(CalendarScreenEvent.EntryDeleted)
            } catch (e: Exception) {
                _events.send(CalendarScreenEvent.ShowError("Failed to delete entry: ${e.message}"))
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
    val selectedEntry: JournalEntry? = null,
    val isLoading: Boolean = true,
    val showDatePicker: Boolean = false,
    val currentYear: Int = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault()).year,
    val currentMonth: Month = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault()).month,
    val selectedDate: LocalDate = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault()).date,
    )

sealed class CalendarScreenEvent {
    data object EntryDeleted: CalendarScreenEvent()
    data class ShowError(val message: String) : CalendarScreenEvent()
    data class NavigateToEdit(val id: Long) : CalendarScreenEvent()
    data class NavigateToView(val entry: JournalEntry) : CalendarScreenEvent()
}
