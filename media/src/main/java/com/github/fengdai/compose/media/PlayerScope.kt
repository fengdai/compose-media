package com.github.fengdai.compose.media

import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.text.Cue
import com.google.android.exoplayer2.trackselection.TrackSelectionParameters
import com.google.android.exoplayer2.video.VideoSize

/**
 * Nonnull-player's scope, most states of it are also nonnull.
 */
class PlayerScope internal constructor(
    mediaState: MediaState
) {
    val player: Player = mediaState.player!!
    val timeline: Timeline = mediaState.timeline!!
    val tracksInfo: TracksInfo = mediaState.tracksInfo!!
    val mediaMetadata: MediaMetadata = mediaState.mediaMetadata!!
    val playlistMetadata: MediaMetadata = mediaState.playlistMetadata!!
    val isLoading: Boolean = mediaState.isLoading!!
    val availableCommands: Player.Commands = mediaState.availableCommands!!
    val trackSelectionParameters: TrackSelectionParameters = mediaState.trackSelectionParameters!!

    @get:Player.State
    val playbackState: Int = mediaState.playbackState!!

    val playWhenReady: Boolean = mediaState.playWhenReady!!

    @get:Player.PlaybackSuppressionReason
    val playbackSuppressionReason: Int = mediaState.playbackSuppressionReason!!

    val isPlaying: Boolean = mediaState.isPlaying!!

    @get:Player.RepeatMode
    val repeatMode: Int = mediaState.repeatMode!!

    val shuffleModeEnabled: Boolean = mediaState.shuffleModeEnabled!!
    val playerError: PlaybackException? = mediaState.playerError
    val playbackParameters: PlaybackParameters = mediaState.playbackParameters!!
    val seekBackIncrement: Long = mediaState.seekBackIncrement!!
    val seekForwardIncrement: Long = mediaState.seekForwardIncrement!!
    val maxSeekToPreviousPosition: Long = mediaState.maxSeekToPreviousPosition!!
    val audioAttributes: AudioAttributes = mediaState.audioAttributes!!
    val volume: Float = mediaState.volume!!
    val deviceInfo: DeviceInfo = mediaState.deviceInfo!!
    val deviceVolume: Int = mediaState.deviceVolume!!
    val isDeviceMuted: Boolean = mediaState.isDeviceMuted!!
    val videoSize: VideoSize = mediaState.videoSize!!
    val cues: List<Cue> = mediaState.cues!!
}
