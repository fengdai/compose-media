package com.github.fengdai.compose.media

import androidx.compose.runtime.*
import com.google.android.exoplayer2.Player

/**
 * Create and [remember] a [MediaState] instance.
 *
 * Changes to [player] will result in the [MediaState] being updated.
 *
 * @param player the value for [MediaState.player]
 */
@Composable
fun rememberMediaState(
    player: Player?
): MediaState {
    val state = remember { MediaState() }
    remember { state.rememberObserver }
    state.player = player
    return state
}

/**
 * A state object that can be hoisted to control and observe changes for [Media].
 */
@Stable
class MediaState internal constructor() {
    private var _playerState: PlayerStateImpl? by mutableStateOf(null)
    private var remembered = false

    // Don't let MediaState implement RememberObserver directly to avoid this issue:
    // https://kotlinlang.slack.com/archives/CJLTWPH7S/p1653543177516939
    internal val rememberObserver = object : RememberObserver {
        override fun onRemembered() {
            remembered = true
            _playerState?.registerListener()
        }

        override fun onForgotten() {
            remembered = false
            _playerState?.unregisterListener()
        }

        override fun onAbandoned() {
            remembered = false
            _playerState?.unregisterListener()
        }
    }

    /**
     * The player associated with the [Media].
     */
    var player: Player?
        set(current) {
            val previous = _playerState?.player
            if (current !== previous) {
                // unregister old PlayerState
                _playerState?.unregisterListener()
                _playerState =
                    if (current != null) {
                        // register new PlayerState
                        PlayerStateImpl(current).apply {
                            if (remembered) registerListener()
                        }
                    } else null
            }
        }
        get() = _playerState?.player

    /**
     * The state of the [Media]'s [player].
     */
    val playerState: PlayerState? get() = _playerState

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
