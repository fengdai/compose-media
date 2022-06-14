package com.github.fengdai.compose.media

import android.view.SurfaceView
import android.view.TextureView
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertIs
import kotlin.test.assertNull

class MediaSurfaceTypeTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val player = FakePlayer()

    @Test
    fun none() {
        composeTestRule.setContent {
            Media(
                state = rememberMediaState(player = player),
                surfaceType = SurfaceType.None
            )
        }
        assertNull(player.surface)
    }

    @Test
    fun surfaceView() {
        composeTestRule.setContent {
            Media(
                state = rememberMediaState(player = player),
                surfaceType = SurfaceType.SurfaceView
            )
        }
        assertIs<SurfaceView>(player.surface)
    }

    @Test
    fun textureView() {
        composeTestRule.setContent {
            Media(
                state = rememberMediaState(player = player),
                surfaceType = SurfaceType.TextureView
            )
        }
        assertIs<TextureView>(player.surface)
    }

    @Test
    fun surfaceTypeChanging() {
        var surfaceType by mutableStateOf(SurfaceType.None)
        composeTestRule.setContent {
            Media(
                state = rememberMediaState(player = player),
                surfaceType = surfaceType
            )
        }
        assertNull(player.surface)

        surfaceType = SurfaceType.SurfaceView
        composeTestRule.waitForIdle()
        assertIs<SurfaceView>(player.surface)

        surfaceType = SurfaceType.TextureView
        composeTestRule.waitForIdle()
        assertIs<TextureView>(player.surface)

        surfaceType = SurfaceType.None
        composeTestRule.waitForIdle()
        assertNull(player.surface)
    }

    @Test
    fun playerChanging() {
        var player by mutableStateOf(FakePlayer())
        composeTestRule.setContent {
            Media(
                state = rememberMediaState(player = player),
                surfaceType = SurfaceType.SurfaceView
            )
        }
        assertIs<SurfaceView>(player.surface)

        val previousPlayer = player
        player = FakePlayer()
        composeTestRule.waitForIdle()
        assertNull(previousPlayer.surface)
        assertIs<SurfaceView>(player.surface)
    }
}
