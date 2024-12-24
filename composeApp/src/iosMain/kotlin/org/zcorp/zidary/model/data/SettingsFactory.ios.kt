package org.zcorp.zidary.model.data

import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.Settings
import platform.Foundation.NSUserDefaults

actual class SettingsFactory {
    actual fun createSettings(): Settings {
        return NSUserDefaultsSettings(NSUserDefaults.standardUserDefaults)
    }
}