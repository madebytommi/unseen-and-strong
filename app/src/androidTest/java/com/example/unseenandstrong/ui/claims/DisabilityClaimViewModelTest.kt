package com.example.unseenandstrong.ui.claims

import android.app.Application
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.unseenandstrong.data.local.UnseenDatabase
import com.example.unseenandstrong.data.local.accommodation.AccommodationRequestEntity
import com.example.unseenandstrong.data.local.claims.DisabilityClaimEntity
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
class DisabilityClaimViewModelTest {
    private lateinit var db: UnseenDatabase
    private lateinit var viewModel: DisabilityClaimViewModel

    @Before
    fun setup() {
        val application = ApplicationProvider.getApplicationContext<Application>()
        db = Room.inMemoryDatabaseBuilder(application, UnseenDatabase::class.java).build()
        viewModel = DisabilityClaimViewModel(application, db.interactionDao(), db.vaultDocumentDao(), db)
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun requestLogIsNotCreatedWhenOptionIsOff() = runBlocking {
        val claim = DisabilityClaimEntity(claimType = "STD", status = "Preparing")
        viewModel.saveClaim(claim, enableRequestLogIntegration = false)

        val claims = viewModel.claims.first()
        assertEquals(1, claims.size)
        assertNull(claims.first().linkedRequestId)

        val requests = db.accommodationRequestDao().getAllRequests().first()
        assertTrue(requests.isEmpty())
    }

    @Test
    fun requestLogIsCreatedOnceWhenExplicitlyEnabled() = runBlocking {
        val claim = DisabilityClaimEntity(claimType = "STD", status = "Preparing")
        viewModel.saveClaim(claim, enableRequestLogIntegration = true)

        val claims = viewModel.claims.first()
        assertEquals(1, claims.size)
        val linkedId = claims.first().linkedRequestId
        assertNotNull(linkedId)

        val requests = db.accommodationRequestDao().getAllRequests().first()
        assertEquals(1, requests.size)
        assertEquals(linkedId?.toInt(), requests.first().id)
        assertEquals("STD", requests.first().requestType)
    }

    @Test
    fun repeatedClaimSavesUpdateTheSameRequest() = runBlocking {
        val claim = DisabilityClaimEntity(claimType = "STD", status = "Preparing")
        viewModel.saveClaim(claim, enableRequestLogIntegration = true)

        val savedClaim = viewModel.claims.first().first()
        val linkedId = savedClaim.linkedRequestId
        assertNotNull(linkedId)

        val updatedClaim = savedClaim.copy(status = "Approved")
        viewModel.saveClaim(updatedClaim, enableRequestLogIntegration = true)

        val claimsAfter = viewModel.claims.first()
        assertEquals(1, claimsAfter.size)
        assertEquals(linkedId, claimsAfter.first().linkedRequestId)

        val requests = db.accommodationRequestDao().getAllRequests().first()
        assertEquals(1, requests.size)
        assertEquals("Approved", requests.first().status)
    }

    @Test
    fun missingLinkedRequestIsNotSilentlyRecreated() = runBlocking {
        val claim = DisabilityClaimEntity(claimType = "STD", status = "Preparing", linkedRequestId = 999L)
        viewModel.saveClaim(claim, enableRequestLogIntegration = true)

        val claimsAfter = viewModel.claims.first()
        assertEquals(1, claimsAfter.size)
        // Stale link is cleared, linkedRequestId is now null
        assertNull(claimsAfter.first().linkedRequestId)

        // Request log remains empty; missing request was NOT silently recreated
        val requests = db.accommodationRequestDao().getAllRequests().first()
        assertTrue(requests.isEmpty())
    }

    @Test
    fun replacementRequiresExplicitSelection() = runBlocking {
        // Step 1: Claim has a missing/stale linkedRequestId
        val claimWithStaleId = DisabilityClaimEntity(claimType = "STD", status = "Preparing", linkedRequestId = 999L)
        viewModel.saveClaim(claimWithStaleId, enableRequestLogIntegration = true)

        var claims = viewModel.claims.first()
        val clearedClaim = claims.first()
        assertNull(clearedClaim.linkedRequestId)

        // Step 2: Save again with integration off -> stays unlinked, no record created
        viewModel.saveClaim(clearedClaim, enableRequestLogIntegration = false)
        claims = viewModel.claims.first()
        assertNull(claims.first().linkedRequestId)
        assertTrue(db.accommodationRequestDao().getAllRequests().first().isEmpty())

        // Step 3: Explicitly enable integration on the unlinked claim -> creates request
        viewModel.saveClaim(clearedClaim, enableRequestLogIntegration = true)
        claims = viewModel.claims.first()
        assertNotNull(claims.first().linkedRequestId)
        assertEquals(1, db.accommodationRequestDao().getAllRequests().first().size)
    }

    @Test
    fun requestLogChangesDoNotModifyTheClaim() = runBlocking {
        val claim = DisabilityClaimEntity(claimType = "STD", status = "Preparing")
        viewModel.saveClaim(claim, enableRequestLogIntegration = false)

        val initialClaim = viewModel.claims.first().first()

        // Insert / modify request directly in accommodation request DAO
        db.accommodationRequestDao().insertRequest(
            AccommodationRequestEntity(requestType = "Ergonomic Desk", status = "Approved", notes = "Test", submissionDate = 0)
        )

        val currentClaim = viewModel.claims.first().first()
        assertEquals(initialClaim, currentClaim)
    }

    @Test
    fun unrelatedRequestLogEntriesRemainUnchanged() = runBlocking {
        val req1Id = db.accommodationRequestDao().insertRequest(
            AccommodationRequestEntity(requestType = "Ergonomic Desk", status = "Approved", notes = "Existing", submissionDate = 0)
        )

        val claim = DisabilityClaimEntity(claimType = "LTD", status = "Submitted")
        viewModel.saveClaim(claim, enableRequestLogIntegration = true)

        val requests = db.accommodationRequestDao().getAllRequests().first()
        assertEquals(2, requests.size)

        val req1 = db.accommodationRequestDao().getRequest(req1Id.toInt())
        assertNotNull(req1)
        assertEquals("Ergonomic Desk", req1?.requestType)
        assertEquals("Approved", req1?.status)
    }
}
