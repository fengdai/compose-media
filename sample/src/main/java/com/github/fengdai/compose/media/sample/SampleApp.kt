package com.github.fengdai.compose.media.sample

import android.app.Application
import coil.Coil
import coil.ImageLoader
import coil.decode.VideoFrameDecoder
import coil.util.DebugLogger

class SampleApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Coil.setImageLoader {
            ImageLoader.Builder(this)
                .components {
                    add(VideoFrameDecoder.Factory())
                }
                .logger(DebugLogger())
                .build()
        }
    }
}
