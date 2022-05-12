package com.github.fengdai.compose.media

import android.os.Bundle
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import com.google.android.exoplayer2.Bundleable

/**
 * Create a [Saver] for [Bundleable].
 */
@Suppress("FunctionName")
fun <T : Bundleable> Bundleable.Creator<T>.Saver(): Saver<T, Bundle> = BundleableSaver(this)

private class BundleableSaver<T : Bundleable>(
    private val creator: Bundleable.Creator<T>
) : Saver<T, Bundle> {

    override fun restore(value: Bundle): T {
        return creator.fromBundle(value)
    }

    override fun SaverScope.save(value: T): Bundle {
        return value.toBundle()
    }
}
