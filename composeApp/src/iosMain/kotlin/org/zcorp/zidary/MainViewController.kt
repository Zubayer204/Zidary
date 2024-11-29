package org.zcorp.zidary

import androidx.compose.ui.window.ComposeUIViewController
import org.zcorp.zidary.model.database.DriverFactory
import org.zcorp.zidary.model.database.createDatabase

fun MainViewController() = ComposeUIViewController {
    val db = createDatabase(DriverFactory())
    App(db)
}