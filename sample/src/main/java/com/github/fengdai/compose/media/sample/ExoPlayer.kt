package com.github.fengdai.compose.media.sample

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSource

@Composable
fun rememberManagedExoPlayer(): State<Player?> = rememberManagedPlayer { context ->
    val builder = ExoPlayer.Builder(context)
    builder.setMediaSourceFactory(ProgressiveMediaSource.Factory(DefaultDataSource.Factory(context)))
    builder.build().apply {
        playWhenReady = true
    }
}
