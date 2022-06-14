package com.github.fengdai.compose.media

import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.painter.Painter
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.video.VideoSize
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

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
                        PlayerStateImpl(current, listener).apply {
                            if (rememberScope != null) registerListener()
                        }
                    } else null
                onPlayerChanged(current)
            }
        }
        get() = _playerState?.player

    /**
     * The state of the [Media]'s [player].
     */
    val playerState: PlayerState? get() = _playerState

    // Controller visibility related properties and functions
    /**
     * Whether the controller is showing.
     */
    var isControllerShowing: Boolean
        get() = controllerVisibility.isShowing
        set(value) {
            controllerVisibility = if (value) ControllerVisibility.Visible
            else ControllerVisibility.Invisible
        }

    /**
     * The current [visibility][ControllerVisibility] of the controller.
     */
    var controllerVisibility: ControllerVisibility by mutableStateOf(ControllerVisibility.Invisible)

    /**
     * Typically, when controller is shown, it will be automatically hidden after a short time has
     * elapsed without user interaction. If [shouldShowControllerIndefinitely] is true, you should
     * consider disabling this behavior, and show the controller indefinitely.
     */
    val shouldShowControllerIndefinitely: Boolean by derivedStateOf {
        playerState?.run {
            controllerAutoShow
                    && !timeline.isEmpty
                    && (playbackState == Player.STATE_IDLE
                        || playbackState == Player.STATE_ENDED
                        || !playWhenReady)
        } ?: true
    }

    internal var controllerAutoShow: Boolean by mutableStateOf(true)

    internal fun maybeShowController() {
        if (shouldShowControllerIndefinitely) {
            controllerVisibility = ControllerVisibility.Visible
        }
    }

    // internally used properties and functions
    private fun onPlayerChanged(current: Player?) {
        if (current == null) {
            controllerVisibility = ControllerVisibility.Invisible
        }
    }

    private var _playerState: PlayerStateImpl? by mutableStateOf(null)
    private val listener = object : Player.Listener {
        override fun onRenderedFirstFrame() {
            closeShutter = false
            artworkPainter = null
        }

        override fun onEvents(player: Player, events: Player.Events) {
            if (events.containsAny(
                    Player.EVENT_PLAYBACK_STATE_CHANGED,
                    Player.EVENT_PLAY_WHEN_READY_CHANGED
                )
            ) {
                maybeShowController()
            }
        }
    }

    private val contentAspectRatioRaw by derivedStateOf {
        artworkPainter?.aspectRatio
            ?: (playerState?.videoSize ?: VideoSize.UNKNOWN).aspectRatio
    }
    private var _contentAspectRatio by mutableStateOf(0f)
    internal var contentAspectRatio
        private set(value) {
            val aspectDeformation: Float = value / contentAspectRatio - 1f
            if (aspectDeformation.absoluteValue > 0.01f) {
                // Not within the allowed tolerance, populate the new aspectRatio.
                _contentAspectRatio = value
            }
        }
        get() = _contentAspectRatio

    // true: video track is selected
    // false: non video track is selected
    // null: there isn't any track
    internal val isVideoTrackSelected: Boolean? by derivedStateOf {
        playerState?.tracksInfo
            ?.takeIf { it.trackGroupInfos.isNotEmpty() }
            ?.isTypeSelected(C.TRACK_TYPE_VIDEO)
    }

    internal var closeShutter by mutableStateOf(true)

    internal val artworkData: ByteArray? by derivedStateOf {
        playerState?.mediaMetadata?.artworkData
    }
    internal var artworkPainter by mutableStateOf<Painter?>(null)

    internal val playerError: PlaybackException? by derivedStateOf {
        playerState?.playerError
    }

    private var rememberScope: CoroutineScope? = null

    // Don't let MediaState implement RememberObserver directly to avoid this issue:
    // https://kotlinlang.slack.com/archives/CJLTWPH7S/p1653543177516939
    internal val rememberObserver = object : RememberObserver {
        override fun onRemembered() {
            if (rememberScope != null) return
            _playerState?.registerListener()

            val scope = CoroutineScope(Job())
            rememberScope = scope

            scope.launch {
                snapshotFlow { contentAspectRatioRaw }
                    .collect { aspectRatio -> contentAspectRatio = aspectRatio }
            }
        }

        override fun onForgotten() {
            clear()
        }

        override fun onAbandoned() {
            clear()
        }

        private fun clear() {
            _playerState?.unregisterListener()
            rememberScope?.cancel()
            rememberScope = null
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

private val VideoSize.aspectRatio
    get() = if (height == 0) 0f else width * pixelWidthHeightRatio / height
private val Painter.aspectRatio
    get() = intrinsicSize.run {
        if (this == Size.Unspecified || width.isNaN() || height.isNaN() || height == 0f) 0f
        else width / height
    }
