package com.github.fengdai.compose.media.sample

import android.content.Context
import android.os.Build
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.android.exoplayer2.ExoPlayer

@Composable
fun rememberManagedExoPlayer(
    block: (ExoPlayer.Builder.(Context) -> Unit)? = null
): State<ExoPlayer?> {
    val currentContext = LocalContext.current
    val exoPlayerManager = remember { ExoPlayerManager(currentContext, block) }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when {
                event == Lifecycle.Event.ON_START && Build.VERSION.SDK_INT > 23 -> exoPlayerManager.initialize()
                event == Lifecycle.Event.ON_RESUME && Build.VERSION.SDK_INT <= 23 -> exoPlayerManager.initialize()
                event == Lifecycle.Event.ON_PAUSE && Build.VERSION.SDK_INT <= 23 -> exoPlayerManager.release()
                event == Lifecycle.Event.ON_STOP && Build.VERSION.SDK_INT > 23 -> exoPlayerManager.release()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
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

    internal fun initialize() {
        if (player.value != null) return
        val builder = ExoPlayer.Builder(context)
        buildBlock?.invoke(builder, context)
        player.value = builder.build()
    }

    internal fun release() {
        player.value?.release()
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
