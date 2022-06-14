package com.github.fengdai.compose.media

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.click
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.unit.dp
import com.google.android.exoplayer2.C
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class TimeBarTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun drag() {
        var position by mutableStateOf(0L)
        composeTestRule.setContent {
            TimeBar(
                durationMs = 100,
                positionMs = position,
                bufferedPositionMs = 0,
                modifier = Modifier
                    .testTag(TestTag_TimeBar)
                    .size(100.dp, 4.dp),
                onScrubStop = { position = it },
                scrubber = { enabled, scrubbing ->
                    TimeBarScrubber(
                        enabled,
                        scrubbing,
                        enabledSize = ScrubberEnabledSize.dp,
                        draggedSize = ScrubberDraggedSize.dp
                    )
                }
            )
        }
        composeTestRule.onNodeWithTag(TestTag_TimeBar)
            .performTouchInput {
                // drag to bar center
                down(centerLeft)
                moveBy(Offset((width - ScrubberDraggedSize.dp.toPx()) / 2f, 0f))
                up()
            }
        composeTestRule.waitForIdle()
        assertEquals(50L, position)
    }

    @Test
    fun disableWhileDragging() {
        var enabled by mutableStateOf(true)
        var position by mutableStateOf(0L)
        composeTestRule.setContent {
            TimeBar(
                enabled = enabled,
                durationMs = 100,
                positionMs = position,
                bufferedPositionMs = 0,
                modifier = Modifier
                    .testTag(TestTag_TimeBar)
                    .size(100.dp, 4.dp),
                onScrubStop = { position = it },
                scrubber = { enabled, scrubbing ->
                    TimeBarScrubber(
                        enabled,
                        scrubbing,
                        enabledSize = ScrubberEnabledSize.dp,
                        draggedSize = ScrubberDraggedSize.dp
                    )
                }
            )
        }
        composeTestRule.onNodeWithTag(TestTag_TimeBar)
            .performTouchInput {
                // drag to bar center
                down(centerLeft)
                moveBy(Offset((width - ScrubberDraggedSize.dp.toPx()) / 2f, 0f))
                enabled = false
                up()
            }
        composeTestRule.waitForIdle()
        assertEquals(0L, position)
    }

    @Test
    fun durationChangeToUnsetWhileDragging() {
        var duration by mutableStateOf(100L)
        var position by mutableStateOf(0L)
        composeTestRule.setContent {
            TimeBar(
                durationMs = duration,
                positionMs = position,
                bufferedPositionMs = 0,
                modifier = Modifier
                    .testTag(TestTag_TimeBar)
                    .size(100.dp, 4.dp),
                onScrubStop = { position = it },
                scrubber = { enabled, scrubbing ->
                    TimeBarScrubber(
                        enabled,
                        scrubbing,
                        enabledSize = ScrubberEnabledSize.dp,
                        draggedSize = ScrubberDraggedSize.dp
                    )
                }
            )
        }
        composeTestRule.onNodeWithTag(TestTag_TimeBar)
            .performTouchInput {
                // drag to bar center
                down(centerLeft)
                moveBy(Offset((width - ScrubberDraggedSize.dp.toPx()) / 2f, 0f))
                duration = C.TIME_UNSET
                up()
            }
        composeTestRule.waitForIdle()
        assertEquals(0L, position)
    }

    @Test
    fun click() {
        var position by mutableStateOf(0L)
        composeTestRule.setContent {
            TimeBar(
                durationMs = 100,
                positionMs = position,
                bufferedPositionMs = 0,
                modifier = Modifier
                    .testTag(TestTag_TimeBar)
                    .size(100.dp, 4.dp),
                onScrubStop = { position = it },
                scrubber = { enabled, scrubbing ->
                    TimeBarScrubber(
                        enabled,
                        scrubbing,
                        enabledSize = ScrubberEnabledSize.dp
                    )
                }
            )
        }
        // click bar left
        composeTestRule.onNodeWithTag(TestTag_TimeBar)
            .performTouchInput {
                click(Offset(0.dp.toPx(), 0f))
            }
        composeTestRule.waitForIdle()
        assertEquals(0L, position)

        // click scrubber center
        composeTestRule.onNodeWithTag(TestTag_TimeBar)
            .performTouchInput {
                click(Offset((ScrubberEnabledSize.dp / 2).toPx(), 0f))
            }
        composeTestRule.waitForIdle()
        assertEquals(0L, position)

        // click scrubber center + 1dp
        composeTestRule.onNodeWithTag(TestTag_TimeBar)
            .performTouchInput {
                click(Offset((ScrubberEnabledSize.dp / 2 + 1.dp).toPx(), 0f))
            }
        composeTestRule.waitForIdle()
        assertEquals(1, position)

        // click 1/10 duration
        composeTestRule.onNodeWithTag(TestTag_TimeBar)
            .performTouchInput {
                click(Offset((ScrubberEnabledSize.dp / 2 + 9.dp).toPx(), 0f))
            }
        composeTestRule.waitForIdle()
        assertEquals(10, position)

        // click bar center
        composeTestRule.onNodeWithTag(TestTag_TimeBar)
            .performTouchInput {
                click(center)
            }
        composeTestRule.waitForIdle()
        assertEquals(50L, position)

        // click bar right - scrubber center
        composeTestRule.onNodeWithTag(TestTag_TimeBar)
            .performTouchInput {
                click(Offset((100 - ScrubberEnabledSize / 2).dp.toPx(), 0f))
            }
        composeTestRule.waitForIdle()
        assertEquals(100L, position)

        // click bar right
        composeTestRule.onNodeWithTag(TestTag_TimeBar)
            .performTouchInput {
                click(Offset(100.dp.toPx(), 0f))
            }
        composeTestRule.waitForIdle()
        assertEquals(100L, position)
    }

    @Test
    fun clickScrubberCenterAsAnchor() {
        var position by mutableStateOf(0L)
        composeTestRule.setContent {
            TimeBar(
                scrubberCenterAsAnchor = true,
                durationMs = 100,
                positionMs = position,
                bufferedPositionMs = 0,
                modifier = Modifier
                    .testTag(TestTag_TimeBar)
                    .size(100.dp, 4.dp),
                onScrubStop = { position = it },
                scrubber = { enabled, scrubbing ->
                    TimeBarScrubber(
                        enabled,
                        scrubbing,
                        enabledSize = ScrubberEnabledSize.dp
                    )
                }
            )
        }
        // click bar left
        composeTestRule.onNodeWithTag(TestTag_TimeBar)
            .performTouchInput {
                click(Offset(0.dp.toPx(), 0f))
            }
        composeTestRule.waitForIdle()
        assertEquals(0L, position)

        // click 1/10 duration
        composeTestRule.onNodeWithTag(TestTag_TimeBar)
            .performTouchInput {
                click(Offset(10.dp.toPx(), 0f))
            }
        composeTestRule.waitForIdle()
        assertEquals(10, position)

        // click bar center
        composeTestRule.onNodeWithTag(TestTag_TimeBar)
            .performTouchInput {
                click(center)
            }
        composeTestRule.waitForIdle()
        assertEquals(50L, position)

        // click bar right
        composeTestRule.onNodeWithTag(TestTag_TimeBar)
            .performTouchInput {
                click(Offset(100.dp.toPx(), 0f))
            }
        composeTestRule.waitForIdle()
        assertEquals(100L, position)
    }

    @Test
    fun clickScrubberCenterAsAnchorWithContentPadding() {
        var position by mutableStateOf(0L)
        val paddingHorizontal = (ScrubberDraggedSize / 2).dp
        val barWidth = 100.dp + paddingHorizontal * 2
        composeTestRule.setContent {
            TimeBar(
                scrubberCenterAsAnchor = true,
                contentPadding = PaddingValues(horizontal = paddingHorizontal),
                durationMs = 100,
                positionMs = position,
                bufferedPositionMs = 0,
                modifier = Modifier
                    .testTag(TestTag_TimeBar)
                    .size(barWidth, 4.dp),
                onScrubStop = { position = it },
                scrubber = { enabled, scrubbing ->
                    TimeBarScrubber(
                        enabled,
                        scrubbing,
                        enabledSize = ScrubberEnabledSize.dp
                    )
                }
            )
        }
        // click bar left
        composeTestRule.onNodeWithTag(TestTag_TimeBar)
            .performTouchInput {
                click(Offset(0.dp.toPx(), 0f))
            }
        composeTestRule.waitForIdle()
        assertEquals(0L, position)

        // click content left
        composeTestRule.onNodeWithTag(TestTag_TimeBar)
            .performTouchInput {
                click(Offset(paddingHorizontal.toPx(), 0f))
            }
        composeTestRule.waitForIdle()
        assertEquals(0L, position)

        // click 1/10 duration
        composeTestRule.onNodeWithTag(TestTag_TimeBar)
            .performTouchInput {
                click(Offset((paddingHorizontal + 10.dp).toPx(), 0f))
            }
        composeTestRule.waitForIdle()
        assertEquals(10, position)

        // click bar center
        composeTestRule.onNodeWithTag(TestTag_TimeBar)
            .performTouchInput {
                click(center)
            }
        composeTestRule.waitForIdle()
        assertEquals(50L, position)

        // click content right
        composeTestRule.onNodeWithTag(TestTag_TimeBar)
            .performTouchInput {
                click(Offset((100.dp + paddingHorizontal).toPx(), 0f))
            }
        composeTestRule.waitForIdle()
        assertEquals(100L, position)

        // click bar right
        composeTestRule.onNodeWithTag(TestTag_TimeBar)
            .performTouchInput {
                click(Offset(barWidth.toPx(), 0f))
            }
        composeTestRule.waitForIdle()
        assertEquals(100L, position)
    }
}

private const val TestTag_TimeBar = "TimeBar"
private const val ScrubberEnabledSize = 10
private const val ScrubberDraggedSize = 16
