package com.github.fengdai.compose.media.sample

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.media3.common.Player
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource

@Composable
fun rememberManagedExoPlayer(): State<Player?> = rememberManagedPlayer { context ->
    val builder = ExoPlayer.Builder(context)
    builder.setMediaSourceFactory(ProgressiveMediaSource.Factory(DefaultDataSource.Factory(context)))
    builder.build().apply {
        playWhenReady = true
    }
}
