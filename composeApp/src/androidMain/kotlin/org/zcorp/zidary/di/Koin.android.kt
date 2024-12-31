package org.zcorp.zidary.di

import android.app.NotificationManager
import com.russhwolf.settings.Settings
import com.tweener.alarmee.channel.AlarmeeNotificationChannel
import com.tweener.alarmee.configuration.AlarmeeAndroidPlatformConfiguration
import com.tweener.alarmee.configuration.AlarmeePlatformConfiguration
import org.koin.dsl.module
import org.zcorp.zidary.db.ZidaryDatabase
import org.zcorp.zidary.R

actual val platformModule = module {
    single { database }
    single { settings }

    // Alarmee
    factory<AlarmeePlatformConfiguration> { AlarmeeAndroidPlatformConfiguration(
        R.mipmap.ic_launcher,
        notificationChannels = listOf(
            AlarmeeNotificationChannel(
                id = "dailyWritingReminder",
                name = "Daily Writing Reminder",
                importance = NotificationManager.IMPORTANCE_HIGH,
            )
        )
    ) }
}

lateinit var database: ZidaryDatabase
lateinit var settings: Settings