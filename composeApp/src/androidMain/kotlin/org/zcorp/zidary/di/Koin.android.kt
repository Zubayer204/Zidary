package org.zcorp.zidary.di

import com.russhwolf.settings.Settings
import org.koin.dsl.module
import org.zcorp.zidary.db.ZidaryDatabase

actual val platformModule = module {
    single { database }
    single { settings }
}

lateinit var database: ZidaryDatabase
lateinit var settings: Settings