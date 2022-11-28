package com.github.fengdai.compose.media.sample

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.systemGestureExclusion
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.github.fengdai.compose.media.TimeBar
import com.github.fengdai.compose.media.TimeBarProgress
import com.github.fengdai.compose.media.TimeBarScrubber

@Composable
fun TimeBars(navController: NavHostController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("TimeBars") },
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
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.Black)
                .padding(vertical = 48.dp),
            verticalArrangement = Arrangement.spacedBy(48.dp)
        ) {
            DefaultTimeBar()
            YouTubeTimeBar()
            DiamondScrubberTimeBar()
            NyanCatTimeBar()
        }
    }
}

@Composable
fun DefaultTimeBar() {
    var position by remember { mutableStateOf(60L) }
    val bufferedPosition by remember { derivedStateOf { (position + 10).coerceAtMost(100) } }
    TimeBar(
        durationMs = 100,
        positionMs = position,
        bufferedPositionMs = bufferedPosition,
        modifier = Modifier
            .systemGestureExclusion()
            .fillMaxWidth()
            .height(52.dp),
        contentPadding = PaddingValues(24.dp),
        scrubberCenterAsAnchor = true,
        onScrubStop = { position = it },
    )
}

@Composable
fun YouTubeTimeBar() {
    var position by remember { mutableStateOf(60L) }
    val bufferedPosition by remember { derivedStateOf { (position + 10).coerceAtMost(100) } }
    TimeBar(
        durationMs = 100,
        positionMs = position,
        bufferedPositionMs = bufferedPosition,
        modifier = Modifier
            .systemGestureExclusion()
            .fillMaxWidth()
            .height(50.dp),
        contentPadding = PaddingValues(vertical = 24.dp),
        onScrubStop = { position = it },
        progress = { current, _, buffered ->
            // YouTube use current progress as played progress
            TimeBarProgress(current, buffered, playedColor = Color.Red)
        }
    ) { enabled, scrubbing ->
        TimeBarScrubber(enabled, scrubbing, draggedSize = 20.dp, color = Color.Red)
    }
}

@Composable
fun DiamondScrubberTimeBar() {
    var position by remember { mutableStateOf(60L) }
    val bufferedPosition by remember { derivedStateOf { (position + 10).coerceAtMost(100) } }
    TimeBar(
        durationMs = 100,
        positionMs = position,
        bufferedPositionMs = bufferedPosition,
        modifier = Modifier
            .systemGestureExclusion()
            .fillMaxWidth()
            .height(52.dp),
        contentPadding = PaddingValues(24.dp),
        scrubberCenterAsAnchor = true,
        onScrubStop = { position = it },
        progress = { _, scrubbed, buffered ->
            TimeBarProgress(scrubbed, buffered, playedColor = MaterialTheme.colorScheme.primary)
        },
        scrubber = { enabled, scrubbing ->
            TimeBarScrubber(
                enabled,
                scrubbing,
                enabledSize = 20.dp,
                draggedSize = 24.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = CutCornerShape(50)
            )
        }
    )
}

val RainbowColors = listOf(
    Color.Red,
    Color(255, 154, 0),
    Color.Yellow,
    Color.Green,
    Color.Blue,
    Color(100, 30, 255)
)

@Composable
fun NyanCatTimeBar() {
    var position by remember { mutableStateOf(60L) }
    val bufferedPosition by remember { derivedStateOf { (position + 10).coerceAtMost(100) } }
    TimeBar(
        durationMs = 100,
        positionMs = position,
        bufferedPositionMs = bufferedPosition,
        modifier = Modifier
            .systemGestureExclusion()
            .fillMaxWidth()
            .height(56.dp),
        contentPadding = PaddingValues(24.dp),
        scrubberCenterAsAnchor = true,
        onScrubStop = { position = it },
        progress = { _, scrubbed, buffered ->
            @Suppress("UnnecessaryVariable")
            val played = scrubbed  // use scrubbed progress as played progress
            val brush = remember {
                Brush.verticalGradient(RainbowColors)
            }
            val normalBarHeightFactor = 0.5f
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val normalBarHeight = size.height * normalBarHeightFactor
                val normalBarOffsetY = (size.height - normalBarHeight) / 2
                var left = 0f
                // draw played
                if (played > 0) {
                    val playedRight = played * width
                    val playedWidth = playedRight - left
                    drawRect(
                        brush,
                        topLeft = Offset(left, 0f),
                        size = size.copy(width = playedWidth)
                    )
                    left = playedRight
                }
                // draw buffered
                if (buffered > played) {
                    val bufferedRight = buffered * width
                    val bufferedWidth = bufferedRight - left
                    drawRect(
                        Color(0xCCFFFFFF),
                        topLeft = Offset(left, normalBarOffsetY),
                        size = size.copy(width = bufferedWidth, height = normalBarHeight)
                    )
                    left = bufferedRight
                }
                // draw unplayed
                if (left < size.width) {
                    drawRect(
                        Color(0x33FFFFFF),
                        topLeft = Offset(left, normalBarOffsetY),
                        size = size.copy(width = size.width - left, height = normalBarHeight)
                    )
                }
            }
        },
        scrubber = { enabled, _ ->
            if (enabled) {
                Image(
                    painter = painterResource(id = R.drawable.nyan_cat),
                    contentDescription = null,
                    Modifier.size(42.dp)
                )
            }
        }
    )
}
