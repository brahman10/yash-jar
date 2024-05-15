package com.jar.app.core_image_picker.api

import androidx.navigation.NavController
import com.jar.app.core_base.data.dto.KycFeatureFlowType
import com.jar.app.core_image_picker.api.data.ImagePickerOption

/*Exposed API to pick image*/
interface ImagePickerManager {

    /**
     * option: Image picker configuration object
     * navController: pass any child(child navHost where camera flow will start)
     * navController or leave it as null
     * onImageSelectedListener: callback after user captured/selected image path from Camera/Gallery
     */
    fun openImagePicker(
        option: ImagePickerOption,
        navController: NavController? = null,
        fromScreen: String? = null,
        kycFeatureFlowType: KycFeatureFlowType = KycFeatureFlowType.UNKNOWN,
        onImageSelectedListener: (String) -> Unit
    )

}