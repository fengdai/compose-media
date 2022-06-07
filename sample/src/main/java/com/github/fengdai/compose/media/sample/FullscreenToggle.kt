package com.github.fengdai.compose.media.sample

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo.*
import android.content.res.Configuration
import androidx.activity.OnBackPressedCallback
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
import com.github.fengdai.compose.media.ShowBuffering
import com.github.fengdai.compose.media.rememberUpdatedMediaState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSource

@Composable
fun FullscreenToggle(navController: NavHostController) {
    val configuration = LocalConfiguration.current
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

    val mediaState = rememberUpdatedMediaState(player)
    val mediaContent = remember {
        // TODO movableContentOf here doesn't avoid Media from recreating its surface view when
        // screen rotation changed. Seems like a bug of Compose.
        // see: https://kotlinlang.slack.com/archives/CJLTWPH7S/p1654734644676989
        movableContentOf { isLandscape: Boolean, modifier: Modifier ->
            MediaContent(mediaState, isLandscape, modifier)
        }
    }
    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text("Fullscreen Toggle") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, null)
                    }
                }
            )
        },
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding(),
    ) { padding ->
        if (!isLandscape) {
            mediaContent(
                false,
                Modifier
                    .padding(padding)
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
            )
        }
    }
    if (isLandscape) {
        mediaContent(
            true,
            Modifier
                .fillMaxSize()
                .background(Color.Black)
        )
    }
}

@Composable
private fun MediaContent(
    mediaState: MediaState,
    isLandscape: Boolean,
    modifier: Modifier = Modifier
) {
    val activity = LocalContext.current.findActivity()!!
    val enterFullscreen = { activity.requestedOrientation = SCREEN_ORIENTATION_USER_LANDSCAPE }
    val exitFullscreen = {
        @SuppressLint("SourceLockedOrientationActivity")
        // Will reset to SCREEN_ORIENTATION_USER later
        activity.requestedOrientation = SCREEN_ORIENTATION_USER_PORTRAIT
    }
    Box(modifier) {
        Media(
            mediaState,
            modifier = Modifier.fillMaxSize(),
            showBuffering = ShowBuffering.Always,
            buffering = {
                Box(Modifier.fillMaxSize(), Alignment.Center) {
                    CircularProgressIndicator()
                }
            },
        )
        Button(
            modifier = Modifier.align(Alignment.BottomEnd),
            onClick = if (isLandscape) exitFullscreen else enterFullscreen
        ) {
            Text(text = if (isLandscape) "Exit Fullscreen" else "Enter Fullscreen")
        }
    }
    val onBackPressedCallback = remember {
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                exitFullscreen()
            }
        }
    }
    val onBackPressedDispatcher = activity.onBackPressedDispatcher
    DisposableEffect(onBackPressedDispatcher) {
        onBackPressedDispatcher.addCallback(onBackPressedCallback)
        onDispose { onBackPressedCallback.remove() }
    }
    SideEffect {
        onBackPressedCallback.isEnabled = isLandscape
        if (isLandscape) {
            if (activity.requestedOrientation == SCREEN_ORIENTATION_USER) {
                activity.requestedOrientation = SCREEN_ORIENTATION_USER_LANDSCAPE
            }
        } else {
            activity.requestedOrientation = SCREEN_ORIENTATION_USER
        }
    }
}
