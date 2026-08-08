package com.example.unseenandstrong.ui.claims

import androidx.compose.ui.test.hasAnyDescendant
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToNode
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.unseenandstrong.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DisabilityClaimsUITest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun testNavigationToClaimsAndBasicFlow() {
        // First navigate to Speak Strong tab
        composeTestRule.onNodeWithText("Speak Strong").performScrollTo().performClick()
        composeTestRule.waitForIdle()

        // From Hub, click STD/LTD Claims
        composeTestRule.onNode(
            hasScrollAction() and 
            hasAnyDescendant(hasText("Choose the support that fits the conversation in front of you."))
        ).performScrollToNode(hasText("STD/LTD Claims"))
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
        composeTestRule.onNodeWithText("Save Claim").performScrollTo().performClick()
        composeTestRule.waitForIdle()
        
        // Should be back to List Screen
        composeTestRule.onNodeWithText("Add New Claim").assertExists()
        
        // Click the newly created claim (LTD should be visible)
        composeTestRule.onNodeWithText("LTD").performScrollTo().performClick()
        composeTestRule.waitForIdle()
        
        // Verify we are on Detail screen
        composeTestRule.onNodeWithText("Claim Details").assertExists()
        
        // Verify Edit button exists
        composeTestRule.onNodeWithContentDescription("Edit Claim").assertExists()
        
        // Add Task
        composeTestRule.onNodeWithContentDescription("Add Task").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Cancel").performClick() // just close dialog
        
        // Link Interaction
        composeTestRule.onNodeWithContentDescription("Link Interaction").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Close").performClick()
        
        // Delete claim
        composeTestRule.onNodeWithText("Delete Claim").performScrollTo().performClick()
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
}
