package org.zcorp.zidary.model.data

import kotlinx.serialization.Serializable

@Serializable
data class AppearanceSettings(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val fontFamily: AvailableFontFamily = AvailableFontFamily.EPILOGUE,
    val useCustomAccentColor: Boolean = false,
    val accentColor: Long? = null
)

@Serializable
data class SecuritySettings(
    val useBiometricLock: Boolean = false,
    val hideEntryPreviews: Boolean = false,
)

enum class ThemeMode {
    LIGHT, DARK, SYSTEM
}

enum class AvailableFontFamily {
    EPILOGUE,
    FREDRICKA_THE_GREAT,
    FUNNEL_DISPLAY,
    GEMUNU_LIBRE,
    SPACE_MONO,
}
