package com.example.unseenandstrong.data.local

import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class Migration13To14Test {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private var helper: SupportSQLiteOpenHelper? = null

    @After
    fun tearDown() {
        helper?.close()
        context.deleteDatabase(TEST_DATABASE)
    }

    @Test
    fun migrate13To14() {
        val callback = object : SupportSQLiteOpenHelper.Callback(13) {
            override fun onCreate(db: SupportSQLiteDatabase) {
                createVersion13Tables(db)
                insertVersion13Records(db)
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

        UnseenDatabase.MIGRATION_13_14.migrate(db)

        assertVersion13RecordsSurvive(db)
        assertNewTablesCreated(db)
    }

    private fun createVersion13Tables(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS interactions (
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
            CREATE TABLE IF NOT EXISTS accommodation_requests (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                requestType TEXT NOT NULL,
                submissionDate INTEGER NOT NULL,
                status TEXT NOT NULL,
                notes TEXT NOT NULL
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS vault_documents (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                title TEXT NOT NULL,
                category TEXT NOT NULL,
                fileUri TEXT NOT NULL,
                dateAdded INTEGER NOT NULL
            )
            """.trimIndent()
        )
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
    }

    private fun insertVersion13Records(db: SupportSQLiteDatabase) {
        db.execSQL("INSERT INTO interactions (timestamp, needsFollowUp, category, personName, organization, notes) VALUES (1000, 0, 'Category1', 'Name1', 'Org1', 'Note1')")
        db.execSQL("INSERT INTO accommodation_requests (requestType, submissionDate, status, notes) VALUES ('Type1', 2000, 'Status1', 'Notes1')")
        db.execSQL("INSERT INTO vault_documents (title, category, fileUri, dateAdded) VALUES ('DocTitle', 'DocCategory', 'uri://file', 3000)")
        db.execSQL("INSERT INTO advocacy_sessions (scriptTitle, scriptCategory, selectedTone, scriptTextSnapshot, personName, organization, desiredOutcome, smallGoal, preparationNote, mayNeedFollowUp, createdAt, conversationHappened, outcomeSummary, emotionalReflection, goalResult, needsFollowUp, reflectionNote, reflectionComplete, updatedAt) VALUES ('title', 'cat', 'tone', 'text', 'person', 'org', 'outcome', 'goal', 'prep', 0, 1000, 'happened', 'sum', 'emot', 'result', 0, 'ref', 1, 2000)")
    }

    private fun assertVersion13RecordsSurvive(db: SupportSQLiteDatabase) {
        var cursor = db.query("SELECT * FROM interactions")
        assertTrue(cursor.moveToFirst())
        assertEquals("Name1", cursor.getString(cursor.getColumnIndex("personName")))
        cursor.close()

        cursor = db.query("SELECT * FROM accommodation_requests")
        assertTrue(cursor.moveToFirst())
        assertEquals("Type1", cursor.getString(cursor.getColumnIndex("requestType")))
        cursor.close()

        cursor = db.query("SELECT * FROM vault_documents")
        assertTrue(cursor.moveToFirst())
        assertEquals("DocTitle", cursor.getString(cursor.getColumnIndex("title")))
        cursor.close()

        cursor = db.query("SELECT * FROM advocacy_sessions")
        assertTrue(cursor.moveToFirst())
        assertEquals("title", cursor.getString(cursor.getColumnIndex("scriptTitle")))
        cursor.close()
    }

    private fun assertNewTablesCreated(db: SupportSQLiteDatabase) {
        var cursor = db.query("SELECT * FROM disability_claims")
        assertTrue(!cursor.moveToFirst())
        assertEquals(19, cursor.columnCount)
        cursor.close()

        cursor = db.query("SELECT * FROM disability_claim_tasks")
        assertTrue(!cursor.moveToFirst())
        assertEquals(11, cursor.columnCount)
        cursor.close()

        cursor = db.query("SELECT * FROM claim_interaction_cross_ref")
        assertTrue(!cursor.moveToFirst())
        assertEquals(2, cursor.columnCount)
        cursor.close()

        cursor = db.query("SELECT * FROM claim_document_cross_ref")
        assertTrue(!cursor.moveToFirst())
        assertEquals(2, cursor.columnCount)
        cursor.close()
    }

    private companion object {
        const val TEST_DATABASE = "migration-13-14-test"
    }
}
