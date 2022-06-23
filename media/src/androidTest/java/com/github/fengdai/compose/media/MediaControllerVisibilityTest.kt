package com.github.fengdai.compose.media

import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.testutil.FakeTimeline
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MediaControllerVisibilityTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun toggle() {
        val player = FakePlayer()
        val state = MediaState(player)
        composeTestRule.setContent {
            Media(
                modifier = Modifier.testTag(TestTag_Media),
                state = state,
                controller = {  /* Empty */ }
            )
        }
        composeTestRule.onNodeWithTag(TestTag_Media)
            .performClick()
        composeTestRule.waitForIdle()
        assertTrue(state.isControllerShowing)

        composeTestRule.onNodeWithTag(TestTag_Media)
            .performClick()
        composeTestRule.waitForIdle()
        assertFalse(state.isControllerShowing)
    }

    @Test
    fun toggleByStateChanging() {
        val state = MediaState(null)
        state.isControllerShowing = true
        assertTrue(state.isControllerShowing)

        state.isControllerShowing = false
        assertFalse(state.isControllerShowing)
    }

    @Test
    fun controllerIsNull() {
        val player = FakePlayer()
        val state = MediaState(player)
        composeTestRule.setContent {
            Media(
                controller = null,
                modifier = Modifier.testTag(TestTag_Media),
                state = state,
            )
        }
        composeTestRule.onNodeWithTag(TestTag_Media)
            .performClick()
        composeTestRule.waitForIdle()
        assertFalse(state.isControllerShowing)
    }

    @Test
    fun hideWhenPlayerChangedToNull() {
        val player = FakePlayer()
        val state = MediaState(player)
        composeTestRule.setContent {
            Media(
                modifier = Modifier.testTag(TestTag_Media),
                state = state,
                controller = {  /* Empty */ }
            )
        }
        composeTestRule.onNodeWithTag(TestTag_Media)
            .performClick()
        composeTestRule.waitForIdle()
        assertTrue(state.isControllerShowing)

        state.player = null
        composeTestRule.waitForIdle()
        assertFalse(state.isControllerShowing)
    }

    @Test
    fun donNotHideOnTouch() {
        val player = FakePlayer()
        val state = MediaState(player)
        composeTestRule.setContent {
            Media(
                controllerHideOnTouch = false,
                modifier = Modifier.testTag(TestTag_Media),
                state = state,
                controller = {  /* Empty */ }
            )
        }
        composeTestRule.onNodeWithTag(TestTag_Media)
            .performClick()
        composeTestRule.waitForIdle()
        assertTrue(state.isControllerShowing)

        composeTestRule.onNodeWithTag(TestTag_Media)
            .performClick()
        composeTestRule.waitForIdle()
        assertTrue(state.isControllerShowing)
    }

    @Test
    fun autoShow() {
        val player = FakePlayer().apply {
            playWhenReady_ = true
            playbackState_ = Player.STATE_READY
            timeline_ = FakeTimeline()
        }
        val state = MediaState(player)
        composeTestRule.setContent {
            Media(
                modifier = Modifier.testTag(TestTag_Media),
                state = state,
                controller = {  /* Empty */ }
            )
        }
        composeTestRule.waitForIdle()
        assertFalse(state.isControllerShowing)

        player.playbackState_ = Player.STATE_ENDED
        composeTestRule.waitForIdle()
        assertTrue(state.isControllerShowing)
    }
}

private const val TestTag_Media = "Media"
