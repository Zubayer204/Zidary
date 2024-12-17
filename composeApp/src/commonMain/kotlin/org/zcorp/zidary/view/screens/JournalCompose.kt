package org.zcorp.zidary.view.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import org.zcorp.zidary.view.components.DateTimeSelector
import org.zcorp.zidary.view.theme.AppTypography
import org.zcorp.zidary.viewModel.JournalComposeEvent
import org.zcorp.zidary.viewModel.JournalComposeVM

private const val DEFAULT_ANIMATION_DURATION = 700 // in milliseconds

class JournalCompose(private val viewModel: JournalComposeVM, private val onNavigateBack: () -> Unit): Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val typography = AppTypography()

        val state by viewModel.state.collectAsState()
        val snackBarHostState = remember { SnackbarHostState() }
        val bodyFocusRequester = remember { FocusRequester() }

        val appBarTitle = if (state.isEditMode) "Edit Entry" else "New Entry"

        LaunchedEffect(Unit) {
            viewModel.events.collect {event ->
                when (event) {
                    is JournalComposeEvent.EntryAdded -> {
                        onNavigateBack()
                    }
                    is JournalComposeEvent.ShowError -> {
                        snackBarHostState.showSnackbar(event.message)
                    }
                }
            }
        }

        Scaffold(topBar = {
            TopAppBar(
                title = { Text(appBarTitle) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }) {padding ->
            Column (
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().height(36.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    DateTimeSelector(
                        typography,
                        initialDateTime = state.entryTime,
                        onDateTimeSelected = viewModel::onEntryTimeChanged,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    AnimatedVisibility(
                        visible = state.doneButtonState,
                        enter = slideInHorizontally(
                            animationSpec = tween(DEFAULT_ANIMATION_DURATION),
                            // slide in from right
                            initialOffsetX = { width -> width }
                        ) + fadeIn(
                            animationSpec = tween(DEFAULT_ANIMATION_DURATION)
                        ),
                        exit = slideOutHorizontally(
                            animationSpec = tween(DEFAULT_ANIMATION_DURATION),
                            // slide out to right
                            targetOffsetX = { width -> width }
                        ) + fadeOut(
                            animationSpec = tween(DEFAULT_ANIMATION_DURATION)
                        ),
                    ) {
                        Button(
                            onClick = {
                                viewModel.onSaveClick()
                            },
                            content = { Text("Done") },
                            enabled = state.doneButtonState,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                                disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                                disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.3f)
                            ),
                        )
                    }
                }
                HorizontalDivider(
                    Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.onSecondary
                )
                Text(
                    "Title",
                    style = typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                OutlinedTextField(
                    value = state.title,
                    onValueChange = viewModel::onTitleChanged,
                    placeholder = { Text("Enter a title") },
                    maxLines = 2,
                    modifier = Modifier
                        .fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        focusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f),
                    ),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            bodyFocusRequester.requestFocus()
                        }
                    )
                )
                Text(
                    "Body",
                    style = typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                OutlinedTextField(
                    value = state.body,
                    onValueChange = viewModel::onBodyChanged,
                    placeholder = { Text("Let's hear it...") },
                    maxLines = 1000,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        focusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f),
                    ),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { viewModel.onSaveClick() }
                    )
                )
            }
        }

    }
}
