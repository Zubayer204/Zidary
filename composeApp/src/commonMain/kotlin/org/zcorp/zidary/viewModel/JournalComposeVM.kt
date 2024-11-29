package org.zcorp.zidary.viewModel

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.zcorp.zidary.model.data.JournalFactory

class JournalComposeVM(
    private val repository: JournalFactory
) : ViewModel() {
    private val _state = MutableStateFlow(JournalComposeState())
    val state = _state.asStateFlow()

    private val _events = Channel<JournalComposeEvent>()
    val events = _events.receiveAsFlow()

    fun resetState() {
        _state.update {
            JournalComposeState()
        }
    }

    fun onTitleChanged(title: String) {
        _state.update { it.copy(title = title) }
        _state.update { it.copy(doneButtonState = title.isNotEmpty() && state.value.body.isNotEmpty()) }
    }

    fun onBodyChanged(body: String) {
        _state.update { it.copy(body = body) }
        _state.update { it.copy(doneButtonState = state.value.title.isNotEmpty() && body.isNotEmpty()) }
    }

    fun onEntryTimeChanged(instant: Instant) {
        _state.update { it.copy(entryTime = instant) }
    }

    // Function to load existing entry on Edit page
    fun loadEntry(id: Long) {
        viewModelScope.launch {
            try {
                val entry = repository.getById(id)
                entry.collect { receivedEntry ->
                    _state.update { state ->
                        state.copy(
                            id = receivedEntry.id,
                            title = receivedEntry.title,
                            body = receivedEntry.body,
                            entryTime = receivedEntry.entry_time,
                            isEditMode = true,
                            doneButtonState = true
                        )
                    }
                }
            } catch (e: Exception) {
                _events.send(JournalComposeEvent.ShowError("Failed to load entry: ${e.message}"))
            }
        }
    }

    fun onSaveClick() {
        viewModelScope.launch {
            val currentState = state.value

            if (currentState.title.isBlank()) {
                _events.send(JournalComposeEvent.ShowError("Title cannot be empty"))
                return@launch
            }

            if (currentState.body.isBlank()) {
                _events.send(JournalComposeEvent.ShowError("Body cannot be empty"))
                return@launch
            }

            try {
                if (currentState.isEditMode) {
                    repository.update(
                        id = currentState.id,
                        title = currentState.title,
                        body = currentState.body,
                        entryTime = currentState.entryTime
                    ).collect {entry ->
                        println("Updated Entry: $entry")
                        _events.send(JournalComposeEvent.EntryAdded)
                        _state.update { it.copy(isEditMode = false) }
                    }
                } else {
                    repository.insert(
                        title = currentState.title.trim(),
                        body = currentState.body.trim(),
                        entryTime = currentState.entryTime
                    )
                    _events.send(JournalComposeEvent.EntryAdded)
                }
            } catch (e: Exception) {
                _events.send(JournalComposeEvent.ShowError("Failed to save entry: ${e.message}"))
            }
        }
    }
}

data class JournalComposeState(
    val id: Long = -1,
    val title: String = "",
    val body: String = "",
    val doneButtonState: Boolean = false,
    val entryTime: Instant = Clock.System.now(),
    val isLoading: Boolean = false,
    val isEditMode: Boolean = false
)

sealed class JournalComposeEvent {
    data object EntryAdded : JournalComposeEvent()
    data class ShowError(val message: String) : JournalComposeEvent()
}

