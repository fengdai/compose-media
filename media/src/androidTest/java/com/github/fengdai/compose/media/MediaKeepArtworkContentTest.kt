package com.github.fengdai.compose.media

import androidx.activity.ComponentActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.Tracks
import org.junit.Rule
import org.junit.Test

class MediaKeepArtworkContentTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun donNotKeep() {
        val player = FakePlayer()
        player.tracks = TestTracks_Audio
        player.mediaMetadata_ = MediaMetadata
            .Builder()
            .setArtworkData(
                composeTestRule.createTestArtworkData(),
                MediaMetadata.PICTURE_TYPE_OTHER
            )
            .build()
        composeTestRule.setContent {
            Media(
                state = rememberMediaState(player = player),
                keepContentOnPlayerReset = false,
                surfaceType = SurfaceType.None
            )
        }
        composeTestRule.onNodeWithTag(TestTag_Artwork, useUnmergedTree = true)
            .assertIsDisplayed()

        player.tracks = Tracks.EMPTY
        composeTestRule.onNodeWithTag(TestTag_Artwork, useUnmergedTree = true)
            .assertDoesNotExist()
    }

    @Test
    fun keep() {
        val player = FakePlayer()
        player.tracks = TestTracks_Audio
        player.mediaMetadata_ = MediaMetadata
            .Builder()
            .setArtworkData(
                composeTestRule.createTestArtworkData(),
                MediaMetadata.PICTURE_TYPE_OTHER
            )
            .build()
        composeTestRule.setContent {
            Media(
                state = rememberMediaState(player = player),
                keepContentOnPlayerReset = true,
                surfaceType = SurfaceType.None
            )
        }
        composeTestRule.onNodeWithTag(TestTag_Artwork, useUnmergedTree = true)
            .assertIsDisplayed()

        player.tracks = Tracks.EMPTY
        composeTestRule.onNodeWithTag(TestTag_Artwork, useUnmergedTree = true)
            .assertIsDisplayed()
    }

    @Test
    fun donNotKeepOnUseArtworkChangedToFalse() {
        var useArtwork by mutableStateOf(true)
        val player = FakePlayer()
        player.tracks = TestTracks_Audio
        player.mediaMetadata_ = MediaMetadata
            .Builder()
            .setArtworkData(
                composeTestRule.createTestArtworkData(),
                MediaMetadata.PICTURE_TYPE_OTHER
            )
            .build()
        composeTestRule.setContent {
            Media(
                state = rememberMediaState(player = player),
                useArtwork = useArtwork,
                keepContentOnPlayerReset = true,
                surfaceType = SurfaceType.None
            )
        }
        composeTestRule.onNodeWithTag(TestTag_Artwork, useUnmergedTree = true)
            .assertIsDisplayed()

        useArtwork = false
        composeTestRule.onNodeWithTag(TestTag_Artwork, useUnmergedTree = true)
            .assertDoesNotExist()
    }

    @Test
    fun donNotKeepPlayerChanging() {
        val audioPlayer = FakePlayer()
        audioPlayer.tracks = TestTracks_Audio
        audioPlayer.mediaMetadata_ = MediaMetadata
            .Builder()
            .setArtworkData(
                composeTestRule.createTestArtworkData(),
                MediaMetadata.PICTURE_TYPE_OTHER
            )
            .build()

        var player: Player? by mutableStateOf(audioPlayer)
        composeTestRule.setContent {
            Media(
                state = rememberMediaState(player = player),
                keepContentOnPlayerReset = false,
                surfaceType = SurfaceType.None
            )
        }
        composeTestRule.onNodeWithTag(TestTag_Artwork, useUnmergedTree = true)
            .assertIsDisplayed()

        player = null
        composeTestRule.onNodeWithTag(TestTag_Artwork, useUnmergedTree = true)
            .assertDoesNotExist()
    }

    @Test
    fun keepPlayerChanging() {
        val audioPlayer = FakePlayer()
        audioPlayer.tracks = TestTracks_Audio
        audioPlayer.mediaMetadata_ = MediaMetadata
            .Builder()
            .setArtworkData(
                composeTestRule.createTestArtworkData(),
                MediaMetadata.PICTURE_TYPE_OTHER
            )
            .build()

        var player: Player? by mutableStateOf(audioPlayer)
        composeTestRule.setContent {
            Media(
                state = rememberMediaState(player = player),
                keepContentOnPlayerReset = true,
                surfaceType = SurfaceType.None
            )
        }
        composeTestRule.onNodeWithTag(TestTag_Artwork, useUnmergedTree = true)
            .assertIsDisplayed()

        player = null
        composeTestRule.onNodeWithTag(TestTag_Artwork, useUnmergedTree = true)
            .assertIsDisplayed()
    }
}
