package com.github.fengdai.compose.media

import android.graphics.BitmapFactory
import android.view.SurfaceView
import android.view.View
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.text.Cue
import com.google.android.exoplayer2.video.VideoSize
import kotlin.math.absoluteValue

/**
 * The type of surface view used for video playbacks.
 */
enum class SurfaceType {
    None,
    SurfaceView,
    TextureView;
}

/**
 * Controls how video and album art is resized.
 */
enum class ResizeMode {
    /**
     * Either the width or height is decreased to obtain the desired aspect ratio.
     */
    Fit,

    /**
     * The width is fixed and the height is increased or decreased to obtain the desired aspect ratio.
     */
    FixedWidth,

    /**
     * The height is fixed and the width is increased or decreased to obtain the desired aspect ratio.
     */
    FixedHeight,

    /**
     * The specified aspect ratio is ignored.
     */
    Fill,

    /**
     * Either the width or height is increased to obtain the desired aspect ratio.
     */
    Zoom,
}

/**
 * Determines when the buffering indicator is shown.
 */
enum class ShowBuffering {
    /**
     * The buffering indicator is never shown.
     */
    Never,

    /**
     * The buffering indicator is shown when the player is in the [buffering][Player.STATE_BUFFERING]
     * state and [playWhenReady][Player.getPlayWhenReady] is true.
     */
    WhenPlaying,

    /**
     * The buffering indicator is always shown when the player is in the
     * [buffering][Player.STATE_BUFFERING] state.
     */
    Always;
}

/**
 * Composable component for [Player] media playbacks.
 *
 * @param state The state object to be used to control or observe the [Media] state.
 * @param modifier The modifier to apply to this layout.
 * @param surfaceType The type of surface view used for video playbacks.Using [SurfaceType.None] is
 * recommended for audio only applications, since creating the surface can be expensive. Using
 * [SurfaceType.SurfaceView] is recommended for video applications. Note, [SurfaceType.TextureView]
 * can only be used in a hardware accelerated window. When rendered in software, TextureView will
 * draw nothing.
 * @param resizeMode Controls how video and album art is resized.
 * @param shutterColor The color of the shutter, which used for hiding the currently displayed video
 * frame or media artwork when the player is reset and [keepContentOnPlayerReset] is false.
 * @param keepContentOnPlayerReset Whether the currently displayed video frame or media artwork is
 * kept visible when the player is reset. A player reset is defined to mean the player being
 * re-prepared with different media, the player transitioning to unprepared media or an empty list
 * of media items, or the player being changed.
 * If true is provided, the currently displayed video frame or media artwork will be kept visible
 * until the player has been successfully prepared with new media and loaded enough of it to have
 * determined the available tracks. Hence enabling this option allows transitioning from playing one
 * piece of media to another, or from using one player instance to another, without clearing the
 * content.
 * If false is provided, the currently displayed video frame or media artwork will be hidden as soon
 * as the player is reset.
 * @param useArtwork Whether artwork is used if available in audio streams.
 * @param defaultArtworkPainter The [Painter], which will be used to draw default artwork if no
 * artwork available in audio streams.
 * @param subtitles The subtitles. Default is null.
 * @param showBuffering Determines when the buffering indicator is shown.
 * @param buffering The buffering indicator, typically a circular progress indicator. Default is
 * null.
 * @param errorMessage The error message, which will be shown when an [error][PlaybackException]
 * occurred. Default is null.
 * @param overlay An overlay, which can be shown on top of the player. Default is null.
 * @param controller The controller. Since a controller is always a subject to be customized,
 * default is null. The [Media] only provides logic for controller visibility controlling.
 */
@Composable
fun Media(
    state: MediaState,
    modifier: Modifier = Modifier,
    surfaceType: SurfaceType = SurfaceType.SurfaceView,
    resizeMode: ResizeMode = ResizeMode.Fit,
    shutterColor: Color = Color.Black,
    keepContentOnPlayerReset: Boolean = false,
    useArtwork: Boolean = true,
    defaultArtworkPainter: Painter? = null,
    subtitles: @Composable ((List<Cue>) -> Unit)? = null, // TODO
    showBuffering: ShowBuffering = ShowBuffering.Never,
    buffering: @Composable (() -> Unit)? = null,
    errorMessage: @Composable ((PlaybackException) -> Unit)? = null,
    overlay: @Composable (() -> Unit)? = null,
    controller: @Composable ((MediaState) -> Unit)? = null
) {
    if (showBuffering != ShowBuffering.Never) require(buffering != null) {
        "buffering should not be null if showBuffering is 'ShowBuffering.$showBuffering'"
    }

    var contentAspectRatio by remember { mutableStateOf(0f) }
    val currentAspectRatioSetter: (Float) -> Unit = { viewAspectRatio ->
        val aspectDeformation: Float = viewAspectRatio / contentAspectRatio - 1f
        if (aspectDeformation.absoluteValue > 0.01f) {
            // Not within the allowed tolerance, populate the new viewAspectRatio.
            contentAspectRatio = viewAspectRatio
        }
    }

    var textureViewRotation by remember(surfaceType) { mutableStateOf(0) }

    // shutter
    var closeShutter by remember { mutableStateOf(true) }
    val playerState = state.playerState
    key(playerState) {
        var isNewPlayer by remember { mutableStateOf(true) }
        val tracksInfo = playerState?.tracksInfo
        DisposableEffect(tracksInfo, keepContentOnPlayerReset) {
            if (playerState == null || tracksInfo?.trackGroupInfos.isNullOrEmpty()) {
                if (!keepContentOnPlayerReset) {
                    closeShutter = true
                }
            } else if (
                (isNewPlayer && !keepContentOnPlayerReset)
                || tracksInfo?.isTypeSelected(C.TRACK_TYPE_VIDEO) != true
            ) {
                closeShutter = true
            }
            onDispose {}
        }
        isNewPlayer = false
    }

    if (playerState != null) {
        DisposableEffect(playerState) {
            val listener = object : Player.Listener {
                override fun onVideoSizeChanged(videoSize: VideoSize) {
                    var videoAspectRatio = if (videoSize.height == 0) 0f
                    else videoSize.width * videoSize.pixelWidthHeightRatio / videoSize.height

                    if (surfaceType == SurfaceType.TextureView) {
                        val unappliedRotationDegrees = videoSize.unappliedRotationDegrees
                        // Try to apply rotation transformation when our surface is a TextureView.
                        if (videoAspectRatio > 0
                            && (unappliedRotationDegrees == 90 || unappliedRotationDegrees == 270)
                        ) {
                            // We will apply a rotation 90/270 degree to the output texture of the TextureView.
                            // In this case, the output video's width and height will be swapped.
                            videoAspectRatio = 1 / videoAspectRatio
                        }
                        textureViewRotation = videoSize.unappliedRotationDegrees
                    }

                    currentAspectRatioSetter(videoAspectRatio)
                }

                override fun onRenderedFirstFrame() {
                    closeShutter = false
                }

                override fun onEvents(player: Player, events: Player.Events) {
                    if (events.containsAny(
                            Player.EVENT_PLAYBACK_STATE_CHANGED,
                            Player.EVENT_PLAY_WHEN_READY_CHANGED
                        )
                    ) {
                        if (state.controllerState.autoShow) {
                            state.controllerState.maybeShow()
                        }
                    }
                }
            }
            playerState.player.addListener(listener)
            onDispose {
                playerState.player.removeListener(listener)
            }
        }
    }

    Box(
        modifier = modifier
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                if (controller != null && playerState != null) {
                    state.controllerState.toggleVisibility()
                }
            }
    ) {
        // video
        VideoSurface(
            player = playerState?.player,
            surfaceType = surfaceType,
            modifier = Modifier
                .align(Alignment.Center)
                .run {
                    if (contentAspectRatio <= 0) fillMaxSize()
                    else resize(contentAspectRatio, resizeMode)
                }
                .drawWithContent {
                    drawContent()
                    if (closeShutter) drawRect(shutterColor)
                },
            textureViewRotation,
        )

        // artwork in audio stream
        val hideArtwork by remember(playerState, useArtwork, keepContentOnPlayerReset) {
            derivedStateOf {
                !useArtwork
                        ||
                        (playerState?.tracksInfo?.run {
                            (trackGroupInfos.isEmpty() && !keepContentOnPlayerReset)
                                    || isTypeSelected(C.TRACK_TYPE_VIDEO)
                        } ?: true)
            }
        }
        if (!hideArtwork) {
            val metadataArtworkData = playerState?.mediaMetadata?.artworkData
            val metadataArtworkPainter by remember(metadataArtworkData) {
                lazy {
                    metadataArtworkData?.run {
                        BitmapPainter(BitmapFactory.decodeByteArray(this, 0, size).asImageBitmap())
                    }
                }
            }
            val artworkPainter: Painter? = metadataArtworkPainter ?: defaultArtworkPainter
            if (artworkPainter != null) {
                Image(
                    painter = artworkPainter,
                    contentDescription = "",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = when (resizeMode) {
                        ResizeMode.Fit -> ContentScale.Fit
                        ResizeMode.FixedWidth -> ContentScale.FillWidth
                        ResizeMode.FixedHeight -> ContentScale.FillHeight
                        ResizeMode.Fill -> ContentScale.FillBounds
                        ResizeMode.Zoom -> ContentScale.Crop
                    }
                )
                currentAspectRatioSetter(
                    artworkPainter.intrinsicSize.run { if (height == 0f) 0f else width / height }
                )
            }
        }

        // subtitles
        val cues = playerState?.cues.takeIf { !it.isNullOrEmpty() } ?: emptyList()
        subtitles?.invoke(cues)

        // buffering
        val isBufferingShowing by remember(playerState, showBuffering) {
            derivedStateOf {
                playerState?.run {
                    playbackState == Player.STATE_BUFFERING
                            && (showBuffering == ShowBuffering.Always
                            || (showBuffering == ShowBuffering.WhenPlaying && playWhenReady))
                } ?: false
            }
        }
        if (isBufferingShowing) buffering?.invoke()

        // error message
        playerState?.playerError?.run {
            errorMessage?.invoke(this)
        }

        // overlay
        overlay?.invoke()

        // controller
        DisposableEffect(playerState) {
            if (playerState == null) {
                state.controllerState.visibility = ControllerVisibility.Invisible
            } else if (controller != null) {
                state.controllerState.maybeShow()
            }
            onDispose {}
        }
        if (controller != null) controller(state)
    }
}

private fun Modifier.resize(
    aspectRatio: Float,
    resizeMode: ResizeMode
) = when (resizeMode) {
    ResizeMode.Fit -> aspectRatio(aspectRatio)
    ResizeMode.Fill -> fillMaxSize()
    ResizeMode.FixedWidth -> fixedWidth(aspectRatio)
    ResizeMode.FixedHeight -> fixedHeight(aspectRatio)
    ResizeMode.Zoom -> zoom(aspectRatio)
}

private fun Modifier.fixedWidth(
    aspectRatio: Float
) = clipToBounds()
    .fillMaxWidth()
    .wrapContentHeight(unbounded = true)
    .aspectRatio(aspectRatio)

private fun Modifier.fixedHeight(
    aspectRatio: Float
) = clipToBounds()
    .fillMaxHeight()
    .wrapContentWidth(unbounded = true)
    .aspectRatio(aspectRatio)

private fun Modifier.zoom(
    aspectRatio: Float
) = clipToBounds()
    .layout { measurable, constraints ->
        val maxWidth = constraints.maxWidth
        val maxHeight = constraints.maxHeight
        if (aspectRatio > maxWidth.toFloat() / maxHeight) {
            // wrap width unbounded
            val modifiedConstraints = constraints.copy(maxWidth = Constraints.Infinity)
            val placeable = measurable.measure(modifiedConstraints)
            layout(constraints.maxWidth, placeable.height) {
                val offsetX = Alignment.CenterHorizontally
                    .align(0, constraints.maxWidth - placeable.width, layoutDirection)
                placeable.place(IntOffset(offsetX, 0))
            }
        } else {
            // wrap height unbounded
            val modifiedConstraints = constraints.copy(maxHeight = Constraints.Infinity)
            val placeable = measurable.measure(modifiedConstraints)
            layout(placeable.width, constraints.maxHeight) {
                val offsetY = Alignment.CenterVertically
                    .align(0, constraints.maxHeight - placeable.height)
                placeable.place(IntOffset(0, offsetY))
            }
        }
    }
    .aspectRatio(aspectRatio)

@Composable
private fun VideoSurface(
    player: Player?,
    surfaceType: SurfaceType,
    modifier: Modifier,
    textureViewRotation: Int
) {
    key(surfaceType) {
        AndroidView(
            factory = { context ->
                when (surfaceType) {
                    SurfaceType.None -> View(context)
                    SurfaceType.TextureView -> TextureView(context)
                    SurfaceType.SurfaceView -> SurfaceView(context)
                }
            },
            modifier = modifier,
        ) { surfaceView ->

            // update TextureView rotation
            (surfaceView as? TextureView)?.apply {
                setRotation(textureViewRotation)
            }

            // update player
            val oldPlayer = surfaceView.tag as? Player
            if (oldPlayer === player) return@AndroidView

            oldPlayer?.run {
                when (surfaceType) {
                    SurfaceType.None -> Unit
                    SurfaceType.TextureView -> clearVideoTextureView(surfaceView as TextureView)
                    SurfaceType.SurfaceView -> clearVideoSurfaceView(surfaceView as SurfaceView)
                }
            }

            surfaceView.tag = player?.apply {
                when (surfaceType) {
                    SurfaceType.None -> Unit
                    SurfaceType.TextureView -> setVideoTextureView(surfaceView as TextureView)
                    SurfaceType.SurfaceView -> setVideoSurfaceView(surfaceView as SurfaceView)
                }
            }
        }
    }
}
