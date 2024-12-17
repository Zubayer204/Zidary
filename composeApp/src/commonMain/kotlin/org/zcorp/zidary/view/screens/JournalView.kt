package org.zcorp.zidary.view.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.zcorp.zidary.db.JournalEntry
import org.zcorp.zidary.utils.formatDateTime
import org.zcorp.zidary.view.theme.AppTypography

class JournalView(
    private val entry: JournalEntry,
    private val onNavigateBack: () -> Unit
) : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val typography = AppTypography()

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("") },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                Icons.AutoMirrored.Default.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    },
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Entry Time
                Text(
                    text = formatDateTime(entry.entry_time.toLocalDateTime(TimeZone.currentSystemDefault())),
                    style = typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                HorizontalDivider(
                    Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.onSecondary
                )

                // Title
                Text(
                    text = entry.title,
                    style = typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Body
                Text(
                    text = entry.body,
                    style = typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}