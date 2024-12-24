package org.zcorp.zidary.view.components.settings

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.PanoramaFishEye
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.icons.filled.VisibilityOff
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
            subtitle = "Require biometric authentication to unlock journal",
            icon = Icons.Default.Fingerprint,
            checked = settings.useBiometricLock,
            onCheckedChange = onAppLockChanged
        )

        // Hide Entry Previews
        SettingsClickableElementWithToggle(
            name = "Hide Entry Previews",
            subtitle = "Show only titles in journal list",
            icon = Icons.Default.VisibilityOff,
            checked = settings.hideEntryPreviews,
            onCheckedChange = onHidePreviewsChanged
        )
    }
}
