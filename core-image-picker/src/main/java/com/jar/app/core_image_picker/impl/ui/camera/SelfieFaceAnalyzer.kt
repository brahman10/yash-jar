package com.jar.app.core_image_picker.impl.ui.camera

import android.annotation.SuppressLint
import android.graphics.Rect
import android.graphics.RectF
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.view.PreviewView
import androidx.core.graphics.toRect
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection

internal class SelfieFaceAnalyzer(
    private val cropOverLayRect: RectF,
    private val previewView: PreviewView,
//    private val selfieOverlay: CameraSelfieOverlay,
    private val resultListener: (Boolean, String) -> Unit
) : ImageAnalysis.Analyzer {

    private val isFrontLens = true
    private val detector = FaceDetection.getClient()

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        imageProxy.setCropRect(cropOverLayRect.toRect())
        val mediaImage = imageProxy.image
        if (mediaImage == null) {
            imageProxy.close()
            return
        }
        val rotation = imageProxy.imageInfo.rotationDegrees
        val image = InputImage.fromMediaImage(mediaImage, rotation)
        detector.process(image)
            .addOnSuccessListener { faces ->
                if (faces.size > 0) {
                    val reverseDimens = rotation == 90 || rotation == 270
                    val width = if (reverseDimens) imageProxy.height else imageProxy.width
                    val height = if (reverseDimens) imageProxy.width else imageProxy.height
                    val faceRect = faces[0].boundingBox.transform(width, height)
//                    selfieOverlay.drawRect(faceRect)
                    resultListener.invoke(cropOverLayRect.contains(faceRect), "")
                } else {
                    resultListener.invoke(false, "Face not detected.")
                }
                imageProxy.close()
            }
            .addOnFailureListener { e ->
                imageProxy.close()
                resultListener.invoke(false, "Error while detecting face.")
            }
    }

    private fun Rect.transform(width: Int, height: Int): RectF {
        val scaleX = previewView.width / width.toFloat()
        val scaleY = previewView.height / height.toFloat()

        // If the front camera lens is being used, reverse the right/left coordinates
        val flippedLeft = if (isFrontLens) width - right else left
        val flippedRight = if (isFrontLens) width - left else right

        // Scale all coordinates to match preview
        val scaledLeft = scaleX * flippedLeft
        val scaledTop = scaleY * top
        val scaledRight = scaleX * flippedRight
        val scaledBottom = scaleY * bottom
        return RectF(scaledLeft, scaledTop, scaledRight, scaledBottom)
    }
}