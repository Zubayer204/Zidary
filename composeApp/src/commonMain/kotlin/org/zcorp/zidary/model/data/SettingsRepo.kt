package org.zcorp.zidary.model.data

import com.russhwolf.settings.Settings
import kotlinx.serialization.json.Json

class SettingsRepository(
    private val settings: Settings,
    private val json: Json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }
) {
    companion object {
        private const val GENERAL_SETTINGS_KEY = "general_settings"
        private const val APPEARANCE_SETTINGS_KEY = "appearance_settings"
        private const val SECURITY_SETTINGS_KEY = "security_settings"
    }

    fun getGeneralSettings(): GeneralSettings {
        return settings.getStringOrNull(GENERAL_SETTINGS_KEY)?.let {
            json.decodeFromString(GeneralSettings.serializer(), it)
        } ?: GeneralSettings()
    }

    fun updateGeneralSettings(settings: GeneralSettings) {
        val serialized = json.encodeToString(GeneralSettings.serializer(), settings)
        this.settings.putString(GENERAL_SETTINGS_KEY, serialized)
    }

    fun getAppearanceSettings(): AppearanceSettings {
        return settings.getStringOrNull(APPEARANCE_SETTINGS_KEY)?.let {
            json.decodeFromString(AppearanceSettings.serializer(), it)
        } ?: AppearanceSettings()
    }

    fun updateAppearanceSettings(settings: AppearanceSettings) {
        val serialized = json.encodeToString(AppearanceSettings.serializer(), settings)
        this.settings.putString(APPEARANCE_SETTINGS_KEY, serialized)
    }

    fun getSecuritySettings(): SecuritySettings {
        return settings.getStringOrNull(SECURITY_SETTINGS_KEY)?.let {
            json.decodeFromString(SecuritySettings.serializer(), it)
        } ?: SecuritySettings()
    }

    fun updateSecuritySettings(settings: SecuritySettings) {
        val serialized = json.encodeToString(SecuritySettings.serializer(), settings)
        this.settings.putString(SECURITY_SETTINGS_KEY, serialized)
    }
}
