package org.zcorp.zidary.view.components.settings

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.runtime.Composable
import org.zcorp.zidary.model.data.SecuritySettings

@Composable
fun SecuritySection(
    settings: SecuritySettings,
    onAppLockChanged: (Boolean) -> Unit,
    onHidePreviewsChanged: (Boolean) -> Unit
) {
    SettingsSection(title = "Privacy & Security") {
        // App Lock Toggle
        SettingsClickableElementWithToggle(
            name = "Biometric Lock",
            icon = Icons.Default.Fingerprint,
            checked = settings.useBiometricLock,
            onCheckedChange = onAppLockChanged
        )

        // Hide Entry Previews
        SwitchPreference(
            title = "Hide Entry Previews",
            subtitle = "Show only titles in journal list",
            checked = settings.hideEntryPreviews,
            onCheckedChange = onHidePreviewsChanged
        )
    }
}
