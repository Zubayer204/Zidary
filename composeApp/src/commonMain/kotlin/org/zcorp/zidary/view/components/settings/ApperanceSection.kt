package org.zcorp.zidary.view.components.settings

import androidx.compose.runtime.Composable
import org.zcorp.zidary.model.data.AppearanceSettings
import org.zcorp.zidary.model.data.AvailableFontFamily
import org.zcorp.zidary.model.data.ThemeMode

@Composable
fun AppearanceSection(
    settings: AppearanceSettings,
    onThemeChanged: (ThemeMode) -> Unit,
    onFontFamilyChanged: (AvailableFontFamily) -> Unit,
    onAccentColorChanged: (Long?) -> Unit
) {
    SettingsSection(title = "Appearance") {
        // Theme Selection
        SettingsDropdown(
            title = "Theme",
            selectedValue = settings.themeMode,
            values = ThemeMode.entries,
            onValueSelected = onThemeChanged,
            valueToString = { it.name.lowercase().replaceFirstChar { char -> char.titlecase() } }
        )

        // Font Family
        SettingsDropdown(
            title = "Font Family",
            selectedValue = settings.fontFamily,
            values = AvailableFontFamily.entries,
            onValueSelected = onFontFamilyChanged,
            valueToString = { it ->
                it.name.lowercase()
                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                    .replace("_", " ")}
        )

        // Accent Color
//        ColorPickerPreference(
//            title = "Accent Color",
//            subtitle = "Customize app accent color",
//            selectedColor = settings.accentColor,
//            onColorSelected = onAccentColorChanged,
//            enabled = settings.useCustomAccentColor
//        )
    }
}
