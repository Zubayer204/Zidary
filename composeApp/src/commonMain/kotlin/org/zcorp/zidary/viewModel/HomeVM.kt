package org.zcorp.zidary.viewModel

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.zcorp.zidary.db.JournalEntry
import org.zcorp.zidary.model.data.JournalFactory

class HomeVM(private val journalFactory: JournalFactory) : ViewModel() {
    private val _state = MutableStateFlow(HomeScreenState())
    val state = _state.asStateFlow()

    private val _events = Channel<HomeScreenEvent>()
    val events = _events.receiveAsFlow()

    fun onEntryLongPress(entry: JournalEntry) {
        _state.update { it.copy(selectedEntry = entry) }
    }

    fun onDismissSheet() {
        _state.update { it.copy(selectedEntry = null) }
    }

    fun onEditClick(id: Long) {
        viewModelScope.launch {
            try {
                _events.send(HomeScreenEvent.NavigateToEdit(id))
            } catch (e: Exception) {
                _events.send(HomeScreenEvent.ShowError("Failed to navigate to edit: ${e.message}"))
            }
        }
    }

    fun onViewEntryClick(entry: JournalEntry) {
        viewModelScope.launch {
            try {
                _events.send(HomeScreenEvent.NavigateToView(entry))
            } catch (e: Exception) {
                _events.send(HomeScreenEvent.ShowError("Failed to navigate to view: ${e.message}"))
            }
        }
    }


    init {
        viewModelScope.launch {
            loadEntries()
        }
    }

    private suspend fun loadEntries() {
        journalFactory.getAll()
            .collect { entries ->
                _state.update {
                    it.copy(
                        journalEntries = entries,
                        isLoading = false
                    )
                }
            }
    }

    fun deleteEntry(id: Long) {
        viewModelScope.launch {
            try {
                journalFactory.delete(id)
                _state.update { it.copy(selectedEntry = null) }
                _events.send(HomeScreenEvent.EntryDeleted)
            } catch (e: Exception) {
                _events.send(HomeScreenEvent.ShowError("Failed to delete entry: ${e.message}"))
            }
        }
    }

    fun totalEntries(): Long {
        return journalFactory.getTotalEntries()
    }
}

data class HomeScreenState(
    val journalEntries: List<JournalEntry> = emptyList(),
    val isLoading: Boolean = true,
    val selectedEntry: JournalEntry? = null
)

sealed class HomeScreenEvent {
    data object EntryDeleted : HomeScreenEvent()
    data class ShowError(val message: String) : HomeScreenEvent()
    data class NavigateToEdit(val id: Long) : HomeScreenEvent()
    data class NavigateToView(val entry: JournalEntry) : HomeScreenEvent()
}

