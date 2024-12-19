package org.zcorp.zidary.model.data

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class ExportedEntry(
    val id: Long,
    val title: String,
    val body: String,
    val entryTime: Instant,
    val createdAt: Instant,
    val modifiedAt: Instant,
)

@Serializable
data class ExportData(
    val entries: List<ExportedEntry>,
    val exportTime: Instant = Clock.System.now()
)