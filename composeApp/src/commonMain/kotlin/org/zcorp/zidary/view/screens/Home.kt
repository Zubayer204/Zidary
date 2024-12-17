package org.zcorp.zidary.view.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.zcorp.zidary.utils.formatDateTime
import org.zcorp.zidary.view.components.DeleteConfirmationDialog
import org.zcorp.zidary.view.components.GlowingFAB
import org.zcorp.zidary.view.components.JournalEntryBottomSheet
import org.zcorp.zidary.view.components.JournalEntryCard
import org.zcorp.zidary.view.components.TextEntryAnimation
import org.zcorp.zidary.view.theme.AppTypography
import org.zcorp.zidary.view.theme.GreatVibes
import org.zcorp.zidary.viewModel.HomeScreenEvent
import org.zcorp.zidary.viewModel.HomeVM
import org.zcorp.zidary.viewModel.JournalComposeVM

class Home(private val viewModel: HomeVM, private val journalComposeVM: JournalComposeVM): Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val state by viewModel.state.collectAsState()

        val sheetState = rememberModalBottomSheetState()
        val snackbarHostState = remember { SnackbarHostState() }
        var showDeleteConfirmation by remember { mutableStateOf(false) }
        var entryToDelete by remember { mutableStateOf(-1L) }

        val typography = AppTypography()
        val zoneId = TimeZone.currentSystemDefault()

        LaunchedEffect(Unit) {
            viewModel.events.collect { event ->
                when (event) {
                    is HomeScreenEvent.NavigateToEdit -> {
                        navigator.push(JournalEdit(
                            event.id,
                            journalComposeVM,
                            { navigator.pop() }
                        ))
                    }
                    is HomeScreenEvent.NavigateToView -> {
                        navigator.push(JournalView(event.entry, { navigator.pop() }))
                    }
                    is HomeScreenEvent.EntryDeleted -> {
                        snackbarHostState.showSnackbar("Entry Deleted")
                    }
                    is HomeScreenEvent.ShowError -> {
                        snackbarHostState.showSnackbar("Error: ${event.message}")
                    }
                }
            }
        }

        Box (
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (viewModel.totalEntries() == 0L) {
                TextEntryAnimation(
                    headlineText = "Let's write",
                    subheadlineTextList = listOf("", "thoughts...", "memories...", "stories...", "life..."),
                    headlineTextStyle = TextStyle(
                        fontFamily = GreatVibes(),
                        fontSize = 84.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    ),
                    subheadlineTextStyle = TextStyle(
                        fontFamily = GreatVibes(),
                        fontSize = 64.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    ),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                )
            }
            LazyColumn (
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 16.dp),
            ) {
                if (state.isLoading) {
                    item {
                        CircularProgressIndicator(
                            modifier = Modifier.fillMaxWidth()
                                .wrapContentSize()
                        )
                    }
                }

                items(items = state.journalEntries, key = { it.id }) { entry ->
                    JournalEntryCard(
                        entry.title,
                        entry.body,
                        formatDateTime(entry.entry_time.toLocalDateTime(zoneId)),
                        typography,
                        onClick = { viewModel.onViewEntryClick(entry) },
                        onLongClick = { viewModel.onEntryLongPress(entry) },
                        modifier = Modifier.animateItem()
                    )
                }
            }
            GlowingFAB(
                onClick = {
                    navigator.push(JournalCompose(journalComposeVM, onNavigateBack = ({ navigator.pop() })))
                },
                modifier = Modifier
                    .size(96.dp)
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
                    .zIndex(1f),
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
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
    }
}