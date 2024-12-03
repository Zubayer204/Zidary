package org.zcorp.zidary.viewModel

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.zcorp.zidary.db.JournalEntry
import org.zcorp.zidary.model.data.JournalFactory

class CalendarVM(private val journalFactory: JournalFactory) : ViewModel() {
    private val _state = MutableStateFlow(CalendarScreenState())
    val state = _state.asStateFlow()

    init {
        loadEntries()
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        onSelectDate(now)
    }

    fun onSelectDate(date: LocalDate) {
        val entries = journalEntries.value.filter {
            val entryDate = it.entry_time.toLocalDateTime(TimeZone.currentSystemDefault()).date
            entryDate == state.selectedDate
        }
        _state.update { it.copy(selectedDate = date) }
    }

    fun hasEntriesForDate(date: LocalDate): Boolean {
        return state.value.entries.any { entry ->
            val entryDate = entry.entry_time.toLocalDateTime(TimeZone.currentSystemDefault()).date
            entryDate == date
        }
    }



    private fun loadEntries() {
        viewModelScope.launch {
            journalFactory.getAll().collect { entries ->
                _state.value = _state.value.copy(
                    entries = entries,
                    isLoading = false,
                )
            }
        }
    }
}

data class CalendarScreenState(
    val entries: List<JournalEntry> = emptyList(),
    val isLoading: Boolean = true,
    val selectedDate: LocalDate? = null
)
