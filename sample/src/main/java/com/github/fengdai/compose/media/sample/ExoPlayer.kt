package com.github.fengdai.compose.media.sample

import android.content.Context
import android.os.Build
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.Timeline

@Composable
fun rememberManagedExoPlayer(
    lifecycle: Lifecycle = LocalLifecycleOwner.current.lifecycle,
    buildBlock: (ExoPlayer.Builder.(Context) -> Unit)? = null
): State<ExoPlayer?> {
    val currentContext = LocalContext.current
    val exoPlayerManager = remember(buildBlock) {
        ExoPlayerManager(currentContext, buildBlock)
    }
    DisposableEffect(lifecycle) {
        val observer = LifecycleEventObserver { _, event ->
            when {
                event == Lifecycle.Event.ON_START && Build.VERSION.SDK_INT > 23 -> exoPlayerManager.initialize()
                event == Lifecycle.Event.ON_RESUME && Build.VERSION.SDK_INT <= 23 -> exoPlayerManager.initialize()
                event == Lifecycle.Event.ON_PAUSE && Build.VERSION.SDK_INT <= 23 -> exoPlayerManager.release()
                event == Lifecycle.Event.ON_STOP && Build.VERSION.SDK_INT > 23 -> exoPlayerManager.release()
            }
        }
        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
        }
    }
    return exoPlayerManager.player
}

@Stable
internal class ExoPlayerManager(
    private val context: Context,
    private val buildBlock: (ExoPlayer.Builder.(Context) -> Unit)? = null
) : RememberObserver {
    var player = mutableStateOf<ExoPlayer?>(null)
    private var rememberedMediaItemIdAndPosition: Pair<String, Long>? = null

    internal fun initialize() {
        if (player.value != null) return
        val builder = ExoPlayer.Builder(context)
        buildBlock?.invoke(builder, context)
        player.value = builder.build().also { player ->
            player.addListener(object : Player.Listener {
                override fun onTimelineChanged(timeline: Timeline, reason: Int) {
                    // recover the remembered position if media id matched
                    rememberedMediaItemIdAndPosition
                        ?.let { (id, position) ->
                            if (id == player.currentMediaItem?.mediaId) player.seekTo(position)
                        }
                        ?.also { rememberedMediaItemIdAndPosition = null }
                }
            })
        }
    }

    internal fun release() {
        player.value?.let { player ->
            // remember the current position before release
            player.currentMediaItem?.let { mediaItem ->
                rememberedMediaItemIdAndPosition = mediaItem.mediaId to player.currentPosition
            }
            player.release()
        }
        player.value = null
    }

    override fun onAbandoned() {
        release()
    }

    override fun onForgotten() {
        release()
    }

    override fun onRemembered() {
    }
}
