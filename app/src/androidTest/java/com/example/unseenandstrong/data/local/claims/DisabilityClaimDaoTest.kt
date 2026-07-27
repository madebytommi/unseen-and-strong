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
}
