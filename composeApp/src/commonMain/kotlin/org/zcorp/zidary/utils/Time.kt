package org.zcorp.zidary.utils

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.until

fun formatDateTime(dateTime: LocalDateTime): String {
    val month = dateTime.month.name.take(3).lowercase().replaceFirstChar { it.uppercase() }
    val day = dateTime.dayOfMonth.toString().padStart(2, '0')
    val hour = dateTime.hour.toString().padStart(2, '0')
    val minute = dateTime.minute.toString().padStart(2, '0')
    return "$day $month, ${dateTime.year}  $hour:$minute"
}

fun getTotalDaysInMonth(date: LocalDate): Int {
    val start = LocalDate(date.year, date.monthNumber, 1)
    val end = start.plus(1, DateTimeUnit.MONTH)
    return start.until(end, DateTimeUnit.DAY)
}

fun epochMillisecondsToLocalDate(epochMillis: Long, timeZone: TimeZone): LocalDate {
    val instant = Instant.fromEpochMilliseconds(epochMillis)
    return instant.toLocalDateTime(timeZone).date
}