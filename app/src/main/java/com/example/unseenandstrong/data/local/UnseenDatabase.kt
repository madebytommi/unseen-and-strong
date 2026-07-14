package com.example.unseenandstrong.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.migration.Migration
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.unseenandstrong.data.local.checkin.DailyCheckInDao
import com.example.unseenandstrong.data.local.checkin.DailyCheckInEntity
import com.example.unseenandstrong.data.local.interaction.InteractionDao
import com.example.unseenandstrong.data.local.interaction.InteractionEntity
import com.example.unseenandstrong.data.local.journal.JournalDao
import com.example.unseenandstrong.data.local.journal.JournalEntryEntity
import com.example.unseenandstrong.data.local.routine.RoutineDao
import com.example.unseenandstrong.data.local.routine.RoutineTaskEntity
import com.example.unseenandstrong.data.local.script.ScriptDao
import com.example.unseenandstrong.data.local.script.ScriptEntity
import com.example.unseenandstrong.data.local.vault.VaultDocumentDao
import com.example.unseenandstrong.data.local.vault.VaultDocumentEntity
import com.example.unseenandstrong.data.local.accommodation.AccommodationRequestEntity
import com.example.unseenandstrong.data.local.accommodation.AccommodationRequestDao
import com.example.unseenandstrong.data.local.benefits.BenefitsStageEntity
import com.example.unseenandstrong.data.local.benefits.BenefitsStageDao
import com.example.unseenandstrong.data.local.medication.MedLogDao
import com.example.unseenandstrong.data.local.medication.MedLogEntity
import com.example.unseenandstrong.data.local.medication.MedicationDao
import com.example.unseenandstrong.data.local.medication.MedicationEntity
import com.example.unseenandstrong.data.local.medication.PRNLogDao
import com.example.unseenandstrong.data.local.medication.PRNLogEntity
import com.example.unseenandstrong.data.local.medication.ReactionDao
import com.example.unseenandstrong.data.local.medication.ReactionEntity

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
        ReactionEntity::class
    ],
    version = 11,
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
                    .addMigrations(MIGRATION_6_7, MIGRATION_7_8, MIGRATION_8_9, MIGRATION_9_10, MIGRATION_10_11)
                    .addCallback(SEED_SCRIPTS_CALLBACK)
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE interactions ADD COLUMN followUpDateMillis INTEGER"
                )
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
                    SELECT
                        id,
                        timestamp,
                        CASE WHEN followUpDateMillis IS NULL THEN 0 ELSE 1 END,
                        followUpDateMillis,
                        category,
                        personName,
                        organization,
                        notes
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
                db.execSQL("INSERT INTO benefits_stages (stageOrder, stageName, status, notes) VALUES (1, 'Initial Application', 'Pending', '')")
                db.execSQL("INSERT INTO benefits_stages (stageOrder, stageName, status, notes) VALUES (2, 'Medical Evaluation', 'Pending', '')")
                db.execSQL("INSERT INTO benefits_stages (stageOrder, stageName, status, notes) VALUES (3, 'Reconsideration (Appeal 1)', 'Pending', '')")
                db.execSQL("INSERT INTO benefits_stages (stageOrder, stageName, status, notes) VALUES (4, 'Hearing (Appeal 2)', 'Pending', '')")
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

        private val SEED_SCRIPTS_CALLBACK = object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)

                db.execSQL(
                    """
                    INSERT INTO scripts (category, title, gentleText, directText, firmText)
                    VALUES (
                        'Doctor',
                        'Requesting symptom support',
                        'I have been managing persistent symptoms, and I would really appreciate your help making a plan that feels manageable for me.',
                        'My symptoms are affecting daily function. I need clear next steps for treatment and follow-up.',
                        'These symptoms are significantly impacting my life. I need this concern documented and a concrete care plan today.'
                    )
                    """.trimIndent()
                )

                db.execSQL(
                    """
                    INSERT INTO scripts (category, title, gentleText, directText, firmText)
                    VALUES (
                        'Work',
                        'Asking for reasonable flexibility',
                        'I am committed to my role and would appreciate a small adjustment so I can keep contributing consistently.',
                        'I need a reasonable accommodation to manage my health while maintaining my work responsibilities.',
                        'I am formally requesting accommodations so I can perform essential duties safely and sustainably.'
                    )
                    """.trimIndent()
                )

                db.execSQL(
                    """
                    INSERT INTO scripts (category, title, gentleText, directText, firmText)
                    VALUES (
                        'Boundary',
                        'Protecting energy and rest',
                        'I care about this, but I need to pause and rest right now. I can revisit this when I have capacity.',
                        'I cannot take this on today. I need to protect my energy and keep my commitments realistic.',
                        'I am not available for this. Please respect that decision and do not pressure me to explain further.'
                    )
                    """.trimIndent()
                )

                db.execSQL("INSERT INTO benefits_stages (stageOrder, stageName, status, notes) VALUES (1, 'Initial Application', 'Pending', '')")
                db.execSQL("INSERT INTO benefits_stages (stageOrder, stageName, status, notes) VALUES (2, 'Medical Evaluation', 'Pending', '')")
                db.execSQL("INSERT INTO benefits_stages (stageOrder, stageName, status, notes) VALUES (3, 'Reconsideration (Appeal 1)', 'Pending', '')")
                db.execSQL("INSERT INTO benefits_stages (stageOrder, stageName, status, notes) VALUES (4, 'Hearing (Appeal 2)', 'Pending', '')")
            }
        }
    }
}
