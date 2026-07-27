package com.example.unseenandstrong.data.local.advocacy

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.unseenandstrong.data.local.UnseenDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AdvocacySessionDaoTest {
    private lateinit var database: UnseenDatabase
    private lateinit var dao: AdvocacySessionDao

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getInstrumentation().targetContext,
            UnseenDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = database.advocacySessionDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun emptyTableEmitsAnEmptyList() = runBlocking {
        assertTrue(dao.observeAllSessions().first().isEmpty())
    }

    @Test
    fun preparationSavesReloadsAndReflectionUpdatesSameRecord() = runBlocking {
        val id = dao.insertSession(
            AdvocacySessionEntity(
                scriptId = 7,
                scriptTitle = "Ask for written denial",
                scriptCategory = "Insurance",
                selectedTone = "DIRECT",
                scriptTextSnapshot = "Please provide the denial in writing.",
                desiredOutcome = "Written decision",
                smallGoal = "Get the correct mailing address",
                preparationNote = "Keep the case number nearby",
                createdAt = 1_000L,
                updatedAt = 1_000L
            )
        )

        val saved = requireNotNull(dao.getSessionById(id))
        assertEquals("Written decision", saved.desiredOutcome)
        assertEquals("Please provide the denial in writing.", saved.scriptTextSnapshot)
        assertNull(saved.followUpDate)

        val updatedCount = dao.updateSession(
            saved.copy(
                conversationHappened = "Yes",
                outcomeSummary = "The written notice was requested.",
                emotionalReflection = "Tired but clear about the next step",
                goalResult = "Partly",
                needsFollowUp = true,
                followUpDate = 2_000L,
                reflectionNote = "Call again if nothing arrives",
                updatedAt = 3_000L
            )
        )

        assertEquals(1, updatedCount)
        val updated = requireNotNull(dao.observeSession(id).first())
        assertEquals(id, updated.id)
        assertEquals("The written notice was requested.", updated.outcomeSummary)
        assertEquals("Partly", updated.goalResult)
        assertEquals(2_000L, updated.followUpDate)
        assertEquals("Written decision", updated.desiredOutcome)
        assertEquals("Get the correct mailing address", updated.smallGoal)
        assertEquals("Please provide the denial in writing.", updated.scriptTextSnapshot)
        assertEquals(1, dao.observeAllSessions().first().size)
    }

    @Test
    fun followUpDateCanBeChangedAndCleared() = runBlocking {
        val id = dao.insertSession(
            AdvocacySessionEntity(
                scriptId = null,
                scriptTitle = "Saved script",
                scriptCategory = "Family",
                selectedTone = "GENTLE",
                scriptTextSnapshot = "I need to rest today.",
                needsFollowUp = true,
                followUpDate = 2_000L,
                createdAt = 1_000L,
                updatedAt = 1_000L
            )
        )

        val saved = requireNotNull(dao.getSessionById(id))
        dao.updateSession(saved.copy(followUpDate = 4_000L, updatedAt = 3_000L))
        assertEquals(4_000L, requireNotNull(dao.getSessionById(id)).followUpDate)

        val changed = requireNotNull(dao.getSessionById(id))
        dao.updateSession(
            changed.copy(
                needsFollowUp = false,
                followUpDate = null,
                updatedAt = 5_000L
            )
        )
        assertNull(requireNotNull(dao.getSessionById(id)).followUpDate)
        assertEquals(1, dao.observeAllSessions().first().size)
    }
}
