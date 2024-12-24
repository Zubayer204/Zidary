package org.zcorp.zidary.view.components.settings

import androidx.compose.runtime.Composable
import org.zcorp.zidary.model.data.LockType
import org.zcorp.zidary.model.data.SecuritySettings

@Composable
fun SecuritySection(
    settings: SecuritySettings,
    onAppLockChanged: (Boolean) -> Unit,
    onLockTypeChanged: (LockType) -> Unit,
    onHidePreviewsChanged: (Boolean) -> Unit
) {
    SettingsSection(title = "Privacy & Security") {
        // App Lock Toggle
        SwitchPreference(
            title = "App Lock",
            subtitle = "Require authentication to open app",
            checked = settings.useAppLock,
            onCheckedChange = onAppLockChanged
        )

        // Lock Type Selection (only shown if app lock is enabled)
        if (settings.useAppLock) {
            SettingsDropdown(
                title = "Lock Type",
                selectedValue = settings.lockType,
                values = LockType.entries.filter { it != LockType.NONE },
                onValueSelected = onLockTypeChanged,
                valueToString = { it.name.lowercase().capitalize() }
            )
        }

        // Hide Entry Previews
        SwitchPreference(
            title = "Hide Entry Previews",
            subtitle = "Show only titles in journal list",
            checked = settings.hideEntryPreviews,
            onCheckedChange = onHidePreviewsChanged
        )
    }
}
