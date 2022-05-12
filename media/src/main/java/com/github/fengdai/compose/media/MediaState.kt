package com.github.fengdai.compose.media

import androidx.compose.runtime.*
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.text.Cue
import com.google.android.exoplayer2.trackselection.TrackSelectionParameters
import com.google.android.exoplayer2.video.VideoSize

/**
 * Creates a [MediaState] that is remembered across compositions.
 *
 * Changes to [player] will result in the [MediaState] being updated.
 *
 * @param player the value for [MediaState.player]
 */
@Composable
fun rememberUpdatedMediaState(
    player: Player?
): MediaState {
    val state = remember { MediaState(player) }.apply {
        this.player = player
        timeline = player?.currentTimeline
        tracksInfo = player?.currentTracksInfo
        mediaMetadata = player?.mediaMetadata
        playlistMetadata = player?.playlistMetadata
        isLoading = player?.isLoading
        availableCommands = player?.availableCommands
        trackSelectionParameters = player?.trackSelectionParameters
        playbackState = player?.playbackState
        playWhenReady = player?.playWhenReady
        playbackSuppressionReason = player?.playbackSuppressionReason
        isPlaying = player?.isPlaying
        repeatMode = player?.repeatMode
        shuffleModeEnabled = player?.shuffleModeEnabled
        playerError = player?.playerError
        playbackParameters = player?.playbackParameters
        seekBackIncrement = player?.seekBackIncrement
        seekForwardIncrement = player?.seekForwardIncrement
        maxSeekToPreviousPosition = player?.maxSeekToPreviousPosition
        audioAttributes = player?.audioAttributes
        volume = player?.volume
        deviceInfo = player?.deviceInfo
        deviceVolume = player?.deviceVolume
        isDeviceMuted = player?.isDeviceMuted
        videoSize = player?.videoSize
        cues = player?.currentCues
    }
    DisposableEffect(player) {
        val listener = object : Player.Listener {
            override fun onTimelineChanged(timeline: Timeline, reason: Int) {
                state.timeline = timeline
            }

            override fun onTracksInfoChanged(tracksInfo: TracksInfo) {
                state.tracksInfo = tracksInfo
            }

            override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
                state.mediaMetadata = mediaMetadata
            }

            override fun onPlaylistMetadataChanged(mediaMetadata: MediaMetadata) {
                state.playlistMetadata = mediaMetadata
            }

            override fun onIsLoadingChanged(isLoading: Boolean) {
                state.isLoading
            }

            override fun onAvailableCommandsChanged(availableCommands: Player.Commands) {
                state.availableCommands = availableCommands
            }

            override fun onTrackSelectionParametersChanged(parameters: TrackSelectionParameters) {
                state.trackSelectionParameters = parameters
            }

            override fun onPlaybackStateChanged(@Player.State playbackState: Int) {
                state.playbackState = playbackState
            }

            override fun onPlayWhenReadyChanged(
                playWhenReady: Boolean,
                @Player.PlayWhenReadyChangeReason reason: Int
            ) {
                state.playWhenReady = playWhenReady
            }

            override fun onPlaybackSuppressionReasonChanged(playbackSuppressionReason: Int) {
                state.playbackSuppressionReason = playbackSuppressionReason
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                state.isPlaying = isPlaying
            }

            override fun onRepeatModeChanged(repeatMode: Int) {
                state.repeatMode = repeatMode
            }

            override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
                state.shuffleModeEnabled = shuffleModeEnabled
            }

            override fun onPlayerErrorChanged(error: PlaybackException?) {
                state.playerError = error
            }

            override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) {
                state.playbackParameters = playbackParameters
            }

            override fun onSeekBackIncrementChanged(seekBackIncrementMs: Long) {
                state.seekBackIncrement = seekBackIncrementMs
            }

            override fun onSeekForwardIncrementChanged(seekForwardIncrementMs: Long) {
                state.seekForwardIncrement = seekForwardIncrementMs
            }

            override fun onMaxSeekToPreviousPositionChanged(maxSeekToPreviousPositionMs: Long) {
                state.maxSeekToPreviousPosition = maxSeekToPreviousPositionMs
            }

            override fun onAudioSessionIdChanged(audioSessionId: Int) {
                //
            }

            override fun onAudioAttributesChanged(audioAttributes: AudioAttributes) {
                state.audioAttributes = audioAttributes
            }

            override fun onVolumeChanged(volume: Float) {
                state.volume = volume
            }

            override fun onSkipSilenceEnabledChanged(skipSilenceEnabled: Boolean) {
                //
            }

            override fun onDeviceInfoChanged(deviceInfo: DeviceInfo) {
                state.deviceInfo = deviceInfo
            }

            override fun onDeviceVolumeChanged(volume: Int, muted: Boolean) {
                state.deviceVolume = volume
                state.isDeviceMuted = muted
            }

            override fun onVideoSizeChanged(videoSize: VideoSize) {
                state.videoSize = videoSize
            }

            override fun onSurfaceSizeChanged(width: Int, height: Int) {
                //
            }

            override fun onCues(cues: List<Cue>) {
                state.cues = cues
            }
        }
        player?.addListener(listener)
        onDispose {
            player?.removeListener(listener)
        }
    }
    return state
}

/**
 * A state object that can be hoisted to control and observe changes for [Media].
 */
@Stable
class MediaState internal constructor(player: Player?) {
    /**
     * The state of the [Media]'s controller.
     */
    val controllerState: ControllerState = ControllerState()

    var player: Player? by mutableStateOf(player)
        internal set

    var timeline: Timeline? by mutableStateOf(player?.currentTimeline)
        internal set

    var tracksInfo: TracksInfo? by mutableStateOf(player?.currentTracksInfo)
        internal set

    var mediaMetadata: MediaMetadata? by mutableStateOf(player?.mediaMetadata)
        internal set

    var playlistMetadata: MediaMetadata? by mutableStateOf(player?.playlistMetadata)
        internal set

    var isLoading: Boolean? by mutableStateOf(player?.isLoading)
        internal set

    var availableCommands: Player.Commands? by mutableStateOf(player?.availableCommands)
        internal set

    var trackSelectionParameters: TrackSelectionParameters? by mutableStateOf(player?.trackSelectionParameters)
        internal set

    @get:Player.State
    var playbackState: Int? by mutableStateOf(player?.playbackState)
        internal set

    var playWhenReady: Boolean? by mutableStateOf(player?.playWhenReady)
        internal set

    @get:Player.PlaybackSuppressionReason
    var playbackSuppressionReason: Int? by mutableStateOf(player?.playbackSuppressionReason)
        internal set

    var isPlaying: Boolean? by mutableStateOf(player?.isPlaying)
        internal set

    @get:Player.RepeatMode
    var repeatMode: Int? by mutableStateOf(player?.repeatMode)
        internal set

    var shuffleModeEnabled: Boolean? by mutableStateOf(player?.shuffleModeEnabled)
        internal set

    var playerError: PlaybackException? by mutableStateOf(player?.playerError)
        internal set

    var playbackParameters: PlaybackParameters? by mutableStateOf(player?.playbackParameters)
        internal set

    var seekBackIncrement: Long? by mutableStateOf(player?.seekBackIncrement)
        internal set

    var seekForwardIncrement: Long? by mutableStateOf(player?.seekForwardIncrement)
        internal set

    var maxSeekToPreviousPosition: Long? by mutableStateOf(player?.maxSeekToPreviousPosition)
        internal set

    var audioAttributes: AudioAttributes? by mutableStateOf(player?.audioAttributes)
        internal set

    var volume: Float? by mutableStateOf(player?.volume)
        internal set

    var deviceInfo: DeviceInfo? by mutableStateOf(player?.deviceInfo)
        internal set

    var deviceVolume: Int? by mutableStateOf(player?.deviceVolume)
        internal set

    var isDeviceMuted: Boolean? by mutableStateOf(player?.isDeviceMuted)
        internal set

    var videoSize: VideoSize? by mutableStateOf(player?.videoSize)
        internal set

    var cues: List<Cue>? by mutableStateOf(player?.currentCues)
        internal set

    /**
     * Enter [PlayerScope] if [player] is not null.
     */
    fun <T> maybeEnterPlayerScope(block: PlayerScope.() -> T): T? {
        return if (player != null) with(PlayerScope(this)) { block() }
        else null
    }

    inner class ControllerState {
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
            maybeEnterPlayerScope {
                !timeline.isEmpty
                        &&
                        (playbackState == Player.STATE_IDLE
                                || playbackState == Player.STATE_ENDED
                                || !playWhenReady)
            } ?: true
        }

        val showPause by derivedStateOf {
            playbackState != null
                    && playbackState != Player.STATE_ENDED
                    && playbackState != Player.STATE_IDLE
                    && playWhenReady == true
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
