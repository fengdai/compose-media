package com.github.fengdai.compose.media

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import androidx.media3.common.VideoSize
import org.junit.Rule
import org.junit.Test

class MediaResizeModeTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun tallVideoSize() {
        val tallVideoSizePlayer = object : FakePlayer() {
            override fun getVideoSize(): VideoSize {
                return VideoSize(100, 125)
            }
        }
        var resizeMode by mutableStateOf(ResizeMode.Fit)
        composeTestRule.setContent {
            Media(
                state = rememberMediaState(player = tallVideoSizePlayer),
                resizeMode = resizeMode,
                surfaceType = SurfaceType.SurfaceView,
                modifier = Modifier.size(100.dp)
            )
        }
        composeTestRule.onNodeWithTag(TestTag_VideoSurface)
            .assertWidthIsEqualTo(80.dp)
            .assertHeightIsEqualTo(100.dp)

        resizeMode = ResizeMode.FixedHeight
        composeTestRule.onNodeWithTag(TestTag_VideoSurface)
            .assertWidthIsEqualTo(80.dp)
            .assertHeightIsEqualTo(100.dp)

        resizeMode = ResizeMode.FixedWidth
        composeTestRule.onNodeWithTag(TestTag_VideoSurface)
            .assertWidthIsEqualTo(100.dp)
            .assertHeightIsEqualTo(125.dp)

        resizeMode = ResizeMode.Fill
        composeTestRule.onNodeWithTag(TestTag_VideoSurface)
            .assertWidthIsEqualTo(100.dp)
            .assertHeightIsEqualTo(100.dp)

        resizeMode = ResizeMode.Zoom
        composeTestRule.onNodeWithTag(TestTag_VideoSurface)
            .assertWidthIsEqualTo(100.dp)
            .assertHeightIsEqualTo(125.dp)
    }

    @Test
    fun shortVideoSize() {
        val shortVideoSizePlayer = object : FakePlayer() {
            override fun getVideoSize(): VideoSize {
                return VideoSize(100, 80)
            }
        }
        var resizeMode by mutableStateOf(ResizeMode.Fit)
        composeTestRule.setContent {
            Media(
                state = rememberMediaState(player = shortVideoSizePlayer),
                resizeMode = resizeMode,
                surfaceType = SurfaceType.SurfaceView,
                modifier = Modifier.size(100.dp)
            )
        }
        composeTestRule.onNodeWithTag(TestTag_VideoSurface)
            .assertWidthIsEqualTo(100.dp)
            .assertHeightIsEqualTo(80.dp)

        resizeMode = ResizeMode.FixedHeight
        composeTestRule.onNodeWithTag(TestTag_VideoSurface)
            .assertWidthIsEqualTo(125.dp)
            .assertHeightIsEqualTo(100.dp)

        resizeMode = ResizeMode.FixedWidth
        composeTestRule.onNodeWithTag(TestTag_VideoSurface)
            .assertWidthIsEqualTo(100.dp)
            .assertHeightIsEqualTo(80.dp)

        resizeMode = ResizeMode.Fill
        composeTestRule.onNodeWithTag(TestTag_VideoSurface)
            .assertWidthIsEqualTo(100.dp)
            .assertHeightIsEqualTo(100.dp)

        resizeMode = ResizeMode.Zoom
        composeTestRule.onNodeWithTag(TestTag_VideoSurface)
            .assertWidthIsEqualTo(125.dp)
            .assertHeightIsEqualTo(100.dp)
    }
}
