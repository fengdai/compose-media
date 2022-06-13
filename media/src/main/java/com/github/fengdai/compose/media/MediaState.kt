package com.github.fengdai.compose.media

import androidx.compose.runtime.*
import com.google.android.exoplayer2.Player

/**
 * Create and [remember] a [MediaState] instance.
 */
@Composable
fun rememberMediaState(): MediaState {
    return remember { MediaState() }
}

/**
 * Create and [remember] a [MediaState] instance. Update its [playerState][MediaState.playerState]
 * to reflect the [player] changes on each recomposition of the [rememberUpdatedMediaState] call.
 */
@Composable
fun rememberUpdatedMediaState(
    player: Player?
): MediaState {
    return remember { MediaState() }.apply {
        playerState = if (player != null) rememberPlayerState(player) else null
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

    // Controller visibility related properties and functions
    /**
     * Whether the playback controls are hidden by touch. Default is true.
     */
    var controllerHideOnTouch by mutableStateOf(true)

    /**
     * Whether the playback controls are automatically shown when playback starts, pauses, ends, or
     * fails.
     */
    var controllerAutoShow by mutableStateOf(true)

    var isControllerShowing
        get() = controllerVisibility.isShowing
        set(value) {
            controllerVisibility = if (value) ControllerVisibility.Visible
            else ControllerVisibility.Invisible
        }

    var controllerVisibility by mutableStateOf(ControllerVisibility.Invisible)

    val shouldShowControllerIndefinitely by derivedStateOf {
        playerState?.run {
            !timeline.isEmpty
                    &&
                    (playbackState == Player.STATE_IDLE
                            || playbackState == Player.STATE_ENDED
                            || !playWhenReady)
        } ?: true
    }

    internal fun toggleControllerVisibility() {
        controllerVisibility = when (controllerVisibility) {
            ControllerVisibility.Visible -> {
                if (controllerHideOnTouch) ControllerVisibility.Invisible
                else ControllerVisibility.Visible
            }
            ControllerVisibility.PartiallyVisible -> ControllerVisibility.Visible
            ControllerVisibility.Invisible -> ControllerVisibility.Visible
        }
    }

    internal fun maybeShowController(force: Boolean = false): Boolean {
        if (force || (controllerAutoShow && shouldShowControllerIndefinitely)) {
            controllerVisibility = ControllerVisibility.Visible
            return true
        }
        return false
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
