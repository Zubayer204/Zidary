package org.zcorp.zidary

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.zcorp.zidary.di.commonModule
import org.zcorp.zidary.di.database
import org.zcorp.zidary.di.initKoin
import org.zcorp.zidary.di.platformModule
import org.zcorp.zidary.model.database.DriverFactory
import org.zcorp.zidary.model.database.createDatabase


class ZidaryApp : Application() {
    override fun onCreate() {
        super.onCreate()

        database = createDatabase(DriverFactory(this))
        initKoin {
            androidContext(this@ZidaryApp)
            modules(commonModule, platformModule)
        }
    }
}


