package com.github.fengdai.compose.media.sample

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.github.fengdai.compose.media.*
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSource

val SurfaceTypes = SurfaceType.values().toList()
val ResizeModes = ResizeMode.values().toList()
val ShowBufferingValues = ShowBuffering.values().toList()
val Urls = listOf(
    "https://storage.googleapis.com/downloads.webmproject.org/av1/exoplayer/bbb-av1-480p.mp4",
    "https://storage.googleapis.com/exoplayer-test-media-0/play.mp3",
    "https://storage.googleapis.com/exoplayer-test-media-1/mkv/android-screens-lavf-56.36.100-aac-avc-main-1280x720.mkv",
    "https://storage.googleapis.com/exoplayer-test-media-1/mp4/frame-counter-one-hour.mp4",
    "https://html5demos.com/assets/dizzy.mp4",
)

private enum class ControllerType {
    None, Simple, StyledPlayerControlView
}

private val ControllerTypes = ControllerType.values().toList()

@Composable
fun Basic(navController: NavHostController) {
    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text("Basic") },
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
        BasicContent(
            Modifier
                .fillMaxSize()
                .padding(padding)
        )
    }
}

@Composable
fun BasicContent(
    modifier: Modifier = Modifier
) {
    var url by rememberSaveable { mutableStateOf(Urls[0]) }

    var surfaceType by rememberSaveable { mutableStateOf(SurfaceType.SurfaceView) }
    var resizeMode by rememberSaveable { mutableStateOf(ResizeMode.Fit) }
    var keepContentOnPlayerReset by rememberSaveable { mutableStateOf(false) }
    var useArtwork by rememberSaveable { mutableStateOf(true) }
    var showBuffering by rememberSaveable { mutableStateOf(ShowBuffering.Always) }

    var setPlayer by rememberSaveable { mutableStateOf(true) }
    var playWhenReady by rememberSaveable { mutableStateOf(true) }

    var controllerType by rememberSaveable { mutableStateOf(ControllerType.Simple) }

    var rememberedMediaItemIdAndPosition: Pair<String, Long>? by remember { mutableStateOf(null) }
    val player by rememberManagedExoPlayer { context ->
        setMediaSourceFactory(ProgressiveMediaSource.Factory(DefaultDataSource.Factory(context)))
    }
    DisposableEffect(player, playWhenReady) {
        player?.playWhenReady = playWhenReady
        onDispose {}
    }

    val mediaItem = remember(url) { MediaItem.Builder().setMediaId(url).setUri(url).build() }
    DisposableEffect(mediaItem, player) {
        player?.run {
            setMediaItem(mediaItem)
            rememberedMediaItemIdAndPosition?.let { (id, position) ->
                if (id == mediaItem.mediaId) seekTo(position)
            }?.also { rememberedMediaItemIdAndPosition = null }
            prepare()
        }
        onDispose {}
    }

    val state = rememberUpdatedMediaState(player = player.takeIf { setPlayer })
    val content = remember {
        movableContentOf {
            Media(
                state,
                modifier = Modifier
                    .aspectRatio(16f / 9f)
                    .background(Color.Black),
                surfaceType = surfaceType,
                resizeMode = resizeMode,
                keepContentOnPlayerReset = keepContentOnPlayerReset,
                useArtwork = useArtwork,
                showBuffering = showBuffering,
                buffering = {
                    Box(Modifier.fillMaxSize(), Alignment.Center) {
                        CircularProgressIndicator()
                    }
                },
                errorMessage = { error ->
                    Box(Modifier.fillMaxSize(), Alignment.Center) {
                        Text(
                            error.message ?: "",
                            modifier = Modifier
                                .background(Color(0x80808080), RoundedCornerShape(16.dp))
                                .padding(horizontal = 12.dp, vertical = 4.dp),
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                    }
                },
                controller = when (controllerType) {
                    ControllerType.None -> null
                    ControllerType.Simple -> @Composable { state ->
                        SimpleController(state, Modifier.fillMaxSize())
                    }
                    ControllerType.StyledPlayerControlView -> @Composable { state ->
                        StyledPlayerControlViewController(state, Modifier.fillMaxSize())
                    }
                }
            )
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Option("Url", Urls, url) { url = it }
                Option("Surface Type", SurfaceTypes, surfaceType) { surfaceType = it }
                Option("Resize Mode", ResizeModes, resizeMode) { resizeMode = it }
                BooleanOption(
                    "Keep Content On Player Reset",
                    keepContentOnPlayerReset
                ) { keepContentOnPlayerReset = it }
                BooleanOption("Use Artwork", useArtwork) { useArtwork = it }
                Option("Show Buffering", ShowBufferingValues, showBuffering) { showBuffering = it }
                BooleanOption("Set Player", setPlayer) { setPlayer = it }
                Column(Modifier.padding(start = 18.dp)) {
                    BooleanOption("Play When Ready", playWhenReady) { playWhenReady = it }
                }
                Option("Controller", ControllerTypes, controllerType) { controllerType = it }
                Column(Modifier.padding(start = 18.dp)) {
                    val enabled = controllerType != ControllerType.None
                    var hideOnTouch by rememberSaveable { mutableStateOf(true) }
                    var autoShow by rememberSaveable { mutableStateOf(true) }
                    state.controllerHideOnTouch = hideOnTouch
                    state.controllerAutoShow = autoShow
                    BooleanOption("Hide On Touch", hideOnTouch, enabled) { hideOnTouch = it }
                    BooleanOption("Auto Show", autoShow, enabled) { autoShow = it }
                }
            }
        }
    }

    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
    if (!isLandscape) Column(modifier) { content() } else Row(modifier) { content() }
}
