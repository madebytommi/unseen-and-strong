package com.example.unseenandstrong

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.hasAnyDescendant
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.hasStateDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isToggleable
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToNode
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SpeakStrongNavigationTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

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
    fun allSpeakStrongChildDestinationsKeepSpeakStrongSelected() {
        composeRule.onNodeWithText("Speak Strong").performClick()
        composeRule.waitForIdle()
        assertSpeakStrongSelected()

        listOf(
            "Draft ADA Request",
            "Advocacy Resources",
            "Boundary Builder",
            "Request Log",
            "Disability Benefits Tracker",
            "STD/LTD Claims",
            "Saved Advocacy Plans",
            "Interaction Log",
            "Document Vault"
        ).forEach { destination ->
            openHubDestination(destination)
            assertSpeakStrongSelected()
            composeRule.onNode(
                hasText("Back to Speak Strong") or hasContentDescription("Back to Speak Strong")
            ).performClick()
            composeRule.waitForIdle()
        }

        scrollHubTo("Practice this script")
        composeRule.onAllNodesWithText("Practice this script")[0].performClick()
        waitForBackToHub()
        assertSpeakStrongSelected()

        scrollCurrentScreenTo(
            anchorText = "Back to Speak Strong",
            targetText = "Prepare for the conversation"
        )
        composeRule.onNodeWithText("Prepare for the conversation").performClick()
        waitForText("Before the conversation")
        assertSpeakStrongSelected()

        scrollCurrentScreenTo(
            anchorText = "Before the conversation",
            targetText = "Continue to after-conversation reflection"
        )
        composeRule.onNodeWithText("Continue to after-conversation reflection").performClick()
        waitForText("After the conversation")
        assertSpeakStrongSelected()

        composeRule.onNodeWithText("Back to saved plans").performClick()
        waitForText("Saved Advocacy Plans")
        assertSpeakStrongSelected()
    }

    @Test
    fun flareDayHubReducesVisibleToolsWithoutRemovingAccess() {
        composeRule.onNode(isToggleable() and hasStateDescription("Off")).performClick()
        composeRule.onNodeWithText("Speak Strong").performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithText("Saved Advocacy Plans").assertExists()
        composeRule.onNodeWithText("Show all advocacy tools").assertExists()
        composeRule.onNodeWithText("Draft ADA Request").assertDoesNotExist()
        composeRule.onNodeWithText("Interaction Log").assertDoesNotExist()
        composeRule.onNodeWithText("Document Vault").assertDoesNotExist()
        composeRule.onNode(isToggleable() and hasStateDescription("On")).assertExists()

        composeRule.onNodeWithText("Show all advocacy tools").performClick()
        composeRule.onNodeWithText("Draft ADA Request").assertExists()
        composeRule.onNodeWithText("Advocacy Resources").assertExists()
        composeRule.onNodeWithText("Request Log").assertExists()
        composeRule.onNodeWithText("STD/LTD Claims").assertExists()
        composeRule.onNodeWithText("Interaction Log").assertExists()
        composeRule.onNodeWithText("Document Vault").assertExists()

        composeRule.onNode(isToggleable() and hasStateDescription("On")).performClick()
        composeRule.waitForIdle()
    }

    private fun openHubDestination(label: String) {
        scrollHubTo(label)
        composeRule.onNodeWithText(label).performClick()
        composeRule.waitForIdle()
    }

    private fun scrollHubTo(text: String) {
        composeRule.onAllNodes(hasScrollAction())[0].performScrollToNode(hasText(text))
    }

    private fun scrollCurrentScreenTo(anchorText: String, targetText: String) {
        composeRule.onAllNodes(hasScrollAction())[0].performScrollToNode(hasText(targetText))
    }

    private fun assertSpeakStrongSelected() {
        composeRule.onNode(
            androidx.compose.ui.test.isSelected() and androidx.compose.ui.test.hasText("Speak Strong")
        ).assertExists()
    }

    private fun waitForBackToHub() {
        composeRule.waitUntil(timeoutMillis = 10_000) {
            composeRule.onAllNodes(
                hasText("Back to Speak Strong") or hasContentDescription("Back to Speak Strong")
            ).fetchSemanticsNodes().isNotEmpty()
        }
    }

    private fun waitForText(text: String) {
        composeRule.waitUntil(timeoutMillis = 10_000) {
            composeRule.onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty()
        }
    }
}
