package com.github.fengdai.compose.media.sample

import android.view.LayoutInflater
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView
import com.github.fengdai.compose.media.SurfaceType
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.StyledPlayerView

@Composable
fun StyledPlayerViewSample() {
    var setPlayer by rememberSaveable { mutableStateOf(true) }
    var url by rememberSaveable { mutableStateOf(Urls[0]) }

    val player by rememberManagedExoPlayer()
    val mediaItem = remember(url) { MediaItem.fromUri(url) }
    LaunchedEffect(mediaItem, player) {
        player?.run {
            setMediaItem(mediaItem)
            prepare()
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        StyledPlayerViewMedia(
            player.takeIf { setPlayer },
            modifier = Modifier
                .aspectRatio(1f)
                .background(Color.Black)
        )
        BooleanOption("Set Player", setPlayer) { setPlayer = it }
        Option("Url", Urls, url) { url = it }
    }
}

@Composable
private fun StyledPlayerViewMedia(
    player: Player?,
    modifier: Modifier = Modifier,
    surfaceType: SurfaceType = SurfaceType.TextureView
) {
    Box(modifier) {
        key(surfaceType) {
            AndroidView(
                factory = { context ->
                    LayoutInflater.from(context)
                        .inflate(R.layout.styled_player_view, null) as StyledPlayerView
                },
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxSize()
            ) {
                it.player = player
            }
        }
    }
}
