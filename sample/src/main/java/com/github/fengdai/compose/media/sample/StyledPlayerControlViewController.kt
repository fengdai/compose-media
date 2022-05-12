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
    state: MediaState,
    modifier: Modifier = Modifier,
    showTimeoutMs: Int = StyledPlayerControlView.DEFAULT_SHOW_TIMEOUT_MS
) {
    val controllerState = state.controllerState
    AndroidView(
        factory = { context ->
            StyledPlayerControlView(context).apply {
                hideImmediately()
                addVisibilityListener {
                    controllerState.visibility = if (isVisible) {
                        if (isFullyVisible) ControllerVisibility.Visible
                        else ControllerVisibility.PartiallyVisible
                    } else ControllerVisibility.Invisible
                }
            }
        },
        modifier = modifier
    ) {
        it.player = state.player
        it.showTimeoutMs =
            if (controllerState.shouldShowIndefinitely) 0
            else showTimeoutMs

        if (controllerState.visibility == ControllerVisibility.Visible) {
            if (controllerState.visibility != ControllerVisibility.PartiallyVisible) {
                it.show()
            }
        } else if (controllerState.visibility == ControllerVisibility.Invisible) {
            if (it.isVisible) it.hide()
        }
    }
}
