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
    fun preparationSavesReloadsAndReflectionUpdatesSameRecord() = runBlocking {
        val id = dao.insertSession(
            AdvocacySessionEntity(
                scriptId = 7,
                scriptTitle = "Ask for written denial",
                scriptCategory = "Insurance",
                selectedTone = "DIRECT",
                scriptTextSnapshot = "Please provide the denial in writing.",
                desiredOutcome = "Written decision",
                createdAt = 1_000L,
                updatedAt = 1_000L
            )
        )

        val saved = requireNotNull(dao.getSessionById(id))
        assertEquals("Written decision", saved.desiredOutcome)
        assertNull(saved.followUpDate)

        dao.updateSession(
            saved.copy(
                conversationHappened = "Yes",
                outcomeSummary = "The written notice was requested.",
                goalResult = "Partly",
                needsFollowUp = true,
                followUpDate = 2_000L,
                updatedAt = 3_000L
            )
        )

        val updated = requireNotNull(dao.observeSession(id).first())
        assertEquals(id, updated.id)
        assertEquals("The written notice was requested.", updated.outcomeSummary)
        assertEquals("Partly", updated.goalResult)
        assertEquals(2_000L, updated.followUpDate)

        dao.updateSession(updated.copy(needsFollowUp = false, followUpDate = null))
        assertNull(requireNotNull(dao.getSessionById(id)).followUpDate)
    }
}
