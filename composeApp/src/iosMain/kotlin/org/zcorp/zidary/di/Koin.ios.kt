package org.zcorp.zidary.di

import com.russhwolf.settings.Settings
import com.tweener.alarmee.configuration.AlarmeeIosPlatformConfiguration
import com.tweener.alarmee.configuration.AlarmeePlatformConfiguration
import org.zcorp.zidary.db.ZidaryDatabase
import org.koin.dsl.module

actual val platformModule = module {
    single { database }
    single { settings }

    // Alarmee
    factory<AlarmeePlatformConfiguration> { AlarmeeIosPlatformConfiguration }
}

lateinit var database: ZidaryDatabase
lateinit var settings: Settings

