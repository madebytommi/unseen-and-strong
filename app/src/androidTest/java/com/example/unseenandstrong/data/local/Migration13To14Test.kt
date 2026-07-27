package com.example.unseenandstrong.data.local

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class Migration13To14Test {
    private val TEST_DB = "migration-test"

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        UnseenDatabase::class.java.canonicalName,
        FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    @Throws(IOException::class)
    fun migrate13To14() {
        var db = helper.createDatabase(TEST_DB, 13)

        // Insert representative data for version 13
        db.execSQL("INSERT INTO interactions (timestamp, needsFollowUp, category, personName, organization, notes) VALUES (1000, 0, 'Category1', 'Name1', 'Org1', 'Note1')")
        db.execSQL("INSERT INTO accommodation_requests (requestType, submissionDate, status, notes) VALUES ('Type1', 2000, 'Status1', 'Notes1')")
        db.execSQL("INSERT INTO vault_documents (title, category, fileUri, dateAdded) VALUES ('DocTitle', 'DocCategory', 'uri://file', 3000)")
        db.execSQL("INSERT INTO advocacy_sessions (scriptTitle, scriptCategory, selectedTone, scriptTextSnapshot, personName, organization, desiredOutcome, smallGoal, preparationNote, mayNeedFollowUp, createdAt, conversationHappened, outcomeSummary, emotionalReflection, goalResult, needsFollowUp, reflectionNote, reflectionComplete, updatedAt) VALUES ('title', 'cat', 'tone', 'text', 'person', 'org', 'outcome', 'goal', 'prep', 0, 1000, 'happened', 'sum', 'emot', 'result', 0, 'ref', 1, 2000)")
        
        db.close()

        // Run migration
        db = helper.runMigrationsAndValidate(TEST_DB, 14, true, UnseenDatabase.MIGRATION_13_14)

        // Verify version 13 data survived
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

        // Verify new tables are created and empty
        cursor = db.query("SELECT * FROM disability_claims")
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
}
