package com.example.unseenandstrong.ui.speakstrong

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.unseenandstrong.data.local.UnseenDatabase
import com.example.unseenandstrong.data.local.advocacy.AdvocacySessionEntity
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AdvocacySupportViewModelTest {
    private lateinit var database: UnseenDatabase
    private lateinit var viewModel: AdvocacySupportViewModel

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getInstrumentation().targetContext,
            UnseenDatabase::class.java
        ).allowMainThreadQueries().build()
        viewModel = AdvocacySupportViewModel(database)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun reflectionExportIsOptInAndLaterSavesUpdateOneLinkedEntry() = runBlocking {
        val sessionId = database.advocacySessionDao().insertSession(newSession())
        var session = requireNotNull(database.advocacySessionDao().getSessionById(sessionId))

        saveReflection(
            session = session,
            input = reflectionInput(
                outcomeSummary = "The call happened.",
                exportToInteractionLog = false
            )
        )

        session = requireNotNull(database.advocacySessionDao().getSessionById(sessionId))
        assertNull(session.linkedInteractionId)
        assertTrue(database.interactionDao().getAllInteractions().first().isEmpty())
        assertEquals("The call happened.", session.outcomeSummary)
        assertEquals("Please explain the denial.", session.scriptTextSnapshot)
        assertEquals("Written explanation", session.desiredOutcome)

        saveReflection(
            session = session,
            input = reflectionInput(
                outcomeSummary = "A written notice was requested.",
                exportToInteractionLog = true
            )
        )

        session = requireNotNull(database.advocacySessionDao().getSessionById(sessionId))
        val firstLinkedId = session.linkedInteractionId
        assertNotNull(firstLinkedId)
        var interactions = database.interactionDao().getAllInteractions().first()
        assertEquals(1, interactions.size)
        assertEquals(firstLinkedId, interactions.single().id)
        assertTrue(interactions.single().notes.contains("A written notice was requested."))

        saveReflection(
            session = session,
            input = reflectionInput(
                outcomeSummary = "The written notice arrived.",
                exportToInteractionLog = false,
                needsFollowUp = false,
                followUpDate = null
            )
        )

        session = requireNotNull(database.advocacySessionDao().getSessionById(sessionId))
        interactions = database.interactionDao().getAllInteractions().first()
        assertEquals(1, interactions.size)
        assertEquals(firstLinkedId, interactions.single().id)
        assertTrue(interactions.single().notes.contains("The written notice arrived."))
        assertNull(interactions.single().followUpDate)
        assertNull(session.followUpDate)
    }

    @Test
    fun missingLinkedInteractionIsRecreatedSafely() = runBlocking {
        val sessionId = database.advocacySessionDao().insertSession(newSession())
        var session = requireNotNull(database.advocacySessionDao().getSessionById(sessionId))

        saveReflection(
            session = session,
            input = reflectionInput(
                outcomeSummary = "Initial outcome",
                exportToInteractionLog = true
            )
        )

        session = requireNotNull(database.advocacySessionDao().getSessionById(sessionId))
        val originalLinkedId = requireNotNull(session.linkedInteractionId)
        val originalInteraction = requireNotNull(
            database.interactionDao().getInteractionById(originalLinkedId)
        )
        database.interactionDao().deleteInteraction(originalInteraction)
        assertTrue(database.interactionDao().getAllInteractions().first().isEmpty())

        saveReflection(
            session = session,
            input = reflectionInput(
                outcomeSummary = "Recreated after the linked entry was removed",
                exportToInteractionLog = false
            )
        )

        session = requireNotNull(database.advocacySessionDao().getSessionById(sessionId))
        val recreatedLinkedId = requireNotNull(session.linkedInteractionId)
        val interactions = database.interactionDao().getAllInteractions().first()
        assertEquals(1, interactions.size)
        assertEquals(recreatedLinkedId, interactions.single().id)
        assertNotEquals(originalLinkedId, recreatedLinkedId)
        assertTrue(interactions.single().notes.contains("Recreated after the linked entry was removed"))
    }

    private fun newSession(): AdvocacySessionEntity = AdvocacySessionEntity(
        scriptId = 4,
        scriptTitle = "Understanding a denial",
        scriptCategory = "Insurance",
        selectedTone = "DIRECT",
        scriptTextSnapshot = "Please explain the denial.",
        personName = "Jordan",
        organization = "Health Plan",
        desiredOutcome = "Written explanation",
        smallGoal = "Get the reference number",
        preparationNote = "Keep the denial letter nearby",
        createdAt = 1_000L,
        updatedAt = 1_000L
    )

    private fun reflectionInput(
        outcomeSummary: String,
        exportToInteractionLog: Boolean,
        needsFollowUp: Boolean = true,
        followUpDate: Long? = 5_000L
    ): AdvocacyReflectionInput = AdvocacyReflectionInput(
        conversationHappened = "Yes",
        outcomeSummary = outcomeSummary,
        emotionalReflection = "Tired but informed",
        goalResult = "Partly",
        needsFollowUp = needsFollowUp,
        followUpDate = followUpDate,
        reflectionNote = "Private reflection",
        reflectionComplete = false,
        exportToInteractionLog = exportToInteractionLog
    )

    private fun saveReflection(
        session: AdvocacySessionEntity,
        input: AdvocacyReflectionInput
    ) {
        val saved = CountDownLatch(1)
        viewModel.saveReflection(session, input) {
            saved.countDown()
        }
        assertTrue("Reflection save timed out", saved.await(10, TimeUnit.SECONDS))
    }
}
