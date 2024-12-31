package org.zcorp.zidary.viewModel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.zcorp.zidary.model.data.AppearanceSettings
import org.zcorp.zidary.model.data.AvailableFontFamily
import org.zcorp.zidary.model.data.GeneralSettings
import org.zcorp.zidary.model.data.SecuritySettings
import org.zcorp.zidary.model.data.SettingsRepository
import org.zcorp.zidary.model.data.ThemeMode

class SettingsManager(
    private val settingsRepository: SettingsRepository
) {
    private val _generalSettings = MutableStateFlow(GeneralSettings())
    val generalSettings: StateFlow<GeneralSettings> = _generalSettings.asStateFlow()

    private val _appearanceSettings = MutableStateFlow(AppearanceSettings())
    val appearanceSettings: StateFlow<AppearanceSettings> = _appearanceSettings.asStateFlow()

    private val _securitySettings = MutableStateFlow(SecuritySettings())
    val securitySettings: StateFlow<SecuritySettings> = _securitySettings.asStateFlow()

    init {
        _generalSettings.update { settingsRepository.getGeneralSettings() }
        _appearanceSettings.update { settingsRepository.getAppearanceSettings() }
        _securitySettings.update { settingsRepository.getSecuritySettings() }
    }

    fun updateGeneralSettings(settings: GeneralSettings) {
        settingsRepository.updateGeneralSettings(settings)
        _generalSettings.update { settings }
    }

    fun updateAppearanceSettings(settings: AppearanceSettings) {
        settingsRepository.updateAppearanceSettings(settings)
        _appearanceSettings.update { settings }
    }

    fun updateSecuritySettings(settings: SecuritySettings) {
        settingsRepository.updateSecuritySettings(settings)
        _securitySettings.update { settings }
    }

    fun getCurrentTheme(): ThemeMode = appearanceSettings.value.themeMode
    fun getCurrentFontFamily(): AvailableFontFamily = appearanceSettings.value.fontFamily
    fun getAccentColor(): Long? = appearanceSettings.value.accentColor

    fun isBiometricLockEnabled(): Boolean = securitySettings.value.useBiometricLock
    fun shouldHideEntryPreviews(): Boolean = securitySettings.value.hideEntryPreviews
}
