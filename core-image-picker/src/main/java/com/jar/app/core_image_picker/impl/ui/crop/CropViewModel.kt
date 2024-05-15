package com.jar.app.core_image_picker.impl.ui.crop

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.base.util.BitmapUtils
import com.jar.app.base.util.ImageCompressionUtil
import com.jar.app.core_image_picker.impl.util.FilePathUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

@HiltViewModel
internal class CropViewModel @Inject constructor(
    private val compressionUtil: ImageCompressionUtil,
    private val bitmapUtils: BitmapUtils,
    private val filePathUtils: FilePathUtils
) : ViewModel() {

    private val _bitmapLiveData = MutableLiveData<Bitmap?>()
    val bitmapLiveData: LiveData<Bitmap?>
        get() = _bitmapLiveData

    private val _pathLiveData = MutableLiveData<String?>()
    val pathLiveData: LiveData<String?>
        get() = _pathLiveData

    private val _croppedPathLiveData = MutableLiveData<String>()
    val croppedPathLiveData: LiveData<String>
        get() = _croppedPathLiveData

    fun getBitmapFromPath(path: String) {
        viewModelScope.launch {
            val deferred = async(Dispatchers.Default) {
                bitmapUtils.getBitmapFromPath(path)
            }
            _bitmapLiveData.postValue(deferred.await())
        }
    }

    fun getRotationAngle(path: String): Int {
        val exif = ExifInterface(path)
        val orientation = exif.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )
        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90
            ExifInterface.ORIENTATION_ROTATE_180 -> 180
            ExifInterface.ORIENTATION_ROTATE_270 -> 270
            else -> 0
        }
    }

    fun getPathFromUri(uri: Uri, outputDir: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    filePathUtils.getPath(uri)?.let {
                        val file = compressionUtil.compressImageToFile(
                            File(it),
                            File(outputDir)
                        )
                        _pathLiveData.postValue(file?.absolutePath)
                    } ?: run {
                        _pathLiveData.postValue(null)
                    }
                } catch (exception: Exception) {
                    exception.printStackTrace()
                    _pathLiveData.postValue(null)
                }

            }
        }
    }

    fun getBitmapFromUri(context: Context, uri: Uri) {
        viewModelScope.launch {
            val deferred = async(Dispatchers.IO) {
                bitmapUtils.getBitmapFromUri(context, uri)
            }
            _bitmapLiveData.postValue(deferred.await())
        }
    }

    fun saveCroppedImageToFile(bitmap: Bitmap, parentDirectoryPath: String) {
        val fileToSave = createFileToSave(parentDirectoryPath)
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                bitmapUtils.saveBitmapToFile(bitmap, fileToSave.absolutePath)?.let {
                    compressionUtil.compressImageToFile(
                        fileToSave,
                        File(parentDirectoryPath)
                    )?.let {
                        _croppedPathLiveData.postValue(it.absolutePath)
                    }
                }
            }
        }
    }

    private fun createFileToSave(parentDir: String): File {
        val fileToSave = File(
            parentDir,
            "temp_${System.currentTimeMillis()}.png"
        )
        if (!fileToSave.exists()) {
            fileToSave.createNewFile()
        }
        return fileToSave
    }
}