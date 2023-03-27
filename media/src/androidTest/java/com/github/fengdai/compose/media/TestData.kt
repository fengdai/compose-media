package com.github.fengdai.compose.media

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.media3.common.C
import androidx.media3.common.Format
import androidx.media3.common.TrackGroup
import androidx.media3.common.Tracks
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.github.fengdai.compose.media.test.R
import java.io.ByteArrayOutputStream

val TestTracks_Audio = Tracks(
    listOf(
        Tracks.Group(
            TrackGroup(Format.Builder().setSampleMimeType("audio/mp3").build()),
            false,
            intArrayOf(C.FORMAT_HANDLED),
            booleanArrayOf(true)
        )
    )
)

val TestTracks_Video = Tracks(
    listOf(
        Tracks.Group(
            TrackGroup(Format.Builder().setSampleMimeType("video/mp4").build()),
            false,
            intArrayOf(C.FORMAT_HANDLED),
            booleanArrayOf(true)
        )
    )
)

fun <A : ComponentActivity>
        AndroidComposeTestRule<ActivityScenarioRule<A>, A>.createTestArtworkData(): ByteArray {
    return (activity.getDrawable(R.drawable.artwork) as BitmapDrawable).bitmap
        .run {
            val stream = ByteArrayOutputStream()
            compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.toByteArray()
        }
}
