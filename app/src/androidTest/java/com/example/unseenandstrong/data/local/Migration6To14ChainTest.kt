package com.example.unseenandstrong.data.local

import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.unseenandstrong.data.local.accommodation.AccommodationRequestEntity
import com.example.unseenandstrong.data.local.advocacy.AdvocacySessionEntity
import com.example.unseenandstrong.data.local.benefits.BenefitsStageEntity
import com.example.unseenandstrong.data.local.claims.ClaimDocumentCrossRef
import com.example.unseenandstrong.data.local.claims.ClaimInteractionCrossRef
import com.example.unseenandstrong.data.local.claims.DisabilityClaimEntity
import com.example.unseenandstrong.data.local.claims.DisabilityClaimTaskEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/** Exercises every supported migration in order against representative local records. */
@RunWith(AndroidJUnit4::class)
class Migration6To14ChainTest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private var helper: SupportSQLiteOpenHelper? = null

    @After
    fun tearDown() {
        helper?.close()
        context.deleteDatabase(TEST_DATABASE)
    }

    @Test
    fun oldestSupportedSchemaReachesCurrentSchemaWithoutDroppingRecords() = runBlocking {
        context.deleteDatabase(TEST_DATABASE)
        val callback = object : SupportSQLiteOpenHelper.Callback(6) {
            override fun onCreate(db: SupportSQLiteDatabase) {
                createVersion6Tables(db)
                insertVersion6Records(db)
            }

            override fun onUpgrade(
                db: SupportSQLiteDatabase,
                oldVersion: Int,
                newVersion: Int
            ) = Unit
        }
        helper = FrameworkSQLiteOpenHelperFactory().create(
            SupportSQLiteOpenHelper.Configuration.builder(context)
                .name(TEST_DATABASE)
                .callback(callback)
                .build()
        )
        val db = requireNotNull(helper).writableDatabase
        db.execSQL("PRAGMA user_version = 6")
        helper?.close()
        helper = null

        // Opening through the production builder runs the registered 6->14 chain and Room's
        // generated final-schema validation before any DAO can be used.
        val roomDb = UnseenDatabase.openDatabase(context, TEST_DATABASE)
        try {
            val interaction = roomDb.interactionDao().getInteractionById(41)
            val document = roomDb.vaultDocumentDao().getDocument(51)
            assertEquals("Alex", interaction?.personName)
            assertFalse(interaction?.needsFollowUp ?: true)
            assertEquals(null, interaction?.followUpDate)
            assertEquals("content://documents/medical", document?.fileUri)

            val interactionWithFollowUp = requireNotNull(interaction).copy(
                needsFollowUp = true,
                followUpDate = 1500
            )
            roomDb.interactionDao().updateInteraction(interactionWithFollowUp)
            val reloadedInteraction = roomDb.interactionDao().getInteractionById(41)
            assertTrue(reloadedInteraction?.needsFollowUp == true)
            assertEquals(1500L, reloadedInteraction?.followUpDate)

            val requestId = roomDb.accommodationRequestDao().insertRequest(
                AccommodationRequestEntity(
                    requestType = "ADA",
                    submissionDate = 2000,
                    status = "Draft",
                    notes = "request note"
                )
            )
            assertEquals("ADA", roomDb.accommodationRequestDao().getRequest(requestId.toInt())?.requestType)

            roomDb.benefitsStageDao().insertStage(
                BenefitsStageEntity(
                    stageOrder = 9,
                    stageName = "Historical review",
                    status = "Active",
                    deadlineDate = 3000,
                    notes = "benefits note"
                )
            )
            assertTrue(roomDb.benefitsStageDao().getAllStagesOnce().any { it.stageName == "Historical review" })

            val sessionId = roomDb.advocacySessionDao().insertSession(
                AdvocacySessionEntity(
                    scriptId = null,
                    scriptTitle = "Follow-up",
                    scriptCategory = "Work",
                    selectedTone = "Gentle",
                    scriptTextSnapshot = "script",
                    personName = "Alex",
                    organization = "Employer",
                    desiredOutcome = "A response",
                    smallGoal = "One step",
                    preparationNote = "note",
                    mayNeedFollowUp = true,
                    createdAt = 5000,
                    conversationHappened = "Yes",
                    outcomeSummary = "Summary",
                    emotionalReflection = "Reflection",
                    goalResult = "Progress",
                    needsFollowUp = true,
                    reflectionNote = "Reflection note",
                    reflectionComplete = false,
                    updatedAt = 6000
                )
            )
            assertEquals("Follow-up", roomDb.advocacySessionDao().getSessionById(sessionId)?.scriptTitle)

            val claimId = roomDb.disabilityClaimDao().insertClaim(
                DisabilityClaimEntity(
                    claimType = "STD",
                    employerName = "Employer",
                    administratorName = "Administrator",
                    claimNumber = "A-1",
                    status = "Preparing",
                    nextAction = "Gather forms",
                    notes = "claim note",
                    createdAt = 7000,
                    updatedAt = 7000
                )
            )
            roomDb.disabilityClaimDao().insertTask(
                DisabilityClaimTaskEntity(
                    claimId = claimId,
                    category = "Forms",
                    title = "Request records",
                    status = "Not started",
                    sortOrder = 0,
                    createdAt = 7000,
                    updatedAt = 7000
                )
            )
            roomDb.disabilityClaimDao().linkInteraction(
                ClaimInteractionCrossRef(claimId, interactionId = 41)
            )
            roomDb.disabilityClaimDao().linkDocument(
                ClaimDocumentCrossRef(claimId, documentId = 51)
            )

            val claim = roomDb.disabilityClaimDao().getClaim(claimId)
            assertEquals("STD", claim?.claimType)
            assertEquals(1, roomDb.disabilityClaimDao().observeTasksForClaim(claimId).first().size)
            assertEquals(1, roomDb.disabilityClaimDao().observeLinkedInteractions(claimId).first().size)
            assertEquals(1, roomDb.disabilityClaimDao().observeLinkedDocuments(claimId).first().size)
        } finally {
            roomDb.close()
        }
    }

    private fun createVersion6Tables(db: SupportSQLiteDatabase) {
        db.execSQL("CREATE TABLE daily_check_ins (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, entry_date TEXT NOT NULL, pain_level INTEGER NOT NULL, spoons_level INTEGER NOT NULL, mood TEXT NOT NULL)")
        db.execSQL("CREATE UNIQUE INDEX index_daily_check_ins_entry_date ON daily_check_ins(entry_date)")
        db.execSQL("CREATE TABLE journal_entries (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, timestamp INTEGER NOT NULL, content TEXT NOT NULL, isUnseenWin INTEGER NOT NULL)")
        db.execSQL("CREATE TABLE routine_tasks (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, taskName TEXT NOT NULL, isCompleted INTEGER NOT NULL)")
        db.execSQL("CREATE TABLE scripts (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, category TEXT NOT NULL, title TEXT NOT NULL, gentleText TEXT NOT NULL, directText TEXT NOT NULL, firmText TEXT NOT NULL)")
        db.execSQL("CREATE TABLE interactions (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, timestamp INTEGER NOT NULL, category TEXT NOT NULL, personName TEXT NOT NULL, organization TEXT NOT NULL, notes TEXT NOT NULL)")
        db.execSQL("CREATE TABLE vault_documents (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, title TEXT NOT NULL, category TEXT NOT NULL, fileUri TEXT NOT NULL, dateAdded INTEGER NOT NULL)")
    }

    private fun insertVersion6Records(db: SupportSQLiteDatabase) {
        db.execSQL("INSERT INTO scripts (id, category, title, gentleText, directText, firmText) VALUES (1, 'Doctor', 'Existing script', 'gentle', 'direct', 'firm')")
        db.execSQL("INSERT INTO interactions (id, timestamp, category, personName, organization, notes) VALUES (41, 1000, 'Doctor', 'Alex', 'Clinic', 'existing interaction')")
        db.execSQL("INSERT INTO vault_documents (id, title, category, fileUri, dateAdded) VALUES (51, 'Medical letter', 'Medical', 'content://documents/medical', 1100)")
    }

    private companion object {
        const val TEST_DATABASE = "migration-6-14-chain-test"
    }
}
