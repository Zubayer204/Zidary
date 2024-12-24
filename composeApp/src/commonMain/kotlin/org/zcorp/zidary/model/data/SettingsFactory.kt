package org.zcorp.zidary.model.data

import com.russhwolf.settings.Settings

expect class SettingsFactory {
    fun createSettings(): Settings
}