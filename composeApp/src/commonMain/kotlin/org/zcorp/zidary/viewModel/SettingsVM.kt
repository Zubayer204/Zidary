package org.zcorp.zidary.viewModel

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
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

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

    fun showSampleNotification(alarmeeScheduler: AlarmeeScheduler) {
        alarmeeScheduler.schedule(
            alarmee = Alarmee(
                uuid = "Test",
                notificationTitle = "Notification Sample",
                notificationBody = "This is how your notification will look like",
                scheduledDateTime =
                (Clock.System.now().plus(2.seconds)).toLocalDateTime(TimeZone.currentSystemDefault()),
                androidNotificationConfiguration = AndroidNotificationConfiguration(
                    priority = AndroidNotificationPriority.DEFAULT,
                    channelId = "dailyWritingReminder",
                )
            )
        )
    }

    fun updateWritingReminderSettings(enabled: Boolean, alarmeeScheduler: AlarmeeScheduler) {
        viewModelScope.launch {
            try {
                val updatedSettings = state.value.generalSettings.copy(writingReminderSet = enabled)
                settingsManager.updateGeneralSettings(updatedSettings)
                _state.update { it.copy(generalSettings = updatedSettings) }

                if (enabled) {
                    alarmeeScheduler.schedule(
                        alarmee = Alarmee(
                            uuid = alarmeeWritingReminderUUID,
                            notificationTitle = "Write a journal entry",
                            notificationBody = "Let's fill up this barren land of journals with all sorts of random thoughts and stories, shall we?",
                            scheduledDateTime = LocalDateTime(year = currentTime.year, monthNumber = currentTime.monthNumber, dayOfMonth = currentTime.dayOfMonth, hour = 20, minute = 0),
                            repeatInterval = RepeatInterval.Daily,
                            androidNotificationConfiguration = AndroidNotificationConfiguration(
                                priority = AndroidNotificationPriority.DEFAULT,
                                channelId = "dailyWritingReminder",
                            )
                        )
                    )
                    _events.send(SettingsEvent.SettingsUpdated("Writing reminder enabled: You will be reminded to write a journal entry every day at 8:00 PM"))
                } else {
                    _events.send(SettingsEvent.SettingsUpdated("Writing reminder disabled"))
                    alarmeeScheduler.cancel(alarmeeWritingReminderUUID)
                }
            } catch (e: Exception) {
                _events.send(SettingsEvent.ShowError("Failed to update writing reminder settings: ${e.message}"))
                val updatedSettings = state.value.generalSettings.copy(writingReminderSet = false)
                settingsManager.updateGeneralSettings(updatedSettings)
                _state.update { it.copy(generalSettings = updatedSettings) }
            }
        }
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
