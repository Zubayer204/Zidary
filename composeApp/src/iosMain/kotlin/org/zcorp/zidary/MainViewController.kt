package org.zcorp.zidary

import androidx.compose.ui.window.ComposeUIViewController
import org.zcorp.zidary.di.commonModule
import org.zcorp.zidary.di.database
import org.zcorp.zidary.di.initKoin
import org.zcorp.zidary.di.platformModule
import org.zcorp.zidary.model.database.DriverFactory
import org.zcorp.zidary.model.database.createDatabase

fun MainViewController() = ComposeUIViewController {
    database = createDatabase(DriverFactory())
    initKoin {
        modules(commonModule, platformModule)
    }

    App()
}