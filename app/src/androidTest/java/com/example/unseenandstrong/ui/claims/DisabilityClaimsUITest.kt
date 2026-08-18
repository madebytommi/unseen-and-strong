package com.example.unseenandstrong.ui.claims

import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToNode
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.unseenandstrong.FlareDayPreferences
import com.example.unseenandstrong.MainActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DisabilityClaimsUITest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() {
        resetFlareDayPreference()
    }

    @After
    fun tearDown() {
        resetFlareDayPreference()
    }

    private fun resetFlareDayPreference() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        FlareDayPreferences(context).isEnabled = false
    }

    @Test
    fun testNavigationToClaimsAndBasicFlow() {
        // First navigate to Speak Strong tab
        composeTestRule.onNodeWithText("Speak Strong").performScrollTo().performClick()
        composeTestRule.waitForIdle()

        // From Hub, click STD/LTD Claims
        composeTestRule.onAllNodes(hasScrollAction())[0].performScrollToNode(hasText("STD/LTD Claims"))
        composeTestRule.onNodeWithText("STD/LTD Claims").performClick()
        composeTestRule.waitForIdle()

        // Verify we are on the list screen
        composeTestRule.onNodeWithText("Add New Claim").assertExists()
        
        // Verify Speak Strong is still the selected bottom tab
        // Checking if "Speak Strong" tab exists is generally true, but we could verify its state if we had a tag.
        
        // Click Add Claim
        composeTestRule.onNodeWithText("Add New Claim").performClick()
        composeTestRule.waitForIdle()
        
        // Verify we are on Form Screen
        composeTestRule.onNodeWithText("New Claim").assertExists()
        
        // Select LTD
        composeTestRule.onNodeWithText("LTD").performClick()
        
        // Save
        composeTestRule.onAllNodes(hasScrollAction())[0].performScrollToNode(hasText("Save Claim"))
        composeTestRule.onNodeWithText("Save Claim").performClick()
        composeTestRule.waitForIdle()
        
        // Should be back to List Screen
        composeTestRule.onNodeWithText("Add New Claim").assertExists()
        
        // Click the newly created claim (Preparing should be visible)
        composeTestRule.onAllNodes(hasScrollAction())[0].performScrollToNode(hasText("Preparing"))
        composeTestRule.onNodeWithText("Preparing").performClick()
        composeTestRule.waitForIdle()
        
        // Verify we are on Detail screen
        composeTestRule.onNodeWithText("Claim Details").assertExists()
        
        // Verify Edit button exists
        composeTestRule.onNodeWithContentDescription("Edit claim").assertExists()
        
        // Add Task
        composeTestRule.onNodeWithContentDescription("Add task").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Cancel").performClick() // just close dialog
        
        // Link Interaction
        composeTestRule.onNodeWithContentDescription("Link interaction").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Close").performClick()
        
        // Delete claim
        composeTestRule.onAllNodes(hasScrollAction())[0].performScrollToNode(hasText("Delete Claim"))
        composeTestRule.onNodeWithText("Delete Claim").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Delete").performClick()
        composeTestRule.waitForIdle()
        
        // Back to list
        composeTestRule.onNodeWithText("Add New Claim").assertExists()
        
        // Go back to hub
        composeTestRule.onNodeWithContentDescription("Back to Speak Strong").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Draft ADA Request").assertExists()
    }

    @Test
    fun editingClaimRestoresSelectedClaimAfterRecreation() {
        openClaims()

        composeTestRule.onNodeWithText("Add New Claim").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onAllNodes(hasScrollAction())[0].performScrollToNode(hasText("Save Claim"))
        composeTestRule.onNodeWithText("Save Claim").performClick()
        waitForText("Preparing")

        composeTestRule.onAllNodes(hasScrollAction())[0].performScrollToNode(hasText("Preparing"))
        composeTestRule.onNodeWithText("Preparing").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithContentDescription("Edit claim").performClick()
        waitForText("Edit Claim")

        composeTestRule.activityRule.scenario.recreate()
        waitForText("Edit Claim")
        composeTestRule.onNodeWithContentDescription("Cancel").performClick()
        waitForText("Claim Details")

        composeTestRule.onAllNodes(hasScrollAction())[0].performScrollToNode(hasText("Delete Claim"))
        composeTestRule.onNodeWithText("Delete Claim").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Delete").performClick()
        composeTestRule.waitForIdle()
    }

    private fun openClaims() {
        composeTestRule.onNodeWithText("Speak Strong").performScrollTo().performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onAllNodes(hasScrollAction())[0].performScrollToNode(hasText("STD/LTD Claims"))
        composeTestRule.onNodeWithText("STD/LTD Claims").performClick()
        composeTestRule.waitForIdle()
    }

    private fun waitForText(text: String) {
        composeTestRule.waitUntil(timeoutMillis = 10_000) {
            composeTestRule.onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty()
        }
    }
}
