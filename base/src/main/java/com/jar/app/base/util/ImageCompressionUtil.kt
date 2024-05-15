package com.jar.app.base.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.jar.app.core_base.util.BaseConstants
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import top.zibin.luban.Luban
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageCompressionUtil @Inject constructor(
    @ApplicationContext private val context: Context
) {
    suspend fun compressImage(file: File): Bitmap? = withContext(Dispatchers.IO) {
        try {
            val file = Luban.with(context)
                .load(file)
                .ignoreBy(100)
                .setTargetDir(
                    File(
                        context.externalCacheDir, BaseConstants.COMPRESSED_DIR
                    ).absolutePath
                ).get()[0]
            BitmapFactory.decodeFile(file.absolutePath)
        } catch (ignored: Exception) {
            null
        }
    }

    suspend fun compressImageToFile(
        imageFile: File,
        compressFile: File = File(context.externalCacheDir, BaseConstants.COMPRESSED_DIR)
    ): File? = withContext(Dispatchers.IO) {
        try {
            val file = Luban.with(context)
                .load(imageFile)
                .ignoreBy(100)
                .setTargetDir(
                    compressFile.absolutePath
                ).get()[0]
            file
        } catch (ignored: Exception) {
            ignored.printStackTrace()
            null
        } catch (error: OutOfMemoryError) {
            error.printStackTrace()
            System.gc()
            null
        }
    }
}