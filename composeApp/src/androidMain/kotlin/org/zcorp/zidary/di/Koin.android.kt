package org.zcorp.zidary.di

import com.russhwolf.settings.Settings
import org.koin.dsl.module
import org.zcorp.zidary.db.ZidaryDatabase
import org.zcorp.zidary.model.data.SettingsRepository

actual val platformModule = module {
    single { database }
    single { settings }
}

lateinit var database: ZidaryDatabase
lateinit var settings: Settings