package com.jar.app.core_utils.data

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.core.content.FileProvider
import com.jar.app.core_base.util.BaseConstants
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FileUtils @Inject constructor(@ApplicationContext private val context: Context) {


    private val fileWriteMutex = Mutex()
    companion object {
        private const val SOME_ERROR_OCCURRED = "Some Error Occurred"
    }

    suspend fun copyDrawable(drawable: Int, filename: String): File? =
        withContext(Dispatchers.IO) {
            try {
                val bitmap = BitmapFactory.decodeResource(context.resources, drawable)
                val parent = File(context.externalCacheDir, BaseConstants.CACHE_DIR_SHARED)
                parent.mkdirs()
                val file =
                    File(parent, "$filename.png")
                if (file.exists().not()) {
                    val outputStream = FileOutputStream(file)
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                }
                file
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

    suspend fun copyBitmap(bitmap: Bitmap, filename: String): File? =
        withContext(Dispatchers.IO) {
            try {
                val parent = File(context.externalCacheDir, BaseConstants.CACHE_DIR_SHARED)
                parent.mkdirs()
                val file =
                    File(parent, "$filename.png")
                if (file.exists().not()) {
                    val outputStream = FileOutputStream(file)
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                }
                file
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    fun shareImage(context: Context, file: File, extraText: String) {
        try {
            val authority =
                "${context.applicationContext.packageName}${BaseConstants.FILE_PROVIDER_AUTHORITY}"
            FileProvider.getUriForFile(context, authority, file)?.let {
                val shareIntent = Intent()
                    .setAction(Intent.ACTION_SEND)
                    .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
                    .setDataAndType(it, context.contentResolver.getType(it))
                    .putExtra(Intent.EXTRA_STREAM, it)
                    .putExtra(Intent.EXTRA_TEXT, extraText)

                context.startActivity(Intent.createChooser(shareIntent, BaseConstants.SHARE))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context.applicationContext, SOME_ERROR_OCCURRED, Toast.LENGTH_LONG)
                .show()
        }
    }

    fun shareText(text: String, title: String) {
        try {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = BaseConstants.TEXT_PLAIN
            intent.putExtra(Intent.EXTRA_TEXT, text)
            context.startActivity(Intent.createChooser(intent, title).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context.applicationContext, SOME_ERROR_OCCURRED, Toast.LENGTH_LONG)
                .show()
        }
    }

    suspend fun storeContentToFile(content: String, filename: String = "api_response.json") {
        withContext(Dispatchers.IO) {
            fileWriteMutex.withLock {
                val file = File(context.filesDir, filename)
                file.writeText(content)
            }
        }
    }


    suspend fun restoreContentFromFile(filename: String = "api_response.json"): String? =
        withContext(Dispatchers.IO) {
            val file = File(context.filesDir, filename)
             if (file.exists()) {
                val jsonString = file.readText()
                jsonString
            } else {
                null
            }
        }

}