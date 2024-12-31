package org.zcorp.zidary.view.components.settings

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.runtime.Composable
import org.zcorp.zidary.model.data.GeneralSettings

@Composable
fun GeneralSection(
    settings: GeneralSettings,
    onWritingReminderSettingsChanged: (Boolean) -> Unit,
) {
    SettingsSection(title = "General") {
        // Writing reminder toggle
        SettingsClickableElementWithToggle(
            name = "Writing Reminder",
            subtitle = "Remind to write a journal entry every day",
            icon = Icons.Default.Alarm,
            checked = settings.writingReminderSet,
            onCheckedChange = onWritingReminderSettingsChanged,
        )
    }
}
