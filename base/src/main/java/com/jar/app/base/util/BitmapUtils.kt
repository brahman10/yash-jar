package com.jar.app.base.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.WorkerThread
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BitmapUtils @Inject constructor() {
    @WorkerThread
    suspend fun rotateBitmap(degrees: Float, bitmap: Bitmap): Bitmap {
        var returnBitmap: Bitmap
        try {
            val matrix = Matrix()
            matrix.postRotate(degrees)
            returnBitmap = Bitmap.createBitmap(
                bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true
            )
        } catch (exception: IllegalArgumentException) {
            returnBitmap = bitmap
        }
        return returnBitmap
    }

    @WorkerThread
    suspend fun getBitmapFromPath(path: String): Bitmap? {
        val options = BitmapFactory.Options()
        options.inPreferredConfig = Bitmap.Config.ARGB_8888
        return BitmapFactory.decodeFile(path, options)
    }

    @WorkerThread
    suspend fun decodeBitmapFromPath(path: String): Bitmap? {
        var bitmap: Bitmap? = null
        val options = BitmapFactory.Options()
        options.inPreferredConfig = Bitmap.Config.ARGB_8888
        options.inSampleSize = 2
        do {
            try {
                bitmap = BitmapFactory.decodeFile(path, options)
                break
            } catch (exception: Exception) {
                exception.printStackTrace()
                options.inSampleSize.plus(2)
            } catch (error: OutOfMemoryError) {
                System.gc()
                error.printStackTrace()
                options.inSampleSize.plus(2)
            }
        } while (options.inSampleSize <= 16)
        return bitmap
    }

    @WorkerThread
    suspend fun saveBitmapToFile(bitmap: Bitmap, filePath: String, quality:Int = 100): String? {
        return try {
            withContext(Dispatchers.IO) {
                FileOutputStream(File(filePath)).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, quality, out)
                    out.flush()
                }
                return@withContext filePath
            }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    @WorkerThread
    suspend fun getBitmapFromUri(context: Context, uri: Uri): Bitmap? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, uri))
            } else {
                MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
            }
        } catch (exception: Exception) {
            null
        }
    }

    fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        return outputStream.toByteArray()
    }
}