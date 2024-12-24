package org.zcorp.zidary.view.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.koin.compose.koinInject
import org.zcorp.zidary.model.auth.AuthManager
import org.zcorp.zidary.model.auth.getPlatformContext
import org.zcorp.zidary.viewModel.SettingsManager

@Composable
fun AuthenticationWrapper(
    settingsManager: SettingsManager = koinInject(),
    content: @Composable () -> Unit
) {
    val platformContext = getPlatformContext()
    val authManager = AuthManager(settingsManager, platformContext)
    var isAuthenticated by remember { mutableStateOf(false) }
    var showAuthDialog by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        if (showAuthDialog) {
            isAuthenticated = authManager.authenticateIfRequired()
            showAuthDialog = false
        }
    }

    if (isAuthenticated) {
        content()
    } else if (!showAuthDialog) {
        // Show an error or lock screen
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Authentication required")
            Button(
                onClick = { showAuthDialog = true }
            ) {
                Text("Try Again")
            }
        }
    }
}
