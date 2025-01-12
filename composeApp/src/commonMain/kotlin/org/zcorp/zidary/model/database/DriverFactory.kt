package org.zcorp.zidary.model.database

import app.cash.sqldelight.ColumnAdapter
import app.cash.sqldelight.db.SqlDriver
import kotlinx.datetime.Instant
import org.zcorp.zidary.db.JournalEntry
import org.zcorp.zidary.db.ZidaryDatabase

const val DB_NAME = "sqlight.db"

expect class DriverFactory {
    fun createDriver(): SqlDriver
}

object InstantSqlDelightAdapter : ColumnAdapter<Instant, Long> {

    override fun decode(databaseValue: Long): Instant = Instant.fromEpochMilliseconds(databaseValue)

    override fun encode(value: Instant): Long = value.toEpochMilliseconds()
}

fun createDatabase(driverFactory: DriverFactory): ZidaryDatabase {
    return ZidaryDatabase(
        driverFactory.createDriver(),
        JournalEntryAdapter = JournalEntry.Adapter(
            InstantSqlDelightAdapter,
            InstantSqlDelightAdapter,
            InstantSqlDelightAdapter
        )
    )
}