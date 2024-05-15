package com.jar.app.core_image_picker.api.data

data class ImagePickerOption(
    val imageSelectionSource:ImageSelectionSource = ImageSelectionSource.CAMERA,
    val cameraType: CameraType = CameraType.DOC_SINGLE_SIDE,
    val maxSelectionCount:Int = 1,
    val docType:String = ""
)