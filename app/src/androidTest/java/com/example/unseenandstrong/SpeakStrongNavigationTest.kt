package com.example.unseenandstrong

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.hasAnyDescendant
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToNode
import org.junit.Rule
import org.junit.Test

class SpeakStrongNavigationTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun allSpeakStrongChildDestinationsKeepSpeakStrongSelected() {
        composeRule.onNodeWithText("Speak Strong").performScrollTo().performClick()
        composeRule.waitForIdle()
        assertSpeakStrongSelected()

        listOf(
            "Draft ADA Request",
            "Advocacy Resources",
            "Boundary Builder",
            "Request Log",
            "Disability Benefits Tracker",
            "Saved Advocacy Plans"
        ).forEach { destination ->
            openHubDestination(destination)
            assertSpeakStrongSelected()
            composeRule.onNodeWithText("Back to Speak Strong").performScrollTo().performClick()
            composeRule.waitForIdle()
        }

        scrollHubTo("Practice this script")
        composeRule.onAllNodesWithText("Practice this script")[0].performClick()
        waitForText("Back to Speak Strong")
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

        composeRule.onNodeWithText("Back to saved plans").performScrollTo().performClick()
        waitForText("Saved Advocacy Plans")
        assertSpeakStrongSelected()
    }

    private fun openHubDestination(label: String) {
        scrollHubTo(label)
        composeRule.onNodeWithText(label).performClick()
        composeRule.waitForIdle()
    }

    private fun scrollHubTo(text: String) {
        composeRule.onNode(
            hasScrollAction() and hasAnyDescendant(
                hasText("Choose the support that fits the conversation in front of you.")
            )
        ).performScrollToNode(hasText(text))
    }

    private fun scrollCurrentScreenTo(anchorText: String, targetText: String) {
        composeRule.onNode(
            hasScrollAction() and hasAnyDescendant(hasText(anchorText))
        ).performScrollToNode(hasText(targetText))
    }

    private fun assertSpeakStrongSelected() {
        composeRule.onNode(
            androidx.compose.ui.test.isSelected() and androidx.compose.ui.test.hasText("Speak Strong")
        ).assertExists()
    }

    private fun waitForText(text: String) {
        composeRule.waitUntil(timeoutMillis = 10_000) {
            composeRule.onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty()
        }
    }
}
