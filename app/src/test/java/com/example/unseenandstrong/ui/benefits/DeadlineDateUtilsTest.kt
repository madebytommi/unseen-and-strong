package com.example.unseenandstrong.ui.benefits

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.Calendar
import java.util.TimeZone

class DeadlineDateUtilsTest {
    private val chicago = TimeZone.getTimeZone("America/Chicago")

    @Test
    fun pickerSelectionRoundTripsWithoutChangingCalendarDay() {
        val pickerMillis = LocalDate.of(2026, 11, 1)
            .atStartOfDay()
            .toInstant(ZoneOffset.UTC)
            .toEpochMilli()

        val storedMillis = DeadlineDateUtils.fromPickerUtcMillis(pickerMillis, chicago)
        val restoredPickerMillis = DeadlineDateUtils.toPickerUtcMillis(storedMillis, chicago)

        assertEquals(pickerMillis, restoredPickerMillis)
    }

    @Test
    fun existingDeadlineInitializesPickerFromLocalCalendarDay() {
        val storedMillis = Calendar.getInstance(chicago).apply {
            clear()
            set(2026, Calendar.MARCH, 8, 12, 0, 0)
        }.timeInMillis
        val expectedPickerMillis = LocalDate.of(2026, 3, 8)
            .atStartOfDay()
            .toInstant(ZoneOffset.UTC)
            .toEpochMilli()

        assertEquals(
            expectedPickerMillis,
            DeadlineDateUtils.toPickerUtcMillis(storedMillis, chicago)
        )
    }

    @Test
    fun missingDeadlineDoesNotInitializePickerSelection() {
        assertNull(DeadlineDateUtils.toPickerUtcMillis(null, chicago))
    }

    @Test
    fun approachingDeadlineUsesCalendarDaysAcrossDstChanges() {
        val now = Instant.parse("2026-03-07T18:00:00Z").toEpochMilli()
        val pickerMillis = LocalDate.of(2026, 3, 14)
            .atStartOfDay()
            .toInstant(ZoneOffset.UTC)
            .toEpochMilli()
        val deadline = DeadlineDateUtils.fromPickerUtcMillis(pickerMillis, chicago)

        assertEquals(7, DeadlineDateUtils.daysUntil(deadline, now, chicago))
    }
}
