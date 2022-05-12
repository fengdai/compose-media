package com.github.fengdai.compose.media

import android.content.Context
import android.graphics.Matrix
import android.graphics.RectF
import android.view.View.OnLayoutChangeListener

internal class TextureView(context: Context) : android.view.TextureView(context) {
    private val layoutChangeListener = OnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
        applyRotation(rotationToApply)
    }
    private var rotationToApply = 0

    fun setRotation(rotation: Int) {
        if (rotationToApply != 0) {
            removeOnLayoutChangeListener(layoutChangeListener)
        }
        rotationToApply = rotation
        if (rotation != 0) {
            // The texture view's dimensions might be changed after layout step.
            // So add an OnLayoutChangeListener to apply rotation after layout step.
            addOnLayoutChangeListener(layoutChangeListener)
        }
        applyRotation(rotationToApply)
    }

    private fun applyRotation(rotation: Int) {
        val transformMatrix = Matrix()
        val textureViewWidth = width.toFloat()
        val textureViewHeight = height.toFloat()
        if (textureViewWidth != 0f && textureViewHeight != 0f && rotation != 0) {
            val pivotX = textureViewWidth / 2
            val pivotY = textureViewHeight / 2
            transformMatrix.postRotate(rotation.toFloat(), pivotX, pivotY)

            // After rotation, scale the rotated texture to fit the TextureView size.
            val originalTextureRect = RectF(0f, 0f, textureViewWidth, textureViewHeight)
            val rotatedTextureRect = RectF()
            transformMatrix.mapRect(rotatedTextureRect, originalTextureRect)
            transformMatrix.postScale(
                textureViewWidth / rotatedTextureRect.width(),
                textureViewHeight / rotatedTextureRect.height(),
                pivotX,
                pivotY
            )
        }
        setTransform(transformMatrix)
    }
}
