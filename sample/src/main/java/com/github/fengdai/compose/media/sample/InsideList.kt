package com.github.fengdai.compose.media.sample

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.systemGestureExclusion
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.github.fengdai.compose.media.*
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import kotlinx.coroutines.delay

@Composable
fun InsideList(navController: NavHostController) {
    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text("Inside List") },
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
        InsideListContent(Modifier.fillMaxSize(), padding)
    }
}

@Composable
fun InsideListContent(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues
) {
    val mediaItems = remember { Urls.map { MediaItem.Builder().setUri(it).setMediaId(it).build() } }
    val listState = rememberLazyListState()
    val focusedIndex by remember(listState) {
        derivedStateOf {
            val firstVisibleItemIndex = listState.firstVisibleItemIndex
            val firstVisibleItemScrollOffset = listState.firstVisibleItemScrollOffset
            if (firstVisibleItemScrollOffset == 0) {
                firstVisibleItemIndex
            } else if (firstVisibleItemIndex + 1 <= listState.layoutInfo.totalItemsCount - 1) {
                firstVisibleItemIndex + 1
            } else -1
        }
    }
    val mediaContent = remember {
        movableContentOf { mediaItem: MediaItem, onPlay: () -> Unit, modifier: Modifier ->
            MediaContent(mediaItem, onPlay, modifier)
        }
    }
    LazyColumn(
        modifier = modifier,
        state = listState,
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = contentPadding,
    ) {
        itemsIndexed(mediaItems) { index, mediaItem ->
            ListItem(
                mediaItem,
                focused = focusedIndex == index
            ) { modifier, onPlay ->
                mediaContent(mediaItem, onPlay, modifier)
            }
        }
    }
}

@Composable
fun ListItem(
    mediaItem: MediaItem,
    focused: Boolean,
    mediaContent: @Composable (modifier: Modifier, onPlay: () -> Unit) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
        ) {
            var isPlaying by remember(focused) { mutableStateOf(false) }
            if (focused) {
                mediaContent(
                    Modifier
                        .fillMaxSize()
                        .background(Color.Black)
                ) { isPlaying = true }
            }
            if (!isPlaying) {
                AsyncImage(
                    model = mediaItem.localConfiguration!!.uri,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black)
                )
            }
        }
    }
}

@Composable
private fun MediaContent(
    mediaItem: MediaItem,
    onPlay: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentOnPlay by rememberUpdatedState(newValue = onPlay)
    Box(modifier = modifier) {
        val player by rememberManagedExoPlayer()
        val mediaState = rememberMediaState(player = player)

        var playing by remember { mutableStateOf(false) }
        LaunchedEffect(mediaItem, player) {
            playing = false
            player?.run {
                setMediaItem(mediaItem)
                prepare()
            }
        }
        DisposableEffect(player) {
            player?.playWhenReady = true
            val listener = object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    if (isPlaying) {
                        playing = true
                        currentOnPlay()
                    }
                }
            }
            player?.addListener(listener)
            onDispose {
                player?.removeListener(listener)
            }
        }
        Media(state = mediaState)
        if (playing) {
            val controllerState = rememberControllerState(mediaState)
            LaunchedEffect(Unit) {
                while (true) {
                    delay(200)
                    controllerState.triggerPositionUpdate()
                }
            }
            TimeBar(
                controllerState.durationMs,
                controllerState.positionMs,
                controllerState.bufferedPositionMs,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .height(38.dp)
                    .offset(y = (18).dp)
                    .systemGestureExclusion(),
                contentPadding = PaddingValues(vertical = 18.dp),
                progress = { _, scrubbed, buffered ->
                    TimeBarProgress(
                        scrubbed,
                        buffered,
                        playedColor = MaterialTheme.colorScheme.primary
                    )
                },
                onScrubStop = { positionMs ->
                    controllerState.seekTo(positionMs)
                }
            ) { enabled, scrubbing ->
                TimeBarScrubber(
                    enabled,
                    scrubbing,
                    draggedSize = 20.dp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
