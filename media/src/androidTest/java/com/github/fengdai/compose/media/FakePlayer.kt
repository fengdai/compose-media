package com.github.fengdai.compose.media

import android.view.SurfaceView
import android.view.TextureView
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.testutil.StubPlayer
import com.google.android.exoplayer2.text.Cue
import com.google.android.exoplayer2.trackselection.TrackSelectionParameters
import com.google.android.exoplayer2.util.FlagSet
import com.google.android.exoplayer2.video.VideoSize
import java.util.concurrent.CopyOnWriteArrayList

open class FakePlayer : StubPlayer() {
    var listeners = CopyOnWriteArrayList<Player.Listener>()
    var surface: Any? = null
    var playbackState_: Int = Player.STATE_IDLE
        set(value) {
            val previousValue = field
            field = value
            if (value != previousValue) {
                listeners.forEach {
                    it.onPlaybackStateChanged(value)
                    it.onEvents(
                        this,
                        Player.Events(
                            FlagSet.Builder().add(Player.EVENT_PLAYBACK_STATE_CHANGED).build()
                        )
                    )
                }
            }
        }
    var playWhenReady_: Boolean = false
    var trackInfo: TracksInfo = TracksInfo.EMPTY
        set(value) {
            val previousValue = field
            field = value
            if (value != previousValue) {
                listeners.forEach { it.onTracksInfoChanged(value) }
            }
        }
    var mediaMetadata_: MediaMetadata = MediaMetadata.EMPTY
        set(value) {
            val previousValue = field
            field = value
            if (value != previousValue) {
                listeners.forEach { it.onMediaMetadataChanged(value) }
            }
        }
    var timeline_: Timeline = Timeline.EMPTY

    override fun addListener(listener: Player.Listener) {
        listeners.add(listener)
    }

    override fun removeListener(listener: Player.Listener) {
        listeners.remove(listener)
    }

    override fun setVideoSurfaceView(surfaceView: SurfaceView?) {
        this.surface = surfaceView
    }

    override fun clearVideoSurfaceView(surfaceView: SurfaceView?) {
        if (surfaceView != null && surfaceView === this.surface) {
            clearVideoSurface()
        }
    }

    override fun setVideoTextureView(textureView: TextureView?) {
        this.surface = textureView
    }

    override fun clearVideoTextureView(textureView: TextureView?) {
        if (textureView != null && textureView === this.surface) {
            clearVideoSurface()
        }
    }

    override fun clearVideoSurface() {
        surface = null
    }

    override fun getCurrentTimeline(): Timeline {
        return timeline_
    }

    override fun getCurrentMediaItemIndex(): Int {
        return 0
    }

    override fun getCurrentTracksInfo(): TracksInfo {
        return trackInfo
    }

    override fun getMediaMetadata(): MediaMetadata {
        return mediaMetadata_
    }

    override fun getPlaylistMetadata(): MediaMetadata {
        return MediaMetadata.EMPTY
    }

    override fun isLoading(): Boolean {
        return false
    }

    override fun getAvailableCommands(): Player.Commands {
        return Player.Commands.EMPTY
    }

    override fun getTrackSelectionParameters(): TrackSelectionParameters {
        return TrackSelectionParameters.DEFAULT_WITHOUT_CONTEXT
    }

    override fun getPlaybackState(): Int {
        return playbackState_
    }

    override fun setPlayWhenReady(playWhenReady: Boolean) {
        val previousPlayWhenReady = playWhenReady_
        playWhenReady_ = playWhenReady
        if (playWhenReady != previousPlayWhenReady) {
            listeners.forEach {
                it.onPlayWhenReadyChanged(playWhenReady, PLAY_WHEN_READY_CHANGE_REASON_USER_REQUEST)
                it.onEvents(
                    this,
                    Player.Events(
                        FlagSet.Builder().add(PLAY_WHEN_READY_CHANGE_REASON_USER_REQUEST).build()
                    )
                )
            }
        }
    }

    override fun getPlayWhenReady(): Boolean {
        return playWhenReady_
    }

    override fun getPlaybackSuppressionReason(): Int {
        return PLAYBACK_SUPPRESSION_REASON_NONE
    }

    override fun getRepeatMode(): Int {
        return REPEAT_MODE_OFF
    }

    override fun getShuffleModeEnabled(): Boolean {
        return false
    }

    override fun getPlayerError(): PlaybackException {
        return PlaybackException(null, null, PlaybackException.ERROR_CODE_UNSPECIFIED)
    }

    override fun getPlaybackParameters(): PlaybackParameters {
        return PlaybackParameters.DEFAULT
    }

    override fun getSeekBackIncrement(): Long {
        return 5000
    }

    override fun getSeekForwardIncrement(): Long {
        return 5000
    }

    override fun getMaxSeekToPreviousPosition(): Long {
        return 0
    }

    override fun getAudioAttributes(): AudioAttributes {
        return AudioAttributes.DEFAULT
    }

    override fun getVolume(): Float {
        return 0f
    }

    override fun getDeviceInfo(): DeviceInfo {
        return DeviceInfo.UNKNOWN
    }

    override fun getDeviceVolume(): Int {
        return 0
    }

    override fun isDeviceMuted(): Boolean {
        return false
    }

    override fun getVideoSize(): VideoSize {
        return VideoSize.UNKNOWN
    }

    override fun getCurrentCues(): List<Cue> {
        return emptyList()
    }
}
