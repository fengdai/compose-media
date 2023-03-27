package com.github.fengdai.compose.media

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.Tracks
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class MediaArtworkTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private class AudioPlayer(
        private val artwork: ByteArray?
    ) : FakePlayer() {
        private val mediaMetadata = MediaMetadata.Builder()
            .run {
                if (artwork == null) this
                else setArtworkData(artwork, MediaMetadata.PICTURE_TYPE_OTHER)
            }
            .build()

        override fun getCurrentTracks(): Tracks = TestTracks_Audio

        override fun getMediaMetadata(): MediaMetadata = mediaMetadata
    }

    @Test
    fun useArtwork() {
        val audioPlayer = AudioPlayer(composeTestRule.createTestArtworkData())
        composeTestRule.setContent {
            Media(
                state = rememberMediaState(player = audioPlayer),
                surfaceType = SurfaceType.None,
                modifier = Modifier.size(100.dp)
            )
        }
        composeTestRule.onNodeWithTag(TestTag_Artwork, useUnmergedTree = true)
            .assertIsDisplayed()
    }

    @Test
    fun donNotUseArtwork() {
        val audioPlayer = AudioPlayer(composeTestRule.createTestArtworkData())
        composeTestRule.setContent {
            Media(
                state = rememberMediaState(player = audioPlayer),
                useArtwork = false,
                surfaceType = SurfaceType.None,
                modifier = Modifier.size(100.dp)
            )
        }
        composeTestRule.onNodeWithTag(TestTag_Artwork, useUnmergedTree = true)
            .assertDoesNotExist()
    }

    @Test
    fun artworkIsNull() {
        val audioPlayer = AudioPlayer(artwork = null)
        composeTestRule.setContent {
            Media(
                state = rememberMediaState(player = audioPlayer),
                surfaceType = SurfaceType.None,
                modifier = Modifier.size(100.dp)
            )
        }
        composeTestRule.onNodeWithTag(TestTag_Artwork, useUnmergedTree = true)
            .assertDoesNotExist()
    }

    @Test
    fun defaultArtwork() {
        val defaultArtworkPainter = ColorPainter(Color.Red)
        val audioPlayer = AudioPlayer(artwork = null)
        val state = MediaState(audioPlayer)
        composeTestRule.setContent {
            Media(
                state = state,
                defaultArtworkPainter = defaultArtworkPainter,
                surfaceType = SurfaceType.None,
                modifier = Modifier.size(100.dp)
            )
        }
        composeTestRule.onNodeWithTag(TestTag_Artwork, useUnmergedTree = true)
            .assertIsDisplayed()

        assertEquals(defaultArtworkPainter, state.usingArtworkPainter)
    }

    @Test
    fun useArtworkPlayerChanging() {
        var player by mutableStateOf<Player?>(null)
        composeTestRule.setContent {
            Media(
                state = rememberMediaState(player = player),
                surfaceType = SurfaceType.None,
                modifier = Modifier.size(100.dp)
            )
        }
        composeTestRule.onNodeWithTag(TestTag_Artwork, useUnmergedTree = true)
            .assertDoesNotExist()

        player = AudioPlayer(composeTestRule.createTestArtworkData())
        composeTestRule.onNodeWithTag(TestTag_Artwork, useUnmergedTree = true)
            .assertIsDisplayed()

        player = null
        composeTestRule.onNodeWithTag(TestTag_Artwork, useUnmergedTree = true)
            .assertDoesNotExist()
    }
}
