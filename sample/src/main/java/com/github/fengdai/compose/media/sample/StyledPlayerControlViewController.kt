package com.github.fengdai.compose.media.sample

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.github.fengdai.compose.media.ControllerVisibility
import com.github.fengdai.compose.media.MediaState
import com.google.android.exoplayer2.ui.StyledPlayerControlView

/**
 * Composable component which leverages [StyledPlayerControlView] to provider a controller.
 */
@Composable
fun StyledPlayerControlViewController(
    mediaState: MediaState,
    modifier: Modifier = Modifier,
    showTimeoutMs: Int = StyledPlayerControlView.DEFAULT_SHOW_TIMEOUT_MS
) {
    AndroidView(
        factory = { context ->
            StyledPlayerControlView(context).apply {
                hideImmediately()
                addVisibilityListener {
                    mediaState.controllerVisibility = if (isVisible) {
                        if (isFullyVisible) ControllerVisibility.Visible
                        else ControllerVisibility.PartiallyVisible
                    } else ControllerVisibility.Invisible
                }
            }
        },
        modifier = modifier
    ) {
        it.player = mediaState.playerState?.player
        it.showTimeoutMs =
            if (mediaState.shouldShowControllerIndefinitely) 0
            else showTimeoutMs

        if (mediaState.controllerVisibility == ControllerVisibility.Visible) {
            if (mediaState.controllerVisibility != ControllerVisibility.PartiallyVisible) {
                it.show()
            }
        } else if (mediaState.controllerVisibility == ControllerVisibility.Invisible) {
            if (it.isVisible) it.hide()
        }
    }
}
