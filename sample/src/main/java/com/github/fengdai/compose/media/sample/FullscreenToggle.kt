package com.github.fengdai.compose.media.sample

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.github.fengdai.compose.media.Media
import com.github.fengdai.compose.media.MediaState
import com.github.fengdai.compose.media.rememberMediaState
import com.github.fengdai.compose.media.rememberPlayerState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSource

@Composable
fun FullscreenToggle(navController: NavHostController) {
    val configuration = LocalConfiguration.current
    val activity = LocalContext.current.findActivity()!!

    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.isStatusBarVisible = !isLandscape
        systemUiController.isNavigationBarVisible = !isLandscape
    }

    val player by rememberManagedExoPlayer { context ->
        setMediaSourceFactory(ProgressiveMediaSource.Factory(DefaultDataSource.Factory(context)))
    }
    DisposableEffect(player) {
        player?.playWhenReady = true
        onDispose {}
    }

    val mediaItem = remember { MediaItem.fromUri(Urls[0]) }
    LaunchedEffect(mediaItem, player) {
        player?.run {
            setMediaItem(mediaItem)
            prepare()
        }
    }

    val state = rememberMediaState()
    if (!isLandscape) {
        // TODO  Workaround for RememberObserver issue.
        // https://kotlinlang.slack.com/archives/CJLTWPH7S/p1653543177516939
        state.playerState = player?.run { rememberPlayerState(player = this) }
        NormalContent(
            state,
            onBackPressed = { navController.popBackStack() },
            enterFullscreen = {
                activity.requestedOrientation =
                    ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE
            }
        )
    } else {
        state.playerState = player?.run { rememberPlayerState(player = this) }
        FullscreenContent(
            state,
            activity.onBackPressedDispatcher,
            exitFullScreen = {
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER
            }
        )
    }
}

@Composable
private fun NormalContent(
    state: MediaState,
    onBackPressed: () -> Unit,
    enterFullscreen: () -> Unit
) {
    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text("Fullscreen Toggle") },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.Filled.ArrowBack, null)
                    }
                }
            )
        },
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding(),
    ) { padding ->
        Box(
            Modifier
                .padding(padding)
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
        ) {
            Media(
                state,
                Modifier.fillMaxSize()
            )
            Button(
                modifier = Modifier.align(Alignment.BottomEnd),
                onClick = enterFullscreen
            ) {
                Text(text = "Enter Fullscreen")
            }
        }
    }
}

@Composable
private fun FullscreenContent(
    state: MediaState,
    onBackPressedDispatcher: OnBackPressedDispatcher,
    exitFullScreen: () -> Unit,
) {
    val currentExitFullScreen by rememberUpdatedState(newValue = exitFullScreen)
    DisposableEffect(onBackPressedDispatcher) {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                currentExitFullScreen()
            }
        }
        onBackPressedDispatcher.addCallback(callback)
        onDispose {
            callback.remove()
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Media(
            state,
            modifier = Modifier.fillMaxSize()
        )
        Button(
            modifier = Modifier.align(Alignment.BottomEnd),
            onClick = exitFullScreen
        ) {
            Text(text = "Exit Fullscreen")
        }
    }
}
