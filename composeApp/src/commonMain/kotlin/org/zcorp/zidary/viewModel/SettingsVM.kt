package org.zcorp.zidary.viewModel

import com.russhwolf.settings.Settings
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.zcorp.zidary.model.data.AppearanceSettings
import org.zcorp.zidary.model.data.AvailableFontFamily
import org.zcorp.zidary.model.data.LockType
import org.zcorp.zidary.model.data.SecuritySettings
import org.zcorp.zidary.model.data.ThemeMode

class SettingsVM(
    private val settingsManager: SettingsManager
): ViewModel() {
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
                    appearanceSettings = settingsManager.appearanceSettings.value,
                    securitySettings = settingsManager.securitySettings.value,
                    isLoading = false
                )
            }
        } catch (e: Exception) {
            _events.send(SettingsEvent.ShowError("Failed to load settings: ${e.message}"))
        }
    }

    fun updateTheme(theme: ThemeMode) {
        viewModelScope.launch {
            try {
                val updatedSettings = state.value.appearanceSettings.copy(themeMode = theme)
                settingsManager.updateAppearanceSettings(updatedSettings)
                _state.update { it.copy(appearanceSettings = updatedSettings) }
                _events.send(SettingsEvent.SettingsUpdated)
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
                _events.send(SettingsEvent.SettingsUpdated)
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
                _events.send(SettingsEvent.SettingsUpdated)
            } catch (e: Exception) {
                _events.send(SettingsEvent.ShowError("Failed to update accent color: ${e.message}"))
            }
        }
    }

    fun updateAppLock(enabled: Boolean) {
        viewModelScope.launch {
            try {
                val updatedSettings = state.value.securitySettings.copy(
                    useAppLock = enabled,
                    lockType = if (enabled) LockType.PIN else LockType.NONE
                )
                settingsManager.updateSecuritySettings(updatedSettings)
                _state.update { it.copy(securitySettings = updatedSettings) }
                _events.send(SettingsEvent.SettingsUpdated)
            } catch (e: Exception) {
                _events.send(SettingsEvent.ShowError("Failed to update app lock: ${e.message}"))
            }
        }
    }

    fun updateLockType(lockType: LockType) {
        viewModelScope.launch {
            try {
                val updatedSettings = state.value.securitySettings.copy(lockType = lockType)
                settingsManager.updateSecuritySettings(updatedSettings)
                _state.update { it.copy(securitySettings = updatedSettings) }
                _events.send(SettingsEvent.SettingsUpdated)
            } catch (e: Exception) {
                _events.send(SettingsEvent.ShowError("Failed to update lock type: ${e.message}"))
            }
        }
    }

    fun updateHideEntryPreviews(hide: Boolean) {
        viewModelScope.launch {
            try {
                val updatedSettings = state.value.securitySettings.copy(hideEntryPreviews = hide)
                settingsManager.updateSecuritySettings(updatedSettings)
                _state.update { it.copy(securitySettings = updatedSettings) }
                _events.send(SettingsEvent.SettingsUpdated)
            } catch (e: Exception) {
                _events.send(SettingsEvent.ShowError("Failed to update preview settings: ${e.message}"))
            }
        }
    }
}

data class SettingsState(
    val appearanceSettings: AppearanceSettings = AppearanceSettings(),
    val securitySettings: SecuritySettings = SecuritySettings(),
    val isLoading: Boolean = true
)

sealed class SettingsEvent {
    data object SettingsUpdated : SettingsEvent()
    data class ShowError(val message: String) : SettingsEvent()
}
