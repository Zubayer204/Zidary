package org.zcorp.zidary.model.data

import android.content.Context
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings

actual class SettingsFactory(private val context: Context) {
    actual fun createSettings(): Settings {
        val sharedPreferences = context.getSharedPreferences(
            "zidary_settings",
            Context.MODE_PRIVATE
        )

        return SharedPreferencesSettings(sharedPreferences)
    }
}