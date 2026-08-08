package com.example.unseenandstrong.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.unseenandstrong.data.local.accommodation.AccommodationRequestDao
import com.example.unseenandstrong.data.local.accommodation.AccommodationRequestEntity
import com.example.unseenandstrong.data.local.advocacy.AdvocacySessionDao
import com.example.unseenandstrong.data.local.advocacy.AdvocacySessionEntity
import com.example.unseenandstrong.data.local.benefits.BenefitsStageDao
import com.example.unseenandstrong.data.local.benefits.BenefitsStageEntity
import com.example.unseenandstrong.data.local.claims.ClaimDocumentCrossRef
import com.example.unseenandstrong.data.local.claims.ClaimInteractionCrossRef
import com.example.unseenandstrong.data.local.claims.DisabilityClaimDao
import com.example.unseenandstrong.data.local.claims.DisabilityClaimEntity
import com.example.unseenandstrong.data.local.claims.DisabilityClaimTaskEntity
import com.example.unseenandstrong.data.local.checkin.DailyCheckInDao
import com.example.unseenandstrong.data.local.checkin.DailyCheckInEntity
import com.example.unseenandstrong.data.local.cycle.CycleLogDao
import com.example.unseenandstrong.data.local.cycle.CycleLogEntity
import com.example.unseenandstrong.data.local.cycle.CycleSettingsDao
import com.example.unseenandstrong.data.local.cycle.CycleSettingsEntity
import com.example.unseenandstrong.data.local.interaction.InteractionDao
import com.example.unseenandstrong.data.local.interaction.InteractionEntity
import com.example.unseenandstrong.data.local.journal.JournalDao
import com.example.unseenandstrong.data.local.journal.JournalEntryEntity
import com.example.unseenandstrong.data.local.medication.MedLogDao
import com.example.unseenandstrong.data.local.medication.MedLogEntity
import com.example.unseenandstrong.data.local.medication.MedicationDao
import com.example.unseenandstrong.data.local.medication.MedicationEntity
import com.example.unseenandstrong.data.local.medication.PRNLogDao
import com.example.unseenandstrong.data.local.medication.PRNLogEntity
import com.example.unseenandstrong.data.local.medication.ReactionDao
import com.example.unseenandstrong.data.local.medication.ReactionEntity
import com.example.unseenandstrong.data.local.routine.RoutineDao
import com.example.unseenandstrong.data.local.routine.RoutineTaskEntity
import com.example.unseenandstrong.data.local.script.ScriptDao
import com.example.unseenandstrong.data.local.script.ScriptEntity
import com.example.unseenandstrong.data.local.script.ScriptSeedData
import com.example.unseenandstrong.data.local.vault.VaultDocumentDao
import com.example.unseenandstrong.data.local.vault.VaultDocumentEntity

@Database(
    entities = [
        DailyCheckInEntity::class,
        JournalEntryEntity::class,
        RoutineTaskEntity::class,
        ScriptEntity::class,
        InteractionEntity::class,
        VaultDocumentEntity::class,
        AccommodationRequestEntity::class,
        BenefitsStageEntity::class,
        MedicationEntity::class,
        MedLogEntity::class,
        PRNLogEntity::class,
        ReactionEntity::class,
        CycleLogEntity::class,
        CycleSettingsEntity::class,
        AdvocacySessionEntity::class,
        DisabilityClaimEntity::class,
        DisabilityClaimTaskEntity::class,
        ClaimInteractionCrossRef::class,
        ClaimDocumentCrossRef::class
    ],
    version = 14,
    exportSchema = false
)
abstract class UnseenDatabase : RoomDatabase() {

    abstract fun dailyCheckInDao(): DailyCheckInDao
    abstract fun journalDao(): JournalDao
    abstract fun routineDao(): RoutineDao
    abstract fun scriptDao(): ScriptDao
    abstract fun interactionDao(): InteractionDao
    abstract fun vaultDocumentDao(): VaultDocumentDao
    abstract fun accommodationRequestDao(): AccommodationRequestDao
    abstract fun benefitsStageDao(): BenefitsStageDao
    abstract fun medicationDao(): MedicationDao
    abstract fun medLogDao(): MedLogDao
    abstract fun prnLogDao(): PRNLogDao
    abstract fun reactionDao(): ReactionDao
    abstract fun cycleLogDao(): CycleLogDao
    abstract fun cycleSettingsDao(): CycleSettingsDao
    abstract fun advocacySessionDao(): AdvocacySessionDao
    abstract fun disabilityClaimDao(): DisabilityClaimDao

    companion object {
        @Volatile
        private var INSTANCE: UnseenDatabase? = null

        fun getDatabase(context: Context): UnseenDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    UnseenDatabase::class.java,
                    "unseen_database"
                )
                    .addMigrations(
                        MIGRATION_6_7,
                        MIGRATION_7_8,
                        MIGRATION_8_9,
                        MIGRATION_9_10,
                        MIGRATION_10_11,
                        MIGRATION_11_12,
                        MIGRATION_12_13,
                        MIGRATION_13_14
                    )
                    .addCallback(SEED_SCRIPTS_CALLBACK)
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE interactions ADD COLUMN followUpDateMillis INTEGER")
            }
        }

        private val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS interactions_new (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        timestamp INTEGER NOT NULL,
                        needsFollowUp INTEGER NOT NULL DEFAULT 0,
                        followUpDate INTEGER,
                        category TEXT NOT NULL,
                        personName TEXT NOT NULL,
                        organization TEXT NOT NULL,
                        notes TEXT NOT NULL
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    INSERT INTO interactions_new (id, timestamp, needsFollowUp, followUpDate, category, personName, organization, notes)
                    SELECT id, timestamp,
                        CASE WHEN followUpDateMillis IS NULL THEN 0 ELSE 1 END,
                        followUpDateMillis, category, personName, organization, notes
                    FROM interactions
                    """.trimIndent()
                )
                db.execSQL("DROP TABLE interactions")
                db.execSQL("ALTER TABLE interactions_new RENAME TO interactions")
            }
        }

        private val MIGRATION_8_9 = object : Migration(8, 9) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS accommodation_requests (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        requestType TEXT NOT NULL,
                        submissionDate INTEGER NOT NULL,
                        status TEXT NOT NULL,
                        notes TEXT NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }

        private val MIGRATION_9_10 = object : Migration(9, 10) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS benefits_stages (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        stageOrder INTEGER NOT NULL,
                        stageName TEXT NOT NULL,
                        status TEXT NOT NULL,
                        dateCompleted INTEGER,
                        deadlineDate INTEGER,
                        notes TEXT NOT NULL
                    )
                    """.trimIndent()
                )
                insertBenefitsStages(db)
            }
        }

        private val MIGRATION_10_11 = object : Migration(10, 11) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS medications (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        name TEXT NOT NULL,
                        dosage TEXT NOT NULL,
                        frequency TEXT NOT NULL,
                        instructions TEXT NOT NULL,
                        isPRN INTEGER NOT NULL,
                        isActive INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS med_logs (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        medId INTEGER NOT NULL,
                        scheduledTime INTEGER NOT NULL,
                        actualTakenTime INTEGER,
                        status TEXT NOT NULL,
                        FOREIGN KEY(medId) REFERENCES medications(id) ON DELETE CASCADE
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS index_med_logs_medId ON med_logs(medId)")
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS prn_logs (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        medId INTEGER NOT NULL,
                        timeTaken INTEGER NOT NULL,
                        reason TEXT NOT NULL,
                        reliefDurationHours INTEGER NOT NULL,
                        effectivenessRating INTEGER NOT NULL,
                        FOREIGN KEY(medId) REFERENCES medications(id) ON DELETE CASCADE
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS index_prn_logs_medId ON prn_logs(medId)")
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS reactions (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        medId INTEGER NOT NULL,
                        date INTEGER NOT NULL,
                        description TEXT NOT NULL,
                        severity INTEGER NOT NULL,
                        FOREIGN KEY(medId) REFERENCES medications(id) ON DELETE CASCADE
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS index_reactions_medId ON reactions(medId)")
            }
        }

        private val MIGRATION_11_12 = object : Migration(11, 12) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS cycle_logs (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        date INTEGER NOT NULL,
                        phase TEXT NOT NULL,
                        flowIntensity TEXT NOT NULL
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS cycle_settings (
                        id INTEGER PRIMARY KEY NOT NULL,
                        trackingMode TEXT NOT NULL
                    )
                    """.trimIndent()
                )
                db.execSQL("INSERT OR IGNORE INTO cycle_settings (id, trackingMode) VALUES (1, 'Standard')")
            }
        }

        internal val MIGRATION_12_13 = object : Migration(12, 13) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS advocacy_sessions (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        scriptId INTEGER,
                        scriptTitle TEXT NOT NULL,
                        scriptCategory TEXT NOT NULL,
                        selectedTone TEXT NOT NULL,
                        scriptTextSnapshot TEXT NOT NULL,
                        personName TEXT NOT NULL,
                        organization TEXT NOT NULL,
                        desiredOutcome TEXT NOT NULL,
                        smallGoal TEXT NOT NULL,
                        preparationNote TEXT NOT NULL,
                        mayNeedFollowUp INTEGER NOT NULL,
                        createdAt INTEGER NOT NULL,
                        conversationHappened TEXT NOT NULL,
                        outcomeSummary TEXT NOT NULL,
                        emotionalReflection TEXT NOT NULL,
                        goalResult TEXT NOT NULL,
                        needsFollowUp INTEGER NOT NULL,
                        followUpDate INTEGER,
                        reflectionNote TEXT NOT NULL,
                        reflectionComplete INTEGER NOT NULL,
                        updatedAt INTEGER NOT NULL,
                        linkedInteractionId INTEGER
                    )
                    """.trimIndent()
                )
                ScriptSeedData.insertMissing(db)
            }
        }

        internal val MIGRATION_13_14 = object : Migration(13, 14) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS disability_claims (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        claimType TEXT NOT NULL,
                        employerName TEXT NOT NULL,
                        administratorName TEXT NOT NULL,
                        claimNumber TEXT NOT NULL,
                        status TEXT NOT NULL,
                        filedDate INTEGER,
                        leaveStartDate INTEGER,
                        leaveEndDate INTEGER,
                        benefitStartDate INTEGER,
                        benefitEndDate INTEGER,
                        decisionDate INTEGER,
                        appealDeadline INTEGER,
                        nextAction TEXT NOT NULL,
                        nextActionDueDate INTEGER,
                        notes TEXT NOT NULL,
                        linkedRequestId INTEGER,
                        createdAt INTEGER NOT NULL,
                        updatedAt INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS disability_claim_tasks (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        claimId INTEGER NOT NULL,
                        category TEXT NOT NULL,
                        title TEXT NOT NULL,
                        status TEXT NOT NULL,
                        dueDate INTEGER,
                        completedDate INTEGER,
                        notes TEXT NOT NULL,
                        sortOrder INTEGER NOT NULL,
                        createdAt INTEGER NOT NULL,
                        updatedAt INTEGER NOT NULL,
                        FOREIGN KEY(claimId) REFERENCES disability_claims(id) ON DELETE CASCADE
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS index_disability_claim_tasks_claimId ON disability_claim_tasks(claimId)")
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS claim_interaction_cross_ref (
                        claimId INTEGER NOT NULL,
                        interactionId INTEGER NOT NULL,
                        PRIMARY KEY(claimId, interactionId)
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS index_claim_interaction_cross_ref_interactionId ON claim_interaction_cross_ref(interactionId)")
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS claim_document_cross_ref (
                        claimId INTEGER NOT NULL,
                        documentId INTEGER NOT NULL,
                        PRIMARY KEY(claimId, documentId)
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS index_claim_document_cross_ref_documentId ON claim_document_cross_ref(documentId)")
            }
        }

        private val SEED_SCRIPTS_CALLBACK = object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                ScriptSeedData.insertMissing(db)
                insertBenefitsStages(db)
            }
        }

        private fun insertBenefitsStages(db: SupportSQLiteDatabase) {
            db.execSQL("INSERT INTO benefits_stages (stageOrder, stageName, status, notes) VALUES (1, 'Initial Application', 'Pending', '')")
            db.execSQL("INSERT INTO benefits_stages (stageOrder, stageName, status, notes) VALUES (2, 'Medical Evaluation', 'Pending', '')")
            db.execSQL("INSERT INTO benefits_stages (stageOrder, stageName, status, notes) VALUES (3, 'Reconsideration (Appeal 1)', 'Pending', '')")
            db.execSQL("INSERT INTO benefits_stages (stageOrder, stageName, status, notes) VALUES (4, 'Hearing (Appeal 2)', 'Pending', '')")
        }
    }
}
