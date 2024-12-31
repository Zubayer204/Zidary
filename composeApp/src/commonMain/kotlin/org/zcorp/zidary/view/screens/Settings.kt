package org.zcorp.zidary.view.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
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
import com.mohamedrejeb.calf.permissions.isGranted
import com.mohamedrejeb.calf.permissions.isNotGranted
import com.mohamedrejeb.calf.permissions.rememberPermissionState
import com.mohamedrejeb.calf.permissions.shouldShowRationale
import com.tweener.alarmee.configuration.AlarmeePlatformConfiguration
import com.tweener.alarmee.rememberAlarmeeScheduler
import org.koin.compose.koinInject
import org.zcorp.zidary.view.components.settings.AppearanceSection
import org.zcorp.zidary.view.components.settings.GeneralSection
import org.zcorp.zidary.view.components.settings.SecuritySection
import org.zcorp.zidary.viewModel.SettingsEvent
import org.zcorp.zidary.viewModel.SettingsVM

class Settings: Screen {
    @OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
    @Composable
    override fun Content() {
        val alarmeePlatformConfiguration = koinInject<AlarmeePlatformConfiguration>()
        val viewModel = koinInject<SettingsVM>()
        val state by viewModel.state.collectAsState()
        val snackbarHostState = remember { SnackbarHostState() }
        val alarmeeScheduler = rememberAlarmeeScheduler(alarmeePlatformConfiguration)
        val notificationPermissionState = rememberPermissionState(Permission.Notification)
        val userEnabledReminder = remember { mutableStateOf(state.generalSettings.writingReminderSet) }

        LaunchedEffect(Unit) {
            viewModel.events.collect { event ->
                when (event) {
                    is SettingsEvent.ShowError -> {
                        snackbarHostState.showSnackbar(event.message)
                    }
                    is SettingsEvent.SettingsUpdated -> {
                        snackbarHostState.showSnackbar(event.message)
                    }
                }
            }
        }

        LaunchedEffect(userEnabledReminder.value) {
            if (userEnabledReminder.value == state.generalSettings.writingReminderSet) return@LaunchedEffect // Skip when the user hasn't changed the setting
            if (userEnabledReminder.value) { // Only request permission if the user actively enabled the reminder
                if (notificationPermissionState.status.isNotGranted) {
                    notificationPermissionState.launchPermissionRequest()
                } else { // Permission already granted
                    viewModel.updateWritingReminderSettings(enabled = true, alarmeeScheduler = alarmeeScheduler)
                }
            } else { // User disabled the reminder
                viewModel.updateWritingReminderSettings(enabled = false, alarmeeScheduler = alarmeeScheduler)
            }
        }

        LaunchedEffect(notificationPermissionState.status) {
            if (userEnabledReminder.value) { // Only react to permission changes if the user intends to have reminders enabled.
                if (notificationPermissionState.status.isGranted) {
                    // If the user enabled reminders and permission is granted, update the setting and schedule
                    viewModel.showSampleNotification(alarmeeScheduler)
                    viewModel.updateWritingReminderSettings(enabled = true, alarmeeScheduler = alarmeeScheduler)

                } else if (notificationPermissionState.status.shouldShowRationale) {
                    snackbarHostState.showSnackbar("Notifications permissions must be granted for reminder functionality to work")
                    // If the user denied permission after rationale, disable the setting.
                    userEnabledReminder.value = false

                } else if (notificationPermissionState.status.isNotGranted && !notificationPermissionState.status.shouldShowRationale) {
                    // User denied permission and doesn't want to see rationale. Disable the setting.
                    userEnabledReminder.value = false

                }
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
