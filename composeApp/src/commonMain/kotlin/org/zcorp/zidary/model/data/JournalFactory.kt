package org.zcorp.zidary.model.data

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOne
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.atTime
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import org.zcorp.zidary.db.JournalEntry
import org.zcorp.zidary.db.ZidaryDatabase

interface JournalFactoryInterface {
    fun getAll(): Flow<List<JournalEntry>>
    fun getById(id: Long): Flow<JournalEntry>
    suspend fun insert(title: String, body: String, entryTime: Instant)
    fun update(id: Long, title: String, body: String, entryTime: Instant): Flow<JournalEntry>
    suspend fun delete(id: Long)
    fun getEntriesByMonth(year: Int, month: Month): Flow<List<JournalEntry>>
    fun getEntriesByDate(date: LocalDate): Flow<List<JournalEntry>>
    fun getEntryDatesForMonth(year: Int, month: Month): Flow<List<LocalDate>>

    /**
     * @return the number of rows present in the JournalEntry table. 0L if there are no entries
     *
     * @throws IllegalStateException if when executed this query has multiple rows in its result set.
     */
    fun getTotalEntries(): Long
}

class JournalFactory(database: ZidaryDatabase): JournalFactoryInterface {
    private val queries = database.journalEntryQueries


    override fun getAll(): Flow<List<JournalEntry>> {
        return queries.selectAllJournalEntries().asFlow().mapToList(Dispatchers.IO)
    }

    override fun getById(id: Long): Flow<JournalEntry> {
        return queries.selectJournalEntryById(id).asFlow().mapToOne(Dispatchers.IO)
    }

    override suspend fun insert(title: String, body: String, entryTime: Instant) {
        withContext(Dispatchers.IO) {
            val now = Clock.System.now()
            queries.insertJournalEntry(
                title,
                body,
                entryTime,
                now,
                now
            )
        }
    }

    override fun update(id: Long, title: String, body: String, entryTime: Instant): Flow<JournalEntry> = run {
        val now = Clock.System.now()
        queries.updateJournalEntry(
            title,
            body,
            entryTime,
            now,
            id
        )
        getById(id)
    }

    override suspend fun delete(id: Long) {
        withContext(Dispatchers.IO) {
            queries.deleteJournalEntryById(id)
        }
    }

    override fun getEntriesByMonth(year: Int, month: Month): Flow<List<JournalEntry>> {
        val timeZone = TimeZone.currentSystemDefault()
        val startOfMonth = LocalDate(year, month, 1)
        val endOfMonth = startOfMonth.plus(1, DateTimeUnit.MONTH).minus(1, DateTimeUnit.DAY)
        return queries.selectEntriesByTimeRange(
            startOfTime = startOfMonth.atStartOfDayIn(timeZone),
            endOfTime = endOfMonth.atTime(23, 59, 59, 99999).toInstant(timeZone)
        ).asFlow().mapToList(Dispatchers.IO)
    }

    override fun getEntriesByDate(date: LocalDate): Flow<List<JournalEntry>> {
        val timeZone = TimeZone.currentSystemDefault()

        return queries.selectEntriesByTimeRange(
            startOfTime = date
                .atStartOfDayIn(timeZone),
            endOfTime = date
                .atTime(23, 59, 59, 999)
                .toInstant(timeZone)
        ).asFlow().mapToList(Dispatchers.IO)
    }

    override fun getEntryDatesForMonth(year: Int, month: Month): Flow<List<LocalDate>> {
        val timeZone = TimeZone.currentSystemDefault()
        val startOfMonth = LocalDate(year, month, 1)
        val endOfMonth = startOfMonth.plus(1, DateTimeUnit.MONTH).minus(1, DateTimeUnit.DAY)
        return queries.selectEntryDatesForTimeRange(
            startOfTime = startOfMonth
                .atStartOfDayIn(TimeZone.currentSystemDefault()),
            endOfTime = endOfMonth.atTime(23, 59, 59, 99999).toInstant(timeZone)
        ).asFlow().mapToList(Dispatchers.IO)
            .map { timestamps ->
                timestamps.map { timestamp ->
                    timestamp.toLocalDateTime(TimeZone.currentSystemDefault()).date
                }
            }
    }

    override fun getTotalEntries(): Long {
        return queries.selectTotalEntries().executeAsOneOrNull() ?: 0L
    }
}
