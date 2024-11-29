package org.zcorp.zidary.model.data

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOne
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.zcorp.zidary.db.JournalEntry
import org.zcorp.zidary.db.ZidaryDatabase

interface JournalFactoryInterface {
    fun getAll(): Flow<List<JournalEntry>>
    fun getById(id: Long): Flow<JournalEntry>
    suspend fun insert(title: String, body: String, entryTime: Instant)
    fun update(id: Long, title: String, body: String, entryTime: Instant): Flow<JournalEntry>
    suspend fun delete(id: Long)
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
//
//        val id = queries.selectLastInsertedRowId().executeAsOne()
//        getById(id)
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

}
