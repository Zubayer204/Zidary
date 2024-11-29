package org.zcorp.zidary.model.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import org.zcorp.zidary.db.ZidaryDatabase

actual class DriverFactory {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(ZidaryDatabase.Schema, DB_NAME)
    }
}
