package com.jar.app.feature_in_app_stories.impl.uitl

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import javax.inject.Inject
import javax.inject.Singleton


class WatermarkUtil constructor(private val context: Context) {

    fun applyWatermarkToImages(originalImageUrl: String, watermarkImageUrl: String, callback: (Bitmap) -> Unit) {
        Glide.with(context)
            .asBitmap()
            .load(originalImageUrl)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(originalBitmap: Bitmap, transition: Transition<in Bitmap>?) {
                    Glide.with(context)
                        .asBitmap()
                        .load(watermarkImageUrl)
                        .into(object : CustomTarget<Bitmap>() {
                            override fun onResourceReady(watermarkBitmap: Bitmap, transition: Transition<in Bitmap>?) {
                                val resultBitmap = overlayWatermark(originalBitmap, watermarkBitmap)
                                callback(resultBitmap)
                            }

                            override fun onLoadCleared(placeholder: Drawable?) {}
                        })
                }

                override fun onLoadCleared(placeholder: Drawable?) {}
            })
    }

    private fun overlayWatermark(originalBitmap: Bitmap, watermarkBitmap: Bitmap): Bitmap {
        val resultBitmap = Bitmap.createBitmap(originalBitmap.width, originalBitmap.height, originalBitmap.config)
        val canvas = Canvas(resultBitmap)

        // Draw the original image
        canvas.drawBitmap(originalBitmap, 0f, 0f, null)

        // Define source rectangle for watermark (covers the entire watermark)
        val sourceRect = Rect(0, 0, watermarkBitmap.width, watermarkBitmap.height)

        // Define destination rectangle for watermark (bottom of the original image and scaled to its width)
        val destRect = Rect(0, originalBitmap.height - (originalBitmap.width * watermarkBitmap.height) / watermarkBitmap.width, originalBitmap.width, originalBitmap.height)

        // Draw the scaled watermark at the bottom
        canvas.drawBitmap(watermarkBitmap, sourceRect, destRect, null)

        return resultBitmap
    }


}