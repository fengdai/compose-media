package com.github.fengdai.compose.media

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.github.fengdai.compose.media.test.R
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.Format
import com.google.android.exoplayer2.TracksInfo
import com.google.android.exoplayer2.source.TrackGroup
import java.io.ByteArrayOutputStream

val TestTrackInfo_Audio = TracksInfo(
    listOf(
        TracksInfo.TrackGroupInfo(
            TrackGroup(Format.Builder().build()),
            intArrayOf(C.FORMAT_HANDLED),
            C.TRACK_TYPE_AUDIO,
            booleanArrayOf(true)
        )
    )
)

val TestTrackInfo_Video = TracksInfo(
    listOf(
        TracksInfo.TrackGroupInfo(
            TrackGroup(Format.Builder().build()),
            intArrayOf(C.FORMAT_HANDLED),
            C.TRACK_TYPE_VIDEO,
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
