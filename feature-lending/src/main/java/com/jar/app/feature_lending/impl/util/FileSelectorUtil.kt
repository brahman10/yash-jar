package com.jar.app.feature_lending.impl.util

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.provider.OpenableColumns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.lang.ref.WeakReference


class FileSelectorUtil(val contextRef: WeakReference<Context>) {
    suspend fun getSizeAndNameFromUri(uri: Uri): Triple<String, String,Long> =
        withContext(Dispatchers.IO) {
            var size: String = ""
            var name: String = "Bank_Statement_Pdf"
            var sizeInByte: Long = 0L

            val uriString = uri.toString()
            val myFile = File(uriString)

            if (uriString.startsWith("content://")) {
                val cursor: Cursor? = contextRef.get()?.contentResolver?.query(uri, null, null, null, null)
                cursor?.use {
                    val sizeIndex = it.getColumnIndex(OpenableColumns.SIZE)
                    val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)

                    if (it.moveToFirst()) {
                        sizeInByte = it.getLong(sizeIndex)
                        size = formatFileSize(sizeInByte)
                        name = it.getString(nameIndex) ?: name
                    }
                }
            } else if (uriString.startsWith("file://")) {
                name = myFile.name
            }

            Triple(size, name, sizeInByte)
        }

    fun formatFileSize(fileSize: Long): String {
        val unit = 1024
        if (fileSize < unit) {
            return "$fileSize B"
        }
        val exp = (Math.log(fileSize.toDouble()) / Math.log(unit.toDouble())).toInt()
        val pre = "KMGTPE"[exp - 1] + "B"
        return String.format("%.1f %s", fileSize / Math.pow(unit.toDouble(), exp.toDouble()), pre)
    }

    fun getFileFromUri(fileUri: Uri): File? {
        var file: File? = null
        if (fileUri.scheme == "content") {
            val contentResolver: ContentResolver? = contextRef.get()?.getContentResolver()
            var outputStream: FileOutputStream? = null
            file = createTempFile() // Create a temporary file to store the content
            outputStream = FileOutputStream(file)
            try {
                contentResolver?.openInputStream(fileUri).use {
                    if (it != null) {
                        val buffer = ByteArray(4 * 1024) // Adjust buffer size as per your needs
                        var bytesRead: Int
                        while (it.read(buffer).also { bytesRead = it } != -1) {
                            outputStream.write(buffer, 0, bytesRead)
                        }
                        return file
                    }
                }

            } catch (e: IOException) {
                // Handle file access error
                e.printStackTrace()
            } finally {
                try {
                    outputStream?.close()
                } catch (e: IOException) {
                    // Handle stream close error
                    e.printStackTrace()
                }
            }
        } else if (fileUri.scheme == "file") {
            file = fileUri.path?.let { File(it) }
        }
        return file
    }

    fun saveFileToDownloads(file: File, filename: String) {
        // Get the Downloads directory
        val downloadsDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

        // Create a new file in the Downloads directory
        val outputFile = File(downloadsDir, filename)
        try {
            val inputStream = FileInputStream(file)
            val outputStream = FileOutputStream(outputFile)
            val buffer = ByteArray(4 * 1024) // Adjust buffer size as per your needs
            var bytesRead: Int
            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                outputStream.write(buffer, 0, bytesRead)
            }

            // Close the streams
            inputStream.close()
            outputStream.close()

            // File saved successfully
            // You can perform any additional operations here

        } catch (e: IOException) {
            // Handle file write error
            e.printStackTrace()

        }
    }

    @Throws(IOException::class)
    fun getBytes(uri: Uri): ByteArray? {
        return contextRef.get()?.contentResolver?.openInputStream(uri)?.use {
            it.buffered().readBytes()
        }


    }
}