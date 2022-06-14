package com.github.fengdai.compose.media

import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.google.android.exoplayer2.Player
import org.junit.Rule
import org.junit.Test

class MediaShowBufferingTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val bufferingPlayer = object : FakePlayer() {
        override fun getPlaybackState(): Int = Player.STATE_BUFFERING
    }
    private val idlePlayer = object : FakePlayer() {
        override fun getPlaybackState(): Int = Player.STATE_IDLE
    }

    @Test
    fun never() {
        composeTestRule.setContent {
            Media(
                state = rememberMediaState(player = bufferingPlayer),
                showBuffering = ShowBuffering.Never,
                buffering = { BasicText("Buffering") }
            )
        }
        composeTestRule
            .onNodeWithText("Buffering")
            .assertDoesNotExist()
    }

    @Test
    fun whenPlaying() {
        composeTestRule.setContent {
            Media(
                state = rememberMediaState(player = bufferingPlayer),
                showBuffering = ShowBuffering.WhenPlaying,
                buffering = { BasicText("Buffering") }
            )
        }
        composeTestRule
            .onNodeWithText("Buffering")
            .assertDoesNotExist()

        bufferingPlayer.playWhenReady = true
        composeTestRule
            .onNodeWithText("Buffering")
            .assertIsDisplayed()
    }

    @Test
    fun always() {
        composeTestRule.setContent {
            Media(
                state = rememberMediaState(player = bufferingPlayer),
                showBuffering = ShowBuffering.Always,
                buffering = { BasicText("Buffering") }
            )
        }
        composeTestRule
            .onNodeWithText("Buffering")
            .assertIsDisplayed()
    }

    @Test
    fun playbackStateChanging() {
        val player = FakePlayer()
        composeTestRule.setContent {
            Media(
                state = rememberMediaState(player = player),
                showBuffering = ShowBuffering.Always,
                buffering = { BasicText("Buffering") }
            )
        }
        composeTestRule
            .onNodeWithText("Buffering")
            .assertDoesNotExist()

        player.playbackState_ = Player.STATE_BUFFERING
        composeTestRule
            .onNodeWithText("Buffering")
            .assertIsDisplayed()
    }

    @Test
    fun playerChanging() {
        var player by mutableStateOf<Player>(bufferingPlayer)
        composeTestRule.setContent {
            Media(
                state = rememberMediaState(player = player),
                showBuffering = ShowBuffering.Always,
                buffering = { BasicText("Buffering") }
            )
        }
        composeTestRule
            .onNodeWithText("Buffering")
            .assertIsDisplayed()

        player = idlePlayer
        composeTestRule
            .onNodeWithText("Buffering")
            .assertDoesNotExist()
    }

    @Test
    fun showBufferingChanging() {
        var showBuffering by mutableStateOf(ShowBuffering.Never)
        composeTestRule.setContent {
            Media(
                state = rememberMediaState(player = bufferingPlayer),
                showBuffering = showBuffering,
                buffering = { BasicText("Buffering") }
            )
        }
        composeTestRule
            .onNodeWithText("Buffering")
            .assertDoesNotExist()

        showBuffering = ShowBuffering.Always
        composeTestRule
            .onNodeWithText("Buffering")
            .assertIsDisplayed()

        showBuffering = ShowBuffering.WhenPlaying
        composeTestRule
            .onNodeWithText("Buffering")
            .assertDoesNotExist()
    }

    @Test(expected = IllegalArgumentException::class)
    fun bufferingSlotIsNull() {
        composeTestRule.setContent {
            Media(
                state = rememberMediaState(player = bufferingPlayer),
                showBuffering = ShowBuffering.Always,
                buffering = null
            )
        }
        assert(false) { "Should throw when showBuffering isn't 'Never' and buffering is null" }
    }
}
