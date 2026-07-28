package com.example.unseenandstrong.data.local.claims

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.unseenandstrong.data.local.UnseenDatabase
import com.example.unseenandstrong.data.local.interaction.InteractionEntity
import com.example.unseenandstrong.data.local.vault.VaultDocumentEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DisabilityClaimDaoTest {
    private lateinit var db: UnseenDatabase
    private lateinit var claimDao: DisabilityClaimDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, UnseenDatabase::class.java).build()
        claimDao = db.disabilityClaimDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertAndGetClaim() = runBlocking {
        val claim = DisabilityClaimEntity(claimType = "STD", status = "Preparing")
        val id = claimDao.insertClaim(claim)
        
        val loaded = claimDao.getClaim(id)
        assertNotNull(loaded)
        assertEquals("STD", loaded?.claimType)
    }

    @Test
    fun deleteClaim_removesTasksAndLinks() = runBlocking {
        // Insert claim
        val claimId = claimDao.insertClaim(DisabilityClaimEntity(claimType = "LTD"))
        val loadedClaim = claimDao.getClaim(claimId)!!
        
        // Insert task
        claimDao.insertTask(DisabilityClaimTaskEntity(claimId = claimId, category = "Form", title = "Task 1"))
        
        // Insert interaction & link
        val interactionId = db.interactionDao().insertInteraction(
            InteractionEntity(timestamp = 0, category = "Call", personName = "John", organization = "Org", notes = "Notes")
        )
        claimDao.linkInteraction(ClaimInteractionCrossRef(claimId, interactionId))
        
        // Insert document & link
        db.vaultDocumentDao().insertDocument(
            VaultDocumentEntity(title = "Doc", category = "Medical", fileUri = "uri", dateAdded = 0)
        )
        // Note: vault documents id starts at 1 usually but we need the actual id
        val docId = db.vaultDocumentDao().getAllDocuments().first().first().id
        claimDao.linkDocument(ClaimDocumentCrossRef(claimId, docId))

        // Verify inserted
        assertTrue(claimDao.observeTasksForClaim(claimId).first().size == 1)
        assertTrue(claimDao.observeLinkedInteractions(claimId).first().size == 1)
        assertTrue(claimDao.observeLinkedDocuments(claimId).first().size == 1)
        
        // Delete claim
        db.runInTransaction {
            runBlocking {
                claimDao.clearInteractionLinksForClaim(claimId)
                claimDao.clearDocumentLinksForClaim(claimId)
                claimDao.deleteClaim(loadedClaim)
            }
        }
        
        // Verify claim is gone
        assertNull(claimDao.getClaim(claimId))
        
        // Verify tasks and links are gone
        assertTrue(claimDao.observeTasksForClaim(claimId).first().isEmpty())
        assertTrue(claimDao.observeLinkedInteractions(claimId).first().isEmpty())
        assertTrue(claimDao.observeLinkedDocuments(claimId).first().isEmpty())
        
        // Verify original interaction and document still exist
        val allInteractions = db.interactionDao().getAllInteractions().first()
        assertTrue(allInteractions.size == 1)
        
        val allDocs = db.vaultDocumentDao().getAllDocuments().first()
        assertTrue(allDocs.size == 1)
    }

    @Test
    fun updatingClaimPreservesTasksAndRelationshipLinks() = runBlocking {
        // 1. Insert a claim
        val originalClaim = DisabilityClaimEntity(claimType = "STD", status = "Preparing", notes = "Original notes")
        val claimId = claimDao.insertClaim(originalClaim)
        val insertedClaim = claimDao.getClaim(claimId)!!

        // 2. Add a task belonging to the claim
        claimDao.insertTask(DisabilityClaimTaskEntity(claimId = claimId, category = "Form", title = "Task 1"))

        // 3. Link an Interaction Log record
        val interactionId = db.interactionDao().insertInteraction(
            InteractionEntity(timestamp = 0, category = "Call", personName = "John", organization = "Org", notes = "Notes")
        )
        claimDao.linkInteraction(ClaimInteractionCrossRef(claimId, interactionId))

        // 4. Link a Vault document
        db.vaultDocumentDao().insertDocument(
            VaultDocumentEntity(title = "Doc", category = "Medical", fileUri = "uri", dateAdded = 0)
        )
        val docId = db.vaultDocumentDao().getAllDocuments().first().first().id
        claimDao.linkDocument(ClaimDocumentCrossRef(claimId, docId))

        // 5. Update claim using updateClaim (@Update)
        val updatedClaim = insertedClaim.copy(status = "Approved", notes = "Updated notes", updatedAt = System.currentTimeMillis())
        val updateCount = claimDao.updateClaim(updatedClaim)
        assertEquals(1, updateCount)

        // 6. Verifications:
        // - Claim ID is unchanged
        val reloadedClaim = claimDao.getClaim(claimId)
        assertNotNull(reloadedClaim)
        assertEquals(claimId, reloadedClaim?.id)

        // - Only one claim exists
        val allClaims = claimDao.observeAllClaims().first()
        assertEquals(1, allClaims.size)

        // - Edited fields persisted
        assertEquals("Approved", reloadedClaim?.status)
        assertEquals("Updated notes", reloadedClaim?.notes)

        // - Task still exists
        val tasks = claimDao.observeTasksForClaim(claimId).first()
        assertEquals(1, tasks.size)

        // - Interaction link still exists
        val linkedInteractions = claimDao.observeLinkedInteractions(claimId).first()
        assertEquals(1, linkedInteractions.size)

        // - Document link still exists
        val linkedDocs = claimDao.observeLinkedDocuments(claimId).first()
        assertEquals(1, linkedDocs.size)

        // - Original linked records still exist
        val allInteractions = db.interactionDao().getAllInteractions().first()
        assertEquals(1, allInteractions.size)
        val allDocs = db.vaultDocumentDao().getAllDocuments().first()
        assertEquals(1, allDocs.size)
    }
}
