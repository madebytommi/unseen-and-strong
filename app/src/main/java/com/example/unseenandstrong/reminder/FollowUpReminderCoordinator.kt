package com.example.unseenandstrong.reminder

import android.content.Context
import androidx.annotation.StringRes
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.example.unseenandstrong.R
import com.example.unseenandstrong.data.local.UnseenDatabase
import com.example.unseenandstrong.data.local.advocacy.AdvocacySessionEntity
import com.example.unseenandstrong.data.local.benefits.BenefitsStageEntity
import com.example.unseenandstrong.data.local.claims.DisabilityClaimEntity
import com.example.unseenandstrong.data.local.interaction.InteractionEntity
import java.time.Clock
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import java.util.concurrent.TimeUnit

interface FollowUpReminderCoordinator {
    fun setRemindersEnabled(enabled: Boolean)
    fun syncAdvocacyFollowUp(session: AdvocacySessionEntity)
    fun syncInteractionFollowUp(interaction: InteractionEntity)
    fun cancelInteractionFollowUp(interactionId: Long)
    fun syncBenefitsDeadline(stage: BenefitsStageEntity)
    fun syncClaimReminders(claim: DisabilityClaimEntity)
    fun cancelClaimReminders(claimId: Long)
    suspend fun reconcileAll(database: UnseenDatabase)
}

object NoOpFollowUpReminderCoordinator : FollowUpReminderCoordinator {
    override fun setRemindersEnabled(enabled: Boolean) = Unit
    override fun syncAdvocacyFollowUp(session: AdvocacySessionEntity) = Unit
    override fun syncInteractionFollowUp(interaction: InteractionEntity) = Unit
    override fun cancelInteractionFollowUp(interactionId: Long) = Unit
    override fun syncBenefitsDeadline(stage: BenefitsStageEntity) = Unit
    override fun syncClaimReminders(claim: DisabilityClaimEntity) = Unit
    override fun cancelClaimReminders(claimId: Long) = Unit
    override suspend fun reconcileAll(database: UnseenDatabase) = Unit
}

internal enum class ReminderKind(
    val storageValue: String,
    @param:StringRes val messageResId: Int
) {
    ADVOCACY_FOLLOW_UP(
        storageValue = "advocacy_follow_up",
        messageResId = R.string.follow_up_notification_advocacy
    ),
    INTERACTION_FOLLOW_UP(
        storageValue = "interaction_follow_up",
        messageResId = R.string.follow_up_notification_interaction
    ),
    BENEFITS_DEADLINE(
        storageValue = "benefits_deadline",
        messageResId = R.string.follow_up_notification_benefits
    ),
    CLAIM_NEXT_ACTION(
        storageValue = "claim_next_action",
        messageResId = R.string.follow_up_notification_claim_next_action
    ),
    CLAIM_APPEAL_DEADLINE(
        storageValue = "claim_appeal_deadline",
        messageResId = R.string.follow_up_notification_claim_appeal
    )
}

internal data class ReminderKey(
    val kind: ReminderKind,
    val recordId: Long
) {
    val uniqueWorkName: String = "follow_up_reminder:${kind.storageValue}:$recordId"
    val notificationId: Int = uniqueWorkName.hashCode().let { hash ->
        if (hash == 0) 1 else hash
    }
}

internal data class ScheduledReminder(
    val key: ReminderKey,
    val triggerAt: Instant
)

internal interface ReminderWorkStore {
    fun replace(reminder: ScheduledReminder)
    fun cancel(key: ReminderKey)
    fun cancelAll()
}

internal class WorkManagerReminderWorkStore(
    context: Context
) : ReminderWorkStore {
    private val workManager = WorkManager.getInstance(context.applicationContext)

    override fun replace(reminder: ScheduledReminder) {
        val delayMillis = (reminder.triggerAt.toEpochMilli() - System.currentTimeMillis())
            .coerceAtLeast(0L)
        val inputData = Data.Builder()
            .putString(
                FollowUpReminderWorker.INPUT_REMINDER_KIND,
                reminder.key.kind.storageValue
            )
            .putInt(
                FollowUpReminderWorker.INPUT_NOTIFICATION_ID,
                reminder.key.notificationId
            )
            .build()
        val request = OneTimeWorkRequest.Builder(FollowUpReminderWorker::class.java)
            .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
            .setInputData(inputData)
            .addTag(WORK_TAG)
            .build()

        workManager.enqueueUniqueWork(
            reminder.key.uniqueWorkName,
            ExistingWorkPolicy.REPLACE,
            request
        )
    }

    override fun cancel(key: ReminderKey) {
        workManager.cancelUniqueWork(key.uniqueWorkName)
    }

    override fun cancelAll() {
        workManager.cancelAllWorkByTag(WORK_TAG)
    }

    private companion object {
        const val WORK_TAG = "advocacy_follow_up_reminders"
    }
}

internal object FollowUpReminderTiming {
    val reminderTime: LocalTime = LocalTime.of(9, 0)

    fun triggerInstant(
        storedDateMillis: Long,
        clock: Clock,
        zoneId: ZoneId
    ): Instant? {
        val now = clock.instant()
        val today = now.atZone(zoneId).toLocalDate()
        val reminderDate = Instant.ofEpochMilli(storedDateMillis)
            .atZone(zoneId)
            .toLocalDate()

        if (reminderDate.isBefore(today)) return null

        val triggerAt = reminderDate.atTime(reminderTime).atZone(zoneId).toInstant()
        return triggerAt.takeIf { it.isAfter(now) }
    }
}

class LocalFollowUpReminderCoordinator internal constructor(
    private val preferences: ReminderPreferenceStore,
    private val workStore: ReminderWorkStore,
    private val clock: Clock,
    private val zoneId: ZoneId
) : FollowUpReminderCoordinator {

    constructor(context: Context) : this(
        preferences = FollowUpReminderPreferences(context),
        workStore = WorkManagerReminderWorkStore(context),
        clock = Clock.systemDefaultZone(),
        zoneId = ZoneId.systemDefault()
    )

    override fun setRemindersEnabled(enabled: Boolean) {
        preferences.remindersEnabled = enabled
        if (!enabled) workStore.cancelAll()
    }

    override fun syncAdvocacyFollowUp(session: AdvocacySessionEntity) {
        syncDate(
            key = ReminderKey(ReminderKind.ADVOCACY_FOLLOW_UP, session.id),
            dateMillis = session.followUpDate,
            eligible = session.needsFollowUp
        )
    }

    override fun syncInteractionFollowUp(interaction: InteractionEntity) {
        syncDate(
            key = ReminderKey(ReminderKind.INTERACTION_FOLLOW_UP, interaction.id),
            dateMillis = interaction.followUpDate,
            eligible = interaction.needsFollowUp
        )
    }

    override fun cancelInteractionFollowUp(interactionId: Long) {
        workStore.cancel(ReminderKey(ReminderKind.INTERACTION_FOLLOW_UP, interactionId))
    }

    override fun syncBenefitsDeadline(stage: BenefitsStageEntity) {
        syncDate(
            key = ReminderKey(ReminderKind.BENEFITS_DEADLINE, stage.id.toLong()),
            dateMillis = stage.deadlineDate,
            eligible = stage.status != "Completed"
        )
    }

    override fun syncClaimReminders(claim: DisabilityClaimEntity) {
        syncDate(
            key = ReminderKey(ReminderKind.CLAIM_NEXT_ACTION, claim.id),
            dateMillis = claim.nextActionDueDate,
            eligible = true
        )
        syncDate(
            key = ReminderKey(ReminderKind.CLAIM_APPEAL_DEADLINE, claim.id),
            dateMillis = claim.appealDeadline,
            eligible = true
        )
    }

    override fun cancelClaimReminders(claimId: Long) {
        workStore.cancel(ReminderKey(ReminderKind.CLAIM_NEXT_ACTION, claimId))
        workStore.cancel(ReminderKey(ReminderKind.CLAIM_APPEAL_DEADLINE, claimId))
    }

    override suspend fun reconcileAll(database: UnseenDatabase) {
        if (!preferences.remindersEnabled) {
            workStore.cancelAll()
            return
        }

        val sessions = database.advocacySessionDao().getAllSessions()
        val linkedInteractionIds = sessions.mapNotNullTo(mutableSetOf()) {
            it.linkedInteractionId
        }
        sessions.forEach(::syncAdvocacyFollowUp)

        database.interactionDao().getAllInteractionsOnce().forEach { interaction ->
            if (interaction.id in linkedInteractionIds) {
                cancelInteractionFollowUp(interaction.id)
            } else {
                syncInteractionFollowUp(interaction)
            }
        }
        database.benefitsStageDao().getAllStagesOnce().forEach(::syncBenefitsDeadline)
        database.disabilityClaimDao().getAllClaims().forEach(::syncClaimReminders)
    }

    private fun syncDate(
        key: ReminderKey,
        dateMillis: Long?,
        eligible: Boolean
    ) {
        if (!preferences.remindersEnabled || !eligible || dateMillis == null) {
            workStore.cancel(key)
            return
        }

        val triggerAt = FollowUpReminderTiming.triggerInstant(
            storedDateMillis = dateMillis,
            clock = clock,
            zoneId = zoneId
        )
        if (triggerAt == null) {
            workStore.cancel(key)
        } else {
            workStore.replace(ScheduledReminder(key, triggerAt))
        }
    }
}
