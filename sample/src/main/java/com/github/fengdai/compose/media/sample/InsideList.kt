package com.github.fengdai.compose.media.sample

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.github.fengdai.compose.media.Media
import com.github.fengdai.compose.media.rememberUpdatedMediaState
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSource

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
    val mediaItems = remember { Urls.map { MediaItem.fromUri(it) } }
    val player by rememberManagedExoPlayer { context ->
        setMediaSourceFactory(ProgressiveMediaSource.Factory(DefaultDataSource.Factory(context)))
    }
    DisposableEffect(player) {
        player?.playWhenReady = true
        onDispose {}
    }

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
    LazyColumn(
        modifier = modifier,
        state = listState,
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = contentPadding,
    ) {
        itemsIndexed(mediaItems) { index, mediaItem ->
            ListItem(
                showVideo = focusedIndex == index
            ) {
                LaunchedEffect(mediaItem, player) {
                    player?.run {
                        setMediaItem(mediaItem)
                        prepare()
                    }
                }
                Media(
                    state = rememberUpdatedMediaState(player = player),
                    modifier = Modifier
                        .matchParentSize()
                        .background(Color.Black)
                )
            }
        }
    }
}

@Composable
fun ListItem(
    showVideo: Boolean,
    video: @Composable BoxScope.() -> Unit
) {
    Card(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 0.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(modifier = Modifier.aspectRatio(1f)) {
            if (showVideo) {
                video()
            }
        }
    }
}
