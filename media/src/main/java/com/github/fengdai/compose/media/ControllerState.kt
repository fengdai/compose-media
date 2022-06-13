package com.github.fengdai.compose.media

import androidx.compose.runtime.*
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.Timeline

/**
 * Create and [remember] a [ControllerState] instance.
 *
 * Changes to [playerState] will result in the [ControllerState] being updated.
 *
 * @param playerState the value for [ControllerState.playerState]
 */
@Composable
fun rememberControllerState(
    playerState: PlayerState?
): ControllerState {
    return remember { ControllerState() }.apply {
        this.playerState = playerState
    }
}

@Stable
class ControllerState {
    var playerState: PlayerState? by mutableStateOf(null)

    /**
     * If ture, show pause button. Otherwise, show play button.
     */
    val showPause: Boolean by derivedStateOf {
        playerState?.run {
            playbackState != Player.STATE_ENDED
                    && playbackState != Player.STATE_IDLE
                    && playWhenReady
        } ?: false
    }

    /**
     * The duration, in milliseconds.
     */
    val durationMs: Long by derivedStateOf {
        windowOffsetAndDurations
            ?.run {
                if (multiWindowTimeBar) this.lastOrNull()?.run { first + second }
                else this[playerState?.mediaItemIndex!!].second
            } ?: C.TIME_UNSET
    }

    /**
     * The current position, in milliseconds.
     */
    val positionMs: Long by derivedStateOf {
        positionUpdateTrigger
        playerState?.run { currentWindowOffset + player.contentPosition } ?: 0L
    }

    /**
     * The current buffered position, in milliseconds.
     */
    val bufferedPositionMs: Long by derivedStateOf {
        positionUpdateTrigger
        playerState?.run { currentWindowOffset + player.contentBufferedPosition } ?: 0L
    }

    /**
     * Whether the time bar should show all windows, as opposed to just the current one. If the
     * timeline has a period with unknown duration or more than
     * [MAX_WINDOWS_FOR_MULTI_WINDOW_TIME_BAR] windows the time bar will fall back to showing
     * a single window.
     */
    var showMultiWindowTimeBar: Boolean by mutableStateOf(false)

    fun triggerPositionUpdate() {
        positionUpdateTrigger++
    }

    fun seekTo(positionMs: Long) {
        playerState?.run {
            var position = positionMs
            var windowIndex = mediaItemIndex
            if (multiWindowTimeBar) {
                val windowCount = timeline.windowCount
                windowIndex = 0
                while (true) {
                    val windowDurationMs = timeline.getWindow(windowIndex, window).durationMs
                    if (position < windowDurationMs) {
                        break
                    } else if (windowIndex == windowCount - 1) {
                        // Seeking past the end of the last window should seek to the end of the timeline.
                        position = windowDurationMs
                        break
                    }
                    position -= windowDurationMs
                    windowIndex++
                }
            }
            player.seekTo(windowIndex, position)
            triggerPositionUpdate()
        }
    }

    private var positionUpdateTrigger by mutableStateOf(0L)

    private val window: Timeline.Window = Timeline.Window()
    private val Timeline.windows: Sequence<Timeline.Window>
        get() = sequence {
            for (index in 0 until windowCount) {
                getWindow(index, window)
                yield(window)
            }
        }
    private val Timeline.canShowMultiWindowTimeBar: Boolean
        get() = windowCount <= MAX_WINDOWS_FOR_MULTI_WINDOW_TIME_BAR
                && windows.all { it.durationUs != C.TIME_UNSET }
    private val multiWindowTimeBar: Boolean by derivedStateOf {
        showMultiWindowTimeBar && (playerState?.timeline?.canShowMultiWindowTimeBar ?: false)
    }

    // Offset and duration pairs of all windows in current timeline.
    private val windowOffsetAndDurations: List<Pair<Long, Long>>? by derivedStateOf {
        playerState?.takeIf { !it.timeline.isEmpty }?.run {
            if (multiWindowTimeBar) {
                timeline.windows.fold(mutableListOf()) { acc, window ->
                    val windowOffset = acc.lastOrNull()?.run { first + second } ?: 0L
                    acc.add(windowOffset to window.durationMs)
                    acc
                }
            } else {
                timeline.windows.map { window -> 0L to window.durationMs }.toList()
            }
        }
    }

    // Current window offset, in milliseconds.
    private val currentWindowOffset: Long by derivedStateOf {
        windowOffsetAndDurations?.get(playerState?.mediaItemIndex!!)?.first ?: 0L
    }

    companion object {
        const val MAX_WINDOWS_FOR_MULTI_WINDOW_TIME_BAR = 100
    }
}
