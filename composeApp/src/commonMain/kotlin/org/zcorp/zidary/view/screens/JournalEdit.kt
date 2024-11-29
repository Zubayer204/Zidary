package org.zcorp.zidary.view.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import cafe.adriel.voyager.core.screen.Screen
import org.zcorp.zidary.viewModel.JournalComposeVM

class JournalEdit(
    private val entryId: Long,
    private val viewModel: JournalComposeVM,
    private val onNavigateBack: () -> Unit
): Screen {
    @Composable
    override fun Content() {
        LaunchedEffect(entryId) {
            viewModel.loadEntry(entryId)
        }

        DisposableEffect(Unit) {
            onDispose {
                viewModel.resetState()
            }
        }

        JournalCompose(viewModel, onNavigateBack).Content()
    }
}