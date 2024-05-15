package com.jar.app.core_image_picker.impl.ui.camera

import android.net.Uri
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.base.util.ImageCompressionUtil
import com.jar.app.core_image_picker.impl.util.FilePathUtils
import com.jar.app.base.data.livedata.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executor
import javax.inject.Inject

@HiltViewModel
internal class CameraViewModel @Inject constructor(
    private val compressionUtil: ImageCompressionUtil,
    private val filePathUtils: FilePathUtils
) : ViewModel() {

    private val _captureLiveData = SingleLiveEvent<String>()
    val captureLiveData: LiveData<String>
        get() = _captureLiveData

    private val _gallerySelectedPathLiveData = SingleLiveEvent<String?>()
    val gallerySelectedPathLiveData: LiveData<String?>
        get() = _gallerySelectedPathLiveData

    fun capturePhoto(
        imageCapture: ImageCapture,
        outputDirectoryPath: String,
        callbackExecutor: Executor,
        shouldReversedHorizontal: Boolean = false
    ) {
        val photoFile = getFormattedFile(outputDirectoryPath)
        val outputOption = ImageCapture.OutputFileOptions
            .Builder(photoFile)
            .build()
        imageCapture.takePicture(
            outputOption,
            callbackExecutor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exception: ImageCaptureException) {
                    exception.printStackTrace()
                    val msg = "Photo capture failed: ${exception.message}"
                    exception.message?.let {

                    }
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    compressImage(photoFile, outputDirectoryPath)
                }
            })
    }

    fun compressImage(capturedFile: File, outputDirectoryPath: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                compressionUtil.compressImageToFile(
                    capturedFile,
                    File(outputDirectoryPath)
                )?.let { file ->
                    _captureLiveData.postValue(file.absolutePath)
                }
            }
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

    private fun getFormattedFile(outputDirectoryPath: String): File {
        val parentDirectory = File(outputDirectoryPath).also {
            if (it.exists())
                it.deleteRecursively()
            it.mkdirs()
        }
        return File(
            parentDirectory,
            SimpleDateFormat(
                "yyyy-MM-dd-HH:mm:ss", Locale.getDefault()
            ).format(System.currentTimeMillis()) + ".jpg"
        )
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
                        _gallerySelectedPathLiveData.postValue(file?.absolutePath)
                    } ?: run {
                        _gallerySelectedPathLiveData.postValue(null)
                    }
                } catch (exception: Exception) {
                    exception.printStackTrace()
                    _gallerySelectedPathLiveData.postValue(null)
                }

            }
        }
    }
}