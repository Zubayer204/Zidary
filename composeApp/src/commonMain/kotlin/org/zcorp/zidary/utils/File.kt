package org.zcorp.zidary.utils

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun generateDateTimeFileName(base: String): String {
    val date = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    return "${base}_${date.date}_${date.time}"
}