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
    fun migrationCreatesAdvocacyStorageAndSeedsExistingInstallExactlyOnce() {
        val callback = object : SupportSQLiteOpenHelper.Callback(12) {
            override fun onCreate(db: SupportSQLiteDatabase) {
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
                    INSERT INTO scripts (category, title, gentleText, directText, firmText)
                    VALUES ('Doctor', 'Requesting symptom support', 'existing gentle', 'existing direct', 'existing firm')
                    """.trimIndent()
                )
            }

            override fun onUpgrade(db: SupportSQLiteDatabase, oldVersion: Int, newVersion: Int) = Unit
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

        db.query("SELECT COUNT(*) FROM advocacy_sessions").use { cursor ->
            assertTrue(cursor.moveToFirst())
            assertEquals(0, cursor.getInt(0))
        }
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
            "SELECT gentleText FROM scripts WHERE category = 'Doctor' AND title = 'Requesting symptom support'"
        ).use { cursor ->
            assertTrue(cursor.moveToFirst())
            assertEquals("existing gentle", cursor.getString(0))
        }
    }

    private companion object {
        const val TEST_DATABASE = "migration-12-13-test"
    }
}
