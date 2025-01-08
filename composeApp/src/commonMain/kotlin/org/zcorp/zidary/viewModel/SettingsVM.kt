package org.zcorp.zidary.viewModel

import com.mohamedrejeb.calf.permissions.ExperimentalPermissionsApi
import com.mohamedrejeb.calf.permissions.PermissionStatus
import com.mohamedrejeb.calf.permissions.isGranted
import com.mohamedrejeb.calf.permissions.isNotGranted
import com.mohamedrejeb.calf.permissions.shouldShowRationale
import com.tweener.alarmee.Alarmee
import com.tweener.alarmee.AlarmeeScheduler
import com.tweener.alarmee.AndroidNotificationConfiguration
import com.tweener.alarmee.AndroidNotificationPriority
import com.tweener.alarmee.RepeatInterval
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.zcorp.zidary.model.data.AppearanceSettings
import org.zcorp.zidary.model.data.AvailableFontFamily
import org.zcorp.zidary.model.data.GeneralSettings
import org.zcorp.zidary.model.data.SecuritySettings
import org.zcorp.zidary.model.data.ThemeMode

class SettingsVM(
    private val settingsManager: SettingsManager,
): ViewModel() {
    private val alarmeeWritingReminderUUID = "writing_reminder"
    private val currentTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

    private val _state = MutableStateFlow(SettingsState())
    val state = _state.asStateFlow()

    private val _events = Channel<SettingsEvent>()
    val events = _events.receiveAsFlow()

    init {
        viewModelScope.launch {
            loadSettings()
        }
    }

    private suspend fun loadSettings() {
        try {
            _state.update {
                it.copy(
                    generalSettings = settingsManager.generalSettings.value,
                    appearanceSettings = settingsManager.appearanceSettings.value,
                    securitySettings = settingsManager.securitySettings.value,
                    isLoading = false
                )
            }
        } catch (e: Exception) {
            _events.send(SettingsEvent.ShowError("Failed to load settings: ${e.message}"))
        }
    }

    private fun showSampleNotification(alarmeeScheduler: AlarmeeScheduler) {
        alarmeeScheduler.push(
            alarmee = Alarmee(
                uuid = "Test",
                notificationTitle = "Notification Sample",
                notificationBody = "This is how your notification will look like",
                androidNotificationConfiguration = AndroidNotificationConfiguration(
                    priority = AndroidNotificationPriority.DEFAULT,
                    channelId = "dailyWritingReminder",
                )
            )
        )
    }

    @OptIn(ExperimentalPermissionsApi::class)
    fun handleWritingReminderToggle(
        enabled: Boolean,
        permissionStatus: PermissionStatus,
        alarmeeScheduler: AlarmeeScheduler,
        requestPermission: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                if (enabled) {
                    when {
                        permissionStatus.isGranted -> {
                            println("permission granted")
                            showSampleNotification(alarmeeScheduler)
                            enableWritingReminder(alarmeeScheduler)
                        }
                        else -> {
                            print("requesting permission")
                            requestPermission()
                        }
                    }
                } else {
                    disableWritingReminder(alarmeeScheduler)
                }
            } catch (e: Exception) {
                _events.send(SettingsEvent.ShowError("Failed to update writing reminder settings: ${e.message}"))
            }
        }
    }

    @OptIn(ExperimentalPermissionsApi::class)
    fun handlePermissionResult(
        permissionStatus: PermissionStatus,
        alarmeeScheduler: AlarmeeScheduler
    ) {
        viewModelScope.launch {
            when {
                permissionStatus.isGranted -> {
                    showSampleNotification(alarmeeScheduler)
                    enableWritingReminder(alarmeeScheduler)
                }
                permissionStatus.shouldShowRationale -> {
                    _events.send(SettingsEvent.ShowError("Notifications permissions must be granted for reminder functionality to work"))
                    disableWritingReminder(alarmeeScheduler)
                }
                permissionStatus.isNotGranted -> {
                    disableWritingReminder(alarmeeScheduler)
                }
            }
        }
    }

    private suspend fun enableWritingReminder(alarmeeScheduler: AlarmeeScheduler) {
        val updatedSettings = state.value.generalSettings.copy(writingReminderSet = true)
        settingsManager.updateGeneralSettings(updatedSettings)
        _state.update { it.copy(generalSettings = updatedSettings) }

        alarmeeScheduler.schedule(
            alarmee = Alarmee(
                uuid = alarmeeWritingReminderUUID,
                notificationTitle = "Write a journal entry",
                notificationBody = "Let's fill up this barren land of journals with all sorts of random thoughts and stories, shall we?",
                scheduledDateTime = LocalDateTime(
                    year = currentTime.year,
                    monthNumber = currentTime.monthNumber,
                    dayOfMonth = currentTime.dayOfMonth,
                    hour = 20,
                    minute = 0
                ),
                repeatInterval = RepeatInterval.Daily,
                androidNotificationConfiguration = AndroidNotificationConfiguration(
                    priority = AndroidNotificationPriority.DEFAULT,
                    channelId = "dailyWritingReminder",
                )
            )
        )
        _events.send(SettingsEvent.SettingsUpdated("Writing reminder enabled: You will be reminded to write a journal entry every day at 8:00 PM"))
    }

    private suspend fun disableWritingReminder(alarmeeScheduler: AlarmeeScheduler) {
        val updatedSettings = state.value.generalSettings.copy(writingReminderSet = false)
        settingsManager.updateGeneralSettings(updatedSettings)
        _state.update { it.copy(generalSettings = updatedSettings) }

        alarmeeScheduler.cancel(alarmeeWritingReminderUUID)
        _events.send(SettingsEvent.SettingsUpdated("Writing reminder disabled"))
    }

    fun updateTheme(theme: ThemeMode) {
        viewModelScope.launch {
            try {
                val updatedSettings = state.value.appearanceSettings.copy(themeMode = theme)
                settingsManager.updateAppearanceSettings(updatedSettings)
                _state.update { it.copy(appearanceSettings = updatedSettings) }
                _events.send(SettingsEvent.SettingsUpdated())
            } catch (e: Exception) {
                _events.send(SettingsEvent.ShowError("Failed to update theme: ${e.message}"))
            }
        }
    }

    fun updateFontFamily(fontFamily: AvailableFontFamily) {
        viewModelScope.launch {
            try {
                val updatedSettings = state.value.appearanceSettings.copy(fontFamily = fontFamily)
                settingsManager.updateAppearanceSettings(updatedSettings)
                _state.update { it.copy(appearanceSettings = updatedSettings) }
                _events.send(SettingsEvent.SettingsUpdated())
            } catch (e: Exception) {
                _events.send(SettingsEvent.ShowError("Failed to update font family: ${e.message}"))
            }
        }
    }

    fun updateAccentColor(color: Long?) {
        viewModelScope.launch {
            try {
                val updatedSettings = state.value.appearanceSettings.copy(
                    useCustomAccentColor = color != null,
                    accentColor = color
                )
                settingsManager.updateAppearanceSettings(updatedSettings)
                _state.update { it.copy(appearanceSettings = updatedSettings) }
                _events.send(SettingsEvent.SettingsUpdated())
            } catch (e: Exception) {
                _events.send(SettingsEvent.ShowError("Failed to update accent color: ${e.message}"))
            }
        }
    }

    fun updateAppLock(enabled: Boolean) {
        viewModelScope.launch {
            try {
                val updatedSettings = state.value.securitySettings.copy(
                    useBiometricLock = enabled,
                )
                settingsManager.updateSecuritySettings(updatedSettings)
                _state.update { it.copy(securitySettings = updatedSettings) }
                _events.send(SettingsEvent.SettingsUpdated())
            } catch (e: Exception) {
                _events.send(SettingsEvent.ShowError("Failed to update app lock: ${e.message}"))
            }
        }
    }

    fun updateHideEntryPreviews(hide: Boolean) {
        viewModelScope.launch {
            try {
                val updatedSettings = state.value.securitySettings.copy(hideEntryPreviews = hide)
                settingsManager.updateSecuritySettings(updatedSettings)
                _state.update { it.copy(securitySettings = updatedSettings) }
                _events.send(SettingsEvent.SettingsUpdated())
            } catch (e: Exception) {
                _events.send(SettingsEvent.ShowError("Failed to update preview settings: ${e.message}"))
            }
        }
    }
}

data class SettingsState(
    val generalSettings: GeneralSettings = GeneralSettings(),
    val appearanceSettings: AppearanceSettings = AppearanceSettings(),
    val securitySettings: SecuritySettings = SecuritySettings(),
    val isLoading: Boolean = true
)

sealed class SettingsEvent {
    data class SettingsUpdated(val message: String = "Settings Updated") : SettingsEvent()
    data class ShowError(val message: String) : SettingsEvent()
}
