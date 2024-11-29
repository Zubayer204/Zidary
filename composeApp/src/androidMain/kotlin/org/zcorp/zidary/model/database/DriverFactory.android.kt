package org.zcorp.zidary.model.database

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import org.zcorp.zidary.db.ZidaryDatabase

actual class DriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(ZidaryDatabase.Schema, context, DB_NAME)
    }
}