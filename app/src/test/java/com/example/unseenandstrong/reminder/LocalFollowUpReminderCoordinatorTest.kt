package com.example.unseenandstrong.reminder

import com.example.unseenandstrong.data.local.advocacy.AdvocacySessionEntity
import com.example.unseenandstrong.data.local.benefits.BenefitsStageEntity
import com.example.unseenandstrong.data.local.claims.DisabilityClaimEntity
import com.example.unseenandstrong.data.local.interaction.InteractionEntity
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LocalFollowUpReminderCoordinatorTest {
    private val zoneId = ZoneId.of("America/Chicago")
    private val now = ZonedDateTime.of(
        LocalDate.of(2026, 8, 14),
        LocalTime.of(8, 0),
        zoneId
    ).toInstant()

    @Test
    fun futureAdvocacyAndInteractionFollowUpsSchedule() {
        val fixture = fixture()
        val futureDate = storedDate(LocalDate.of(2026, 8, 15), zoneId)

        fixture.coordinator.syncAdvocacyFollowUp(
            advocacySession(id = 7, needsFollowUp = true, followUpDate = futureDate)
        )
        fixture.coordinator.syncInteractionFollowUp(
            interaction(id = 8, needsFollowUp = true, followUpDate = futureDate)
        )

        assertTrue(
            fixture.workStore.scheduled.containsKey(
                ReminderKey(ReminderKind.ADVOCACY_FOLLOW_UP, 7).uniqueWorkName
            )
        )
        assertTrue(
            fixture.workStore.scheduled.containsKey(
                ReminderKey(ReminderKind.INTERACTION_FOLLOW_UP, 8).uniqueWorkName
            )
        )
    }

    @Test
    fun pastDateDoesNotSchedule() {
        val fixture = fixture()
        val key = ReminderKey(ReminderKind.ADVOCACY_FOLLOW_UP, 7)

        fixture.coordinator.syncAdvocacyFollowUp(
            advocacySession(
                id = 7,
                needsFollowUp = true,
                followUpDate = storedDate(LocalDate.of(2026, 8, 13), zoneId)
            )
        )

        assertTrue(fixture.workStore.scheduled.isEmpty())
        assertTrue(key.uniqueWorkName in fixture.workStore.cancelledNames)
    }

    @Test
    fun todayAfterReminderTimeIsSkippedWithoutImmediateNotification() {
        val afterReminderClock = Clock.fixed(
            ZonedDateTime.of(
                LocalDate.of(2026, 8, 14),
                LocalTime.of(11, 0),
                zoneId
            ).toInstant(),
            zoneId
        )
        val fixture = fixture(clock = afterReminderClock)

        fixture.coordinator.syncInteractionFollowUp(
            interaction(
                id = 3,
                needsFollowUp = true,
                followUpDate = storedDate(LocalDate.of(2026, 8, 14), zoneId)
            )
        )

        assertTrue(fixture.workStore.scheduled.isEmpty())
    }

    @Test
    fun dateChangeReplacesOneStableWorkName() {
        val fixture = fixture()
        val firstDate = storedDate(LocalDate.of(2026, 8, 15), zoneId)
        val secondDate = storedDate(LocalDate.of(2026, 8, 16), zoneId)

        fixture.coordinator.syncInteractionFollowUp(
            interaction(id = 4, needsFollowUp = true, followUpDate = firstDate)
        )
        val firstReminder = fixture.workStore.scheduled.values.single()
        fixture.coordinator.syncInteractionFollowUp(
            interaction(id = 4, needsFollowUp = true, followUpDate = secondDate)
        )
        val secondReminder = fixture.workStore.scheduled.values.single()

        assertEquals(1, fixture.workStore.scheduled.size)
        assertEquals(firstReminder.key.uniqueWorkName, secondReminder.key.uniqueWorkName)
        assertNotEquals(firstReminder.triggerAt, secondReminder.triggerAt)
        assertEquals(2, fixture.workStore.replaceCount)
    }

    @Test
    fun clearingFollowUpCancelsScheduledWork() {
        val fixture = fixture()
        val key = ReminderKey(ReminderKind.INTERACTION_FOLLOW_UP, 5)
        fixture.coordinator.syncInteractionFollowUp(
            interaction(
                id = 5,
                needsFollowUp = true,
                followUpDate = storedDate(LocalDate.of(2026, 8, 15), zoneId)
            )
        )

        fixture.coordinator.syncInteractionFollowUp(
            interaction(id = 5, needsFollowUp = false, followUpDate = null)
        )

        assertTrue(fixture.workStore.scheduled.isEmpty())
        assertTrue(key.uniqueWorkName in fixture.workStore.cancelledNames)
    }

    @Test
    fun disablingGlobalRemindersCancelsAllWork() {
        val fixture = fixture()
        fixture.coordinator.syncBenefitsDeadline(
            BenefitsStageEntity(
                id = 2,
                stageOrder = 2,
                stageName = "Application",
                deadlineDate = storedDate(LocalDate.of(2026, 8, 15), zoneId)
            )
        )
        assertFalse(fixture.workStore.scheduled.isEmpty())

        fixture.coordinator.setRemindersEnabled(false)

        assertFalse(fixture.preferences.remindersEnabled)
        assertTrue(fixture.workStore.scheduled.isEmpty())
        assertEquals(1, fixture.workStore.cancelAllCount)
    }

    @Test
    fun completedBenefitsStageDoesNotKeepDeadlineReminder() {
        val fixture = fixture()
        val date = storedDate(LocalDate.of(2026, 8, 15), zoneId)
        fixture.coordinator.syncBenefitsDeadline(
            BenefitsStageEntity(
                id = 2,
                stageOrder = 2,
                stageName = "Application",
                deadlineDate = date
            )
        )

        fixture.coordinator.syncBenefitsDeadline(
            BenefitsStageEntity(
                id = 2,
                stageOrder = 2,
                stageName = "Application",
                status = "Completed",
                deadlineDate = date
            )
        )

        assertTrue(fixture.workStore.scheduled.isEmpty())
    }

    @Test
    fun claimNextActionAndAppealDeadlineHaveDistinctKeys() {
        val fixture = fixture()
        val date = storedDate(LocalDate.of(2026, 8, 15), zoneId)

        fixture.coordinator.syncClaimReminders(
            DisabilityClaimEntity(
                id = 12,
                claimType = "STD",
                nextActionDueDate = date,
                appealDeadline = date
            )
        )

        assertEquals(2, fixture.workStore.scheduled.size)
        assertTrue(
            fixture.workStore.scheduled.containsKey(
                ReminderKey(ReminderKind.CLAIM_NEXT_ACTION, 12).uniqueWorkName
            )
        )
        assertTrue(
            fixture.workStore.scheduled.containsKey(
                ReminderKey(ReminderKind.CLAIM_APPEAL_DEADLINE, 12).uniqueWorkName
            )
        )
    }

    @Test
    fun uniqueWorkNamesAreStableAndSourceSpecific() {
        val first = ReminderKey(ReminderKind.ADVOCACY_FOLLOW_UP, 44)
        val same = ReminderKey(ReminderKind.ADVOCACY_FOLLOW_UP, 44)
        val differentSource = ReminderKey(ReminderKind.INTERACTION_FOLLOW_UP, 44)

        assertEquals(first.uniqueWorkName, same.uniqueWorkName)
        assertEquals(first.notificationId, same.notificationId)
        assertNotEquals(first.uniqueWorkName, differentSource.uniqueWorkName)
    }

    @Test
    fun dateOnlyReminderUsesNineAmInProvidedTimeZone() {
        val auckland = ZoneId.of("Pacific/Auckland")
        val localNow = ZonedDateTime.of(
            LocalDate.of(2026, 9, 26),
            LocalTime.of(8, 0),
            auckland
        )
        val reminderDate = LocalDate.of(2026, 9, 27)
        val trigger = FollowUpReminderTiming.triggerInstant(
            storedDateMillis = storedDate(reminderDate, auckland),
            clock = Clock.fixed(localNow.toInstant(), auckland),
            zoneId = auckland
        )

        val localTrigger = trigger?.atZone(auckland)
        assertEquals(reminderDate, localTrigger?.toLocalDate())
        assertEquals(LocalTime.of(9, 0), localTrigger?.toLocalTime())
    }

    private fun fixture(
        clock: Clock = Clock.fixed(now, zoneId)
    ): Fixture {
        val preferences = FakeReminderPreferenceStore(remindersEnabled = true)
        val workStore = FakeReminderWorkStore()
        return Fixture(
            coordinator = LocalFollowUpReminderCoordinator(
                preferences = preferences,
                workStore = workStore,
                clock = clock,
                zoneId = zoneId
            ),
            preferences = preferences,
            workStore = workStore
        )
    }

    private fun advocacySession(
        id: Long,
        needsFollowUp: Boolean,
        followUpDate: Long?
    ) = AdvocacySessionEntity(
        id = id,
        scriptId = null,
        scriptTitle = "Script",
        scriptCategory = "Work",
        selectedTone = "GENTLE",
        scriptTextSnapshot = "",
        createdAt = 1,
        updatedAt = 1,
        needsFollowUp = needsFollowUp,
        followUpDate = followUpDate
    )

    private fun interaction(
        id: Long,
        needsFollowUp: Boolean,
        followUpDate: Long?
    ) = InteractionEntity(
        id = id,
        timestamp = 1,
        needsFollowUp = needsFollowUp,
        followUpDate = followUpDate,
        category = "Work",
        personName = "Person",
        organization = "",
        notes = ""
    )

    private fun storedDate(date: LocalDate, zoneId: ZoneId): Long =
        date.atTime(12, 0).atZone(zoneId).toInstant().toEpochMilli()

    private data class Fixture(
        val coordinator: LocalFollowUpReminderCoordinator,
        val preferences: FakeReminderPreferenceStore,
        val workStore: FakeReminderWorkStore
    )
}

private class FakeReminderPreferenceStore(
    override var remindersEnabled: Boolean
) : ReminderPreferenceStore

private class FakeReminderWorkStore : ReminderWorkStore {
    val scheduled = mutableMapOf<String, ScheduledReminder>()
    val cancelledNames = mutableListOf<String>()
    var replaceCount: Int = 0
    var cancelAllCount: Int = 0

    override fun replace(reminder: ScheduledReminder) {
        replaceCount += 1
        scheduled[reminder.key.uniqueWorkName] = reminder
    }

    override fun cancel(key: ReminderKey) {
        cancelledNames += key.uniqueWorkName
        scheduled.remove(key.uniqueWorkName)
    }

    override fun cancelAll() {
        cancelAllCount += 1
        scheduled.clear()
    }
}
