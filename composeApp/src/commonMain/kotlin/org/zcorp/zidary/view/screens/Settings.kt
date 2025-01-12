package org.zcorp.zidary.view.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import com.mohamedrejeb.calf.permissions.ExperimentalPermissionsApi
import com.mohamedrejeb.calf.permissions.Permission
import com.mohamedrejeb.calf.permissions.rememberPermissionState
import com.tweener.alarmee.configuration.AlarmeePlatformConfiguration
import com.tweener.alarmee.rememberAlarmeeScheduler
import org.koin.compose.koinInject
import org.zcorp.zidary.view.components.settings.AppearanceSection
import org.zcorp.zidary.view.components.settings.GeneralSection
import org.zcorp.zidary.view.components.settings.SecuritySection
import org.zcorp.zidary.viewModel.SettingsEvent
import org.zcorp.zidary.viewModel.SettingsVM

class Settings : Screen {
    @OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
    @Composable
    override fun Content() {
        val alarmeePlatformConfiguration = koinInject<AlarmeePlatformConfiguration>()
        val viewModel = koinInject<SettingsVM>()
        val state by viewModel.state.collectAsState()
        val snackbarHostState = remember { SnackbarHostState() }
        val alarmeeScheduler = rememberAlarmeeScheduler(alarmeePlatformConfiguration)
        val notificationPermissionState = rememberPermissionState(Permission.Notification)
        val userEnabledReminder = remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            viewModel.events.collect { event ->
                when (event) {
                    is SettingsEvent.ShowError -> {
                        snackbarHostState.showSnackbar(
                            message = event.message,
                            withDismissAction = true
                        )
                    }

                    is SettingsEvent.SettingsUpdated -> {
                        snackbarHostState.showSnackbar(
                            message = event.message,
                            withDismissAction = true
                        )
                    }
                }
            }
        }

        LaunchedEffect(notificationPermissionState.status) {
            if (userEnabledReminder.value && !state.generalSettings.writingReminderSet) {
                viewModel.handlePermissionResult(
                    notificationPermissionState.status,
                    alarmeeScheduler
                )
            }
        }

        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                TopAppBar(
                    title = { Text("Settings") }
                )
            }
        ) { padding ->
            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    item {
                        GeneralSection(
                            settings = state.generalSettings,
                            onWritingReminderSettingsChanged = { enabled ->
                                userEnabledReminder.value = enabled
                                viewModel.handleWritingReminderToggle(
                                    enabled = enabled,
                                    permissionStatus = notificationPermissionState.status,
                                    alarmeeScheduler = alarmeeScheduler,
                                    requestPermission = notificationPermissionState::launchPermissionRequest
                                )
                            }
                        )
                    }

                    item {
                        AppearanceSection(
                            settings = state.appearanceSettings,
                            onThemeChanged = viewModel::updateTheme,
                            onFontFamilyChanged = viewModel::updateFontFamily,
                            onAccentColorChanged = viewModel::updateAccentColor
                        )
                    }

                    item {
                        SecuritySection(
                            settings = state.securitySettings,
                            onAppLockChanged = viewModel::updateAppLock,
                            onHidePreviewsChanged = viewModel::updateHideEntryPreviews
                        )
                    }
                }
            }
        }
    }
}
