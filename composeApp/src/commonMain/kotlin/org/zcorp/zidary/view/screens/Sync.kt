package org.zcorp.zidary.view.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import io.github.vinceglb.filekit.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.compose.rememberFileSaverLauncher
import io.github.vinceglb.filekit.core.PickerType
import org.koin.compose.koinInject
import org.zcorp.zidary.view.components.DateRangePickerDialogue
import org.zcorp.zidary.viewModel.SyncScreenEvent
import org.zcorp.zidary.viewModel.SyncVM

object Sync: Screen {
    @Composable
    override fun Content() {
        val viewModel = koinInject<SyncVM>()
        val state by viewModel.state.collectAsState()
        val focusManager = LocalFocusManager.current
        var isDateRangePickerVisible by remember { mutableStateOf(false) }
        var showPassphraseInfo by remember { mutableStateOf(false) }
        val snackbarHostState = remember { SnackbarHostState() }
        val fileSaverLauncher = rememberFileSaverLauncher { file ->
            if (file != null) {
                viewModel.dataExported(file)
            }
            println("Saved file: $file")
        }
        val filePickerLauncher = rememberFilePickerLauncher(type = PickerType.File(listOf("zidary"))) { file ->
            if (file != null) {
                viewModel.pickFileForImport(file)
            }
            println("Picked file: $file")
        }

        LaunchedEffect(Unit) {
            viewModel.events.collect  { event ->
                when (event) {
                    is SyncScreenEvent.ShowError -> {
                        snackbarHostState.showSnackbar("Error: ${event.message}")
                    }
                    is SyncScreenEvent.ExportReady -> {
                        fileSaverLauncher.launch(
                            bytes = event.data,
                            baseName = "journal_export",
                            extension = "zidary"
                        )
                    }
                    is SyncScreenEvent.ExportDone -> {
                        snackbarHostState.showSnackbar("File saved to ${event.file.name}")
                    }
                    is SyncScreenEvent.ImportStart -> {
                        filePickerLauncher.launch()
                    }
                    is SyncScreenEvent.ImportDone -> {
                        snackbarHostState.showSnackbar("Imported ${event.entriesImported} entries")
                    }
                }
            }
        }

        Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) {data ->
            Card(
                modifier = Modifier
                    .padding(16.dp)
                    .wrapContentSize(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = data.visuals.message,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    if (data.visuals.actionLabel != null) {
                        TextButton(
                            onClick = { data.performAction() }
                        ) {
                            Text(
                                text = data.visuals.actionLabel!!,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

        } }) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Export Journal",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        // Date Range selector
                        Box {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { isDateRangePickerVisible = true },
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Date Range",
                                        style = MaterialTheme.typography.titleLarge
                                    )

                                    Text(
                                        text = if (state.startDate != null) "${state.startDate} <-> ${state.endDate}" else "All Data",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowRight,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier
                                        .size(48.dp)
                                )
                            }
                        }
                        // Passphrase Input
                        Box {
                            Column {
                                Text(
                                    text = "Encryption Passphrase",
                                    style = MaterialTheme.typography.titleLarge
                                )

                                OutlinedTextField(
                                    modifier = Modifier.fillMaxWidth(),
                                    value = state.encryptionPassphrase,
                                    onValueChange = { viewModel.updateEncryptionPassphrase(it) },
                                    label = { Text("Enter passphrase") },
                                    isError = state.encryptionPassphrase.length < viewModel.REQUIRED_PASSPHRASE_LENGTH && state.enabledEncryptionPassphrase
                                )

                                if (state.encryptionPassphrase.length < viewModel.REQUIRED_PASSPHRASE_LENGTH && state.enabledEncryptionPassphrase) {
                                    Text(
                                        text = "Passphrase must be at least ${viewModel.REQUIRED_PASSPHRASE_LENGTH} characters",
                                        color = MaterialTheme.colorScheme.error,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }

                                TextButton(
                                    onClick = { showPassphraseInfo = true },
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text("Why do I need a passphrase?")
                                }
                            }
                        }

                        Button(
                            onClick = {
                                focusManager.clearFocus()
                                viewModel.exportData()
                            },
                            enabled = state.encryptionPassphrase.length >= viewModel.REQUIRED_PASSPHRASE_LENGTH,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Export Data")
                        }

                    }
                }
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Import Journal",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Box {
                            Column {
                                Text(
                                    text = "Decryption Passphrase",
                                    style = MaterialTheme.typography.titleLarge
                                )

                                OutlinedTextField(
                                    modifier = Modifier.fillMaxWidth(),
                                    value = state.decryptionPassphrase,
                                    onValueChange = { viewModel.updateDecryptionPassphrase(it) },
                                    label = { Text("Enter passphrase") },
                                    isError = state.decryptionPassphrase.length < viewModel.REQUIRED_PASSPHRASE_LENGTH && state.enabledDecryptionPassphrase
                                )

                                if (state.decryptionPassphrase.length < viewModel.REQUIRED_PASSPHRASE_LENGTH && state.enabledDecryptionPassphrase) {
                                    Text(
                                        text = "Passphrase must be at least ${viewModel.REQUIRED_PASSPHRASE_LENGTH} characters",
                                        color = MaterialTheme.colorScheme.error,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }

                        // Choose File Button
                        OutlinedButton(
                            onClick = {
                                focusManager.clearFocus()
                                viewModel.startImport()
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(if (state.pickedFileForImport != null) state.pickedFileForImport!!.name else "Choose File")
                        }

                        Button(
                            onClick = {
                                focusManager.clearFocus()
                                viewModel.importData()
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Import Data")
                        }
                    }

                }

            }

            if (isDateRangePickerVisible) {
                DateRangePickerDialogue(
                    onDismiss = { isDateRangePickerVisible = false },
                    onConfirm = {startDate, endDate ->
                        viewModel.updateDateRange(startDate, endDate)
                        isDateRangePickerVisible = false
                    }
                )
            }

            if (showPassphraseInfo) {
                AlertDialog(
                    onDismissRequest = { showPassphraseInfo = false },
                    title = { Text("About Encryption") },
                    text = {
                        Text(
                            "Your journal entries will be encrypted with this passphrase. " +
                                    "Make sure to remember it, as you'll need it to restore your entries. " +
                                    "The passphrase cannot be recovered if lost."
                        )
                    },
                    confirmButton = {
                        TextButton(
                            onClick = { showPassphraseInfo = false }
                        ) {
                            Text("Got it")
                        }
                    }
                )
            }
        }
    }
}
