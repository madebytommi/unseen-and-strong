package com.example.unseenandstrong.ui.benefits

import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Calendar
import java.util.TimeZone

object DeadlineDateUtils {
    private val utcTimeZone: TimeZone = TimeZone.getTimeZone("UTC")

    fun toPickerUtcMillis(
        storedMillis: Long?,
        timeZone: TimeZone = TimeZone.getDefault()
    ): Long? {
        if (storedMillis == null) return null

        val localCalendar = Calendar.getInstance(timeZone).apply {
            timeInMillis = storedMillis
        }

        return Calendar.getInstance(utcTimeZone).apply {
            clear()
            set(
                localCalendar.get(Calendar.YEAR),
                localCalendar.get(Calendar.MONTH),
                localCalendar.get(Calendar.DAY_OF_MONTH),
                0,
                0,
                0
            )
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    fun fromPickerUtcMillis(
        selectedUtcMillis: Long,
        timeZone: TimeZone = TimeZone.getDefault()
    ): Long {
        val utcCalendar = Calendar.getInstance(utcTimeZone).apply {
            timeInMillis = selectedUtcMillis
        }

        return Calendar.getInstance(timeZone).apply {
            clear()
            set(
                utcCalendar.get(Calendar.YEAR),
                utcCalendar.get(Calendar.MONTH),
                utcCalendar.get(Calendar.DAY_OF_MONTH),
                12,
                0,
                0
            )
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    fun daysUntil(
        deadlineMillis: Long,
        nowMillis: Long = System.currentTimeMillis(),
        timeZone: TimeZone = TimeZone.getDefault()
    ): Long {
        val zoneId = timeZone.toZoneId()
        val today = Instant.ofEpochMilli(nowMillis).atZone(zoneId).toLocalDate()
        val deadline = Instant.ofEpochMilli(deadlineMillis).atZone(zoneId).toLocalDate()
        return ChronoUnit.DAYS.between(today, deadline)
    }
}
