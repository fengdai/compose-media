package com.github.fengdai.compose.media

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.media3.common.Player
import androidx.media3.common.Tracks
import org.junit.Rule
import org.junit.Test

class MediaKeepVideoContentTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun donNotKeep() {
        val player = FakePlayer()
        player.tracks = TestTracks_Video
        composeTestRule.setContent {
            Media(
                state = rememberMediaState(player = player),
                keepContentOnPlayerReset = false,
                surfaceType = SurfaceType.SurfaceView
            )
        }
        player.listeners.forEach { it.onRenderedFirstFrame() }
        composeTestRule.onNodeWithTag(TestTag_Shutter, useUnmergedTree = true)
            .assertDoesNotExist()

        player.tracks = Tracks.EMPTY
        composeTestRule.onNodeWithTag(TestTag_Shutter, useUnmergedTree = true)
            .assertIsDisplayed()
    }

    @Test
    fun keep() {
        val player = FakePlayer()
        player.tracks = TestTracks_Video
        composeTestRule.setContent {
            Media(
                state = rememberMediaState(player = player),
                keepContentOnPlayerReset = true,
                surfaceType = SurfaceType.SurfaceView
            )
        }
        player.listeners.forEach { it.onRenderedFirstFrame() }
        composeTestRule.onNodeWithTag(TestTag_Shutter, useUnmergedTree = true)
            .assertDoesNotExist()

        player.tracks = Tracks.EMPTY
        composeTestRule.onNodeWithTag(TestTag_Shutter, useUnmergedTree = true)
            .assertDoesNotExist()
    }

    @Test
    fun keepChanging() {
        var keepContentOnPlayerReset by mutableStateOf(false)
        val player = FakePlayer()
        player.tracks = TestTracks_Video
        composeTestRule.setContent {
            Media(
                state = rememberMediaState(player = player),
                keepContentOnPlayerReset = keepContentOnPlayerReset,
                surfaceType = SurfaceType.SurfaceView
            )
        }
        player.listeners.forEach { it.onRenderedFirstFrame() }
        composeTestRule.onNodeWithTag(TestTag_Shutter, useUnmergedTree = true)
            .assertDoesNotExist()

        player.tracks = Tracks.EMPTY
        composeTestRule.onNodeWithTag(TestTag_Shutter, useUnmergedTree = true)
            .assertIsDisplayed()

        keepContentOnPlayerReset = true
        composeTestRule.onNodeWithTag(TestTag_Shutter, useUnmergedTree = true)
            .assertDoesNotExist()

        keepContentOnPlayerReset = false
        composeTestRule.onNodeWithTag(TestTag_Shutter, useUnmergedTree = true)
            .assertIsDisplayed()
    }

    @Test
    fun playerChangingDonNotKeep() {
        val videoPlayer = FakePlayer()
        videoPlayer.tracks = TestTracks_Video

        var player by mutableStateOf<Player?>(videoPlayer)
        composeTestRule.setContent {
            Media(
                state = rememberMediaState(player = player),
                keepContentOnPlayerReset = false,
                surfaceType = SurfaceType.SurfaceView
            )
        }
        videoPlayer.listeners.forEach { it.onRenderedFirstFrame() }
        composeTestRule.onNodeWithTag(TestTag_Shutter, useUnmergedTree = true)
            .assertDoesNotExist()

        val newVideoPlayer = FakePlayer()
        newVideoPlayer.tracks = TestTracks_Video
        player = newVideoPlayer
        composeTestRule.onNodeWithTag(TestTag_Shutter, useUnmergedTree = true)
            .assertIsDisplayed()
        newVideoPlayer.listeners.forEach { it.onRenderedFirstFrame() }
        composeTestRule.onNodeWithTag(TestTag_Shutter, useUnmergedTree = true)
            .assertDoesNotExist()

        player = null
        composeTestRule.onNodeWithTag(TestTag_Shutter, useUnmergedTree = true)
            .assertIsDisplayed()
    }

    @Test
    fun playerChangingKeep() {
        val videoPlayer = FakePlayer()
        videoPlayer.tracks = TestTracks_Video

        var player by mutableStateOf<Player?>(videoPlayer)
        composeTestRule.setContent {
            Media(
                state = rememberMediaState(player = player),
                keepContentOnPlayerReset = true,
                surfaceType = SurfaceType.SurfaceView
            )
        }
        videoPlayer.listeners.forEach { it.onRenderedFirstFrame() }
        composeTestRule.onNodeWithTag(TestTag_Shutter, useUnmergedTree = true)
            .assertDoesNotExist()

        val newVideoPlayer = FakePlayer()
        newVideoPlayer.tracks = TestTracks_Video
        player = newVideoPlayer
        composeTestRule.onNodeWithTag(TestTag_Shutter, useUnmergedTree = true)
            .assertDoesNotExist()
        newVideoPlayer.listeners.forEach { it.onRenderedFirstFrame() }
        composeTestRule.onNodeWithTag(TestTag_Shutter, useUnmergedTree = true)
            .assertDoesNotExist()

        player = null
        composeTestRule.onNodeWithTag(TestTag_Shutter, useUnmergedTree = true)
            .assertDoesNotExist()
    }
}
