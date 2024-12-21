package org.zcorp.zidary.viewModel

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import io.github.vinceglb.filekit.core.PlatformFile
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.zcorp.zidary.model.data.ExportData
import org.zcorp.zidary.model.data.ExportedEntry
import org.zcorp.zidary.model.data.JournalFactory
import org.zcorp.zidary.utils.CryptoResult
import org.zcorp.zidary.utils.Encryption
import org.zcorp.zidary.utils.epochMillisecondsToLocalDate

class SyncVM(private val journalFactory: JournalFactory): ViewModel() {
    val REQUIRED_PASSPHRASE_LENGTH = 6
    private val timeZone = TimeZone.currentSystemDefault()
    private val _state = MutableStateFlow(SyncScreenState())
    val state = _state.asStateFlow()

    private val _events = Channel<SyncScreenEvent>()
    val events = _events.receiveAsFlow()

    // Design elements
    val passphraseKeyboardOptions = KeyboardOptions(
        capitalization = KeyboardCapitalization.None,
        keyboardType = KeyboardType.Password,
    )

    fun updateDateRange(startDate: Long?, endDate: Long?) {
        val currentDate = Clock.System.now().toEpochMilliseconds()

        _state.update { it.copy(
            startDate = startDate?.let { stDate -> epochMillisecondsToLocalDate(stDate, timeZone) },
            endDate = endDate?.let { edDate -> epochMillisecondsToLocalDate(edDate, timeZone) } ?: epochMillisecondsToLocalDate(currentDate, timeZone)
        ) }
    }

    fun updateEncryptionPassphrase(passphrase: String) {
        _state.update { it.copy( encryptionPassphrase = passphrase, enabledEncryptionPassphrase = true ) }
    }

    fun updateDecryptionPassphrase(passphrase: String) {
        _state.update { it.copy( decryptionPassphrase = passphrase, enabledDecryptionPassphrase = true ) }
    }

    fun pickFileForImport(file: PlatformFile) {
        _state.update { it.copy(pickedFileForImport = file) }
    }

    fun exportData() {
        viewModelScope.launch {
            try {
                println("Exporting data")
                if (state.value.encryptionPassphrase.length < REQUIRED_PASSPHRASE_LENGTH) {
                    _events.send(SyncScreenEvent.ShowError("Passphrase must be at least 6 characters long"))
                    return@launch
                }

                println("Starting database query...")
                val exportableEntries = if (state.value.startDate != null && state.value.endDate != null) {
                    println("Querying date range: ${state.value.startDate} to ${state.value.endDate}")
                    val entriesFlow = journalFactory.getEntriesByDateRange(state.value.startDate!!, state.value.endDate!!)
                    println("Got flow, converting to list...")
                    entriesFlow.first()
                } else {
                    println("Querying all entries...")
                    val entriesFlow = journalFactory.getAll()
                    println("Got flow, converting to list...")
                    entriesFlow.first()
                }

                println("Data collected from DB")
                println("Number of entries: ${exportableEntries.size}")

                val exportData = ExportData(
                    entries = exportableEntries.map { entry ->
                        ExportedEntry(
                            id = entry.id,
                            title = entry.title,
                            body = entry.body,
                            entryTime = entry.entry_time,
                            createdAt = entry.created_at,
                            modifiedAt = entry.modified_at
                        )
                    }
                )

                println("Data collected from DB")

                val jsonString = Json.encodeToString(exportData)
                val encrypted = Encryption.encrypt(jsonString.encodeToByteArray(), state.value.encryptionPassphrase)

                println("Encrypted data: $encrypted")

                if (encrypted is CryptoResult.Error) throw encrypted.exception

                val data = (encrypted as CryptoResult.Success).data
                println("Data exported: sending event")
                _events.send(SyncScreenEvent.ExportReady(data))

            } catch (e: Exception) {
                _events.send(SyncScreenEvent.ShowError("Export failed: ${e.message ?: "Unknown error"}"))
            }
        }
    }


    fun dataExported(file: PlatformFile) {
        // Clear the encryption passphrase
        _state.update {
            it.copy(encryptionPassphrase = "", enabledEncryptionPassphrase = false, isExporting = false)
        }
        viewModelScope.launch {
            _events.send(SyncScreenEvent.ExportDone(file))
        }
    }

    fun importData() {
        if (state.value.pickedFileForImport == null) {
            viewModelScope.launch {
                _events.send(SyncScreenEvent.ShowError("No file selected for import"))
            }
            return
        } else if (state.value.decryptionPassphrase.length < REQUIRED_PASSPHRASE_LENGTH) {
            viewModelScope.launch {
                _events.send(SyncScreenEvent.ShowError("Passphrase must be at least 6 characters long"))
            }
            return
        }

        println("Importing data from file: ${state.value.pickedFileForImport!!.name}")

        viewModelScope.launch {
            Encryption.decrypt(state.value.pickedFileForImport!!.readBytes(), state.value.decryptionPassphrase).let { result ->
                when (result) {
                    is CryptoResult.Error -> {
                        if (result.exception.message?.contains("BAD_DECRYPT") == true) {
                            _events.send(SyncScreenEvent.ShowError("Incorrect passphrase"))
                        } else {
                            _events.send(SyncScreenEvent.ShowError("Error decrypting file: ${result.exception.message ?: "Unknown error"}"))
                        }
                    }
                    is CryptoResult.Success -> {
                        val decryptedData = result.data.decodeToString()
                        println("Decrypted data: $decryptedData")
                        try {
                            val importData = Json.decodeFromString(ExportData.serializer(), decryptedData)
                            println("Imported data: $importData")
                            for (entry in importData.entries) {
                                journalFactory.upsert(
                                    entry.id,
                                    entry.title,
                                    entry.body,
                                    entry.entryTime,
                                    entry.createdAt,
                                    entry.modifiedAt
                                )
                            }
                            // Clear the picked file and passphrases
                            _state.update {
                                it.copy(pickedFileForImport = null, decryptionPassphrase = "", enabledDecryptionPassphrase = false)
                            }
                            _events.send(SyncScreenEvent.ImportDone(importData.entries.size))
                        } catch (e: Exception) {
                            _events.send(SyncScreenEvent.ShowError("Error parsing imported data: ${e.message ?: "Unknown error"}"))
                        }
                    }
                }
            }
        }
    }

    fun startImport() {
        viewModelScope.launch {
            _events.send(SyncScreenEvent.ImportStart)
        }
    }
    fun errorLoadingFile() {
        viewModelScope.launch {
            _events.send(SyncScreenEvent.ShowError("Error loading file"))
        }
    }
}

data class SyncScreenState(
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
    val encryptionPassphrase: String = "",
    val enabledEncryptionPassphrase: Boolean = false,
    val decryptionPassphrase: String = "",
    val enabledDecryptionPassphrase: Boolean = false,
    val pickedFileForImport: PlatformFile? = null,
    val isExporting: Boolean = false
)

sealed class SyncScreenEvent {
    data class ShowError(val message: String) : SyncScreenEvent()
    data class ExportReady(val data: ByteArray) : SyncScreenEvent() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false

            other as ExportReady

            return data.contentEquals(other.data)
        }

        override fun hashCode(): Int {
            return data.contentHashCode()
        }
    }
    data class ExportDone(val file: PlatformFile): SyncScreenEvent()
    data object ImportStart: SyncScreenEvent()
    data class ImportDone(val entriesImported: Int): SyncScreenEvent()
}
