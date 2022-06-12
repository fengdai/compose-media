package com.github.fengdai.compose.media

import androidx.compose.runtime.*
import com.google.android.exoplayer2.Player

/**
 * Create and [remember] a [MediaState] instance.
 *
 * Changes to [playerState] will result in the [MediaState] being updated.
 *
 * @param playerState the value for [MediaState.playerState]
 */
@Composable
fun rememberMediaState(
    playerState: PlayerState?
): MediaState {
    return remember { MediaState() }.apply {
        this.playerState = playerState
    }
}

/**
 * A state object that can be hoisted to control and observe changes for [Media].
 */
@Stable
class MediaState {
    /**
     * The state of the [Media]'s player. Null means it doesn't have a player associated with it.
     */
    var playerState: PlayerState? by mutableStateOf(null)

    /**
     * The state of the [Media]'s controller.
     */
    val controllerState: ControllerState = ControllerState()

    inner class ControllerState internal constructor() {
        /**
         * Whether the playback controls are hidden by touch. Default is true.
         */
        var hideOnTouch by mutableStateOf(true)

        /**
         * Whether the playback controls are automatically shown when playback starts, pauses, ends, or
         * fails.
         */
        var autoShow by mutableStateOf(true)

        var isShowing
            get() = visibility.isShowing
            set(value) {
                visibility = if (value) ControllerVisibility.Visible
                else ControllerVisibility.Invisible
            }

        var visibility by mutableStateOf(ControllerVisibility.Invisible)

        val shouldShowIndefinitely by derivedStateOf {
            playerState?.run {
                !timeline.isEmpty
                        &&
                        (playbackState == Player.STATE_IDLE
                                || playbackState == Player.STATE_ENDED
                                || !playWhenReady)
            } ?: true
        }

        val showPause by derivedStateOf {
            playerState?.run {
                playbackState != Player.STATE_ENDED
                        && playbackState != Player.STATE_IDLE
                        && playWhenReady
            } ?: false
        }

        internal fun toggleVisibility() {
            visibility = when (visibility) {
                ControllerVisibility.Visible -> {
                    if (hideOnTouch) ControllerVisibility.Invisible
                    else ControllerVisibility.Visible
                }
                ControllerVisibility.PartiallyVisible -> ControllerVisibility.Visible
                ControllerVisibility.Invisible -> ControllerVisibility.Visible
            }
        }

        internal fun maybeShow(force: Boolean = false): Boolean {
            if (force || (autoShow && shouldShowIndefinitely)) {
                visibility = ControllerVisibility.Visible
                return true
            }
            return false
        }
    }
}

/**
 * The visibility state of the controller.
 */
enum class ControllerVisibility(
    val isShowing: Boolean,
) {
    /**
     * All UI controls are visible.
     */
    Visible(true),

    /**
     * A part of UI controls are visible.
     */
    PartiallyVisible(true),

    /**
     * All UI controls are hidden.
     */
    Invisible(false)
}
