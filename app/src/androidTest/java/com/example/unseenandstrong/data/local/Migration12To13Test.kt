package com.example.unseenandstrong.data.local

import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.unseenandstrong.data.local.script.ScriptSeedData
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class Migration12To13Test {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private var helper: SupportSQLiteOpenHelper? = null

    @After
    fun tearDown() {
        helper?.close()
        context.deleteDatabase(TEST_DATABASE)
    }

    @Test
    fun migrationCreatesAdvocacyStorageSeedsScriptsAndPreservesExistingData() {
        val callback = object : SupportSQLiteOpenHelper.Callback(12) {
            override fun onCreate(db: SupportSQLiteDatabase) {
                createVersion12Tables(db)
                insertVersion12Records(db)
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

        UnseenDatabase.MIGRATION_12_13.migrate(db)
        UnseenDatabase.MIGRATION_12_13.migrate(db)

        assertAdvocacySchema(db)
        assertSeededScripts(db)
        assertExistingRecordsRemain(db)
    }

    private fun createVersion12Tables(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS scripts (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                category TEXT NOT NULL,
                title TEXT NOT NULL,
                gentleText TEXT NOT NULL,
                directText TEXT NOT NULL,
                firmText TEXT NOT NULL
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS interactions (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                timestamp INTEGER NOT NULL,
                needsFollowUp INTEGER NOT NULL,
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
    }

    private fun insertVersion12Records(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            INSERT INTO scripts (category, title, gentleText, directText, firmText)
            VALUES ('Doctor', 'Requesting symptom support', 'existing gentle', 'existing direct', 'existing firm')
            """.trimIndent()
        )
        db.execSQL(
            """
            INSERT INTO scripts (category, title, gentleText, directText, firmText)
            VALUES ('Boundary', 'Protecting energy and rest', 'existing boundary gentle', 'existing boundary direct', 'existing boundary firm')
            """.trimIndent()
        )
        db.execSQL(
            """
            INSERT INTO interactions (
                id, timestamp, needsFollowUp, followUpDate, category, personName, organization, notes
            ) VALUES (41, 1000, 1, 2000, 'Doctor', 'Jordan', 'Clinic', 'existing interaction')
            """.trimIndent()
        )
        db.execSQL(
            """
            INSERT INTO benefits_stages (
                id, stageOrder, stageName, status, dateCompleted, deadlineDate, notes
            ) VALUES (7, 1, 'Initial Application', 'Active', NULL, 3000, 'existing benefits note')
            """.trimIndent()
        )
    }

    private fun assertAdvocacySchema(db: SupportSQLiteDatabase) {
        val expectedColumns = setOf(
            "id",
            "scriptId",
            "scriptTitle",
            "scriptCategory",
            "selectedTone",
            "scriptTextSnapshot",
            "personName",
            "organization",
            "desiredOutcome",
            "smallGoal",
            "preparationNote",
            "mayNeedFollowUp",
            "createdAt",
            "conversationHappened",
            "outcomeSummary",
            "emotionalReflection",
            "goalResult",
            "needsFollowUp",
            "followUpDate",
            "reflectionNote",
            "reflectionComplete",
            "updatedAt",
            "linkedInteractionId"
        )
        val actualColumns = mutableSetOf<String>()

        db.query("PRAGMA table_info(advocacy_sessions)").use { cursor ->
            val nameIndex = cursor.getColumnIndexOrThrow("name")
            while (cursor.moveToNext()) {
                actualColumns += cursor.getString(nameIndex)
            }
        }

        assertEquals(expectedColumns, actualColumns)
        db.query("SELECT COUNT(*) FROM advocacy_sessions").use { cursor ->
            assertTrue(cursor.moveToFirst())
            assertEquals(0, cursor.getInt(0))
        }
    }

    private fun assertSeededScripts(db: SupportSQLiteDatabase) {
        db.query("SELECT COUNT(*) FROM scripts").use { cursor ->
            assertTrue(cursor.moveToFirst())
            assertEquals(ScriptSeedData.scripts.size, cursor.getInt(0))
        }
        db.query(
            "SELECT COUNT(*) FROM scripts WHERE category IN ('Insurance', 'Family', 'Strangers')"
        ).use { cursor ->
            assertTrue(cursor.moveToFirst())
            assertEquals(15, cursor.getInt(0))
        }
        db.query(
            """
            SELECT category, title, COUNT(*) AS duplicateCount
            FROM scripts
            GROUP BY category, title
            HAVING duplicateCount > 1
            """.trimIndent()
        ).use { cursor ->
            assertEquals(0, cursor.count)
        }
        db.query(
            "SELECT gentleText FROM scripts WHERE category = 'Doctor' AND title = 'Requesting symptom support'"
        ).use { cursor ->
            assertTrue(cursor.moveToFirst())
            assertEquals("existing gentle", cursor.getString(0))
        }
        db.query(
            "SELECT gentleText FROM scripts WHERE category = 'Boundary' AND title = 'Protecting energy and rest'"
        ).use { cursor ->
            assertTrue(cursor.moveToFirst())
            assertEquals("existing boundary gentle", cursor.getString(0))
        }
    }

    private fun assertExistingRecordsRemain(db: SupportSQLiteDatabase) {
        db.query("SELECT notes FROM interactions WHERE id = 41").use { cursor ->
            assertTrue(cursor.moveToFirst())
            assertEquals("existing interaction", cursor.getString(0))
        }
        db.query("SELECT status, notes FROM benefits_stages WHERE id = 7").use { cursor ->
            assertTrue(cursor.moveToFirst())
            assertEquals("Active", cursor.getString(0))
            assertEquals("existing benefits note", cursor.getString(1))
        }
    }

    private companion object {
        const val TEST_DATABASE = "migration-12-13-test"
    }
}
