package com.example.unseenandstrong.ui.claims

import org.junit.Assert.assertEquals
import org.junit.Test

class ClaimRequestLogSyncPolicyTest {

    @Test
    fun requestLogIsNotCreatedWhenOptionIsOff() {
        val action = ClaimRequestLogSyncPolicy.determineAction(
            hasLinkedId = false,
            linkedRequestExists = false,
            enableIntegration = false
        )
        assertEquals(RequestLogSyncAction.NONE, action)
    }

    @Test
    fun requestLogIsCreatedOnceWhenExplicitlyEnabled() {
        val action = ClaimRequestLogSyncPolicy.determineAction(
            hasLinkedId = false,
            linkedRequestExists = false,
            enableIntegration = true
        )
        assertEquals(RequestLogSyncAction.CREATE, action)
    }

    @Test
    fun repeatedClaimSavesUpdateTheSameRequest() {
        val action = ClaimRequestLogSyncPolicy.determineAction(
            hasLinkedId = true,
            linkedRequestExists = true,
            enableIntegration = true
        )
        assertEquals(RequestLogSyncAction.UPDATE, action)
    }

    @Test
    fun missingLinkedRequestIsNotSilentlyRecreated() {
        val actionWithIntegrationOn = ClaimRequestLogSyncPolicy.determineAction(
            hasLinkedId = true,
            linkedRequestExists = false,
            enableIntegration = true
        )
        assertEquals(RequestLogSyncAction.CLEAR_STALE_LINK, actionWithIntegrationOn)

        val actionWithIntegrationOff = ClaimRequestLogSyncPolicy.determineAction(
            hasLinkedId = true,
            linkedRequestExists = false,
            enableIntegration = false
        )
        assertEquals(RequestLogSyncAction.CLEAR_STALE_LINK, actionWithIntegrationOff)
    }

    @Test
    fun replacementRequiresExplicitSelection() {
        // Step 1: Link is stale -> policy clears stale link
        val step1Action = ClaimRequestLogSyncPolicy.determineAction(
            hasLinkedId = true,
            linkedRequestExists = false,
            enableIntegration = true
        )
        assertEquals(RequestLogSyncAction.CLEAR_STALE_LINK, step1Action)

        // Step 2: Once stale link is cleared, claim now has no linked ID (hasLinkedId = false).
        // Saving without explicitly enabling integration does NOT create replacement.
        val step2ActionOptionOff = ClaimRequestLogSyncPolicy.determineAction(
            hasLinkedId = false,
            linkedRequestExists = false,
            enableIntegration = false
        )
        assertEquals(RequestLogSyncAction.NONE, step2ActionOptionOff)

        // Step 3: Only when user explicitly enables integration on a claim with no linked ID does it create.
        val step3ActionOptionOn = ClaimRequestLogSyncPolicy.determineAction(
            hasLinkedId = false,
            linkedRequestExists = false,
            enableIntegration = true
        )
        assertEquals(RequestLogSyncAction.CREATE, step3ActionOptionOn)
    }
}
