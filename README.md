# Media player UI for Jetpack Compose

A library which provides a `Media` composable component for [AndroidX Media3][media3] (the successor
of the [ExoPlayer][exoplayer]) media playbacks.

![basic](/arts/basic.png)

```Kotlin
val state = rememberMediaState(player = player)
Media(
    state = state,
    // following parameters are optional
    modifier = Modifier.fillMaxSize().background(Color.Black),
    surfaceType = SurfaceType.SurfaceView,
    resizeMode = ResizeMode.Fit,
    keepContentOnPlayerReset = false,
    useArtwork = true,
    showBuffering = ShowBuffering.Always,
    buffering = {
        Box(Modifier.fillMaxSize(), Alignment.Center) {
            CircularProgressIndicator()
        }
    }
) { state ->
    SimpleController(state, Modifier.fillMaxSize())
}
```

# Download

```groovy
repositories {
    mavenCentral()
}

dependencies {
    implementation "com.github.fengdai.compose:media:<version>"
}
```

Snapshots of the development version are available in [Sonatype's `snapshots` repository][snapshot].

# License

    Copyright 2022 Feng Dai

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

[media3]: https://github.com/androidx/media

[exoplayer]: https://github.com/google/ExoPlayer/

[snapshot]: https://oss.sonatype.org/content/repositories/snapshots/com/github/fengdai/compose/media/
