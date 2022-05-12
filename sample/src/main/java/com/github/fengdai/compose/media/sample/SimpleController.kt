package com.github.fengdai.compose.media.sample

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.github.fengdai.compose.media.MediaState
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.Player
import kotlinx.coroutines.delay

/**
 * A simple controller, which consists of a play/pause button.
 */
@Composable
fun SimpleController(
    state: MediaState,
    modifier: Modifier = Modifier,
) {
    val controllerState = state.controllerState
    Crossfade(targetState = controllerState.isShowing) { isShowing ->
        if (isShowing) {
            val hideWhenTimeout = !state.controllerState.shouldShowIndefinitely
            var hideEffectReset by remember { mutableStateOf(0) }
            LaunchedEffect(hideWhenTimeout, hideEffectReset) {
                if (hideWhenTimeout) {
                    // hide after 3s
                    delay(3000)
                    controllerState.isShowing = false
                }
            }
            Box(
                modifier = modifier.background(Color(0x98000000)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(
                        if (controllerState.showPause) R.drawable.pause
                        else R.drawable.play
                    ),
                    contentDescription = "",
                    modifier = Modifier
                        .size(52.dp)
                        .clickable {
                            state.maybeEnterPlayerScope {
                                hideEffectReset++
                                if (controllerState.showPause) player.pause()
                                else {
                                    if (playbackState == Player.STATE_IDLE) {
                                        player.prepare()
                                    } else if (playbackState == Player.STATE_ENDED) {
                                        player.seekTo(
                                            player.currentMediaItemIndex,
                                            C.TIME_UNSET
                                        )
                                    }
                                    player.play()
                                }
                            }
                        },
                    colorFilter = ColorFilter.tint(Color.White)
                )
            }
        }
    }
}
