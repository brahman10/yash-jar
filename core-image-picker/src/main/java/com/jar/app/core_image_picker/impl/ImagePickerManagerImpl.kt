package com.jar.app.core_image_picker.impl

import android.net.Uri
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import com.jar.app.base.ui.BaseNavigation
import com.jar.app.core_base.util.BaseConstants
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.app.base.util.encodeUrl
import com.jar.app.core_base.data.dto.KycFeatureFlowType
import com.jar.app.core_image_picker.api.ImagePickerManager
import com.jar.app.core_image_picker.api.data.ImagePickerOption
import com.jar.app.core_image_picker.api.data.ImageSelectionSource
import com.jar.app.core_image_picker.impl.data.CameraArguments
import dagger.Lazy
import javax.inject.Inject

internal class ImagePickerManagerImpl @Inject constructor(
    private val activity: FragmentActivity,
    private val serializer: Serializer,
    private val navControllerRef: Lazy<NavController>
) : ImagePickerManager, BaseNavigation {

    private val mNavController by lazy {
        navControllerRef.get()
    }

    override fun openImagePicker(
        option: ImagePickerOption,
        navController: NavController?,
        fromScreen: String?,
        kycFeatureFlowType: KycFeatureFlowType,
        onImageSelectedListener: (String) -> Unit
    ) {
        val fromString = fromScreen ?: BaseConstants.FROM_OTHER
        val cameraArgs =
            CameraArguments(option.cameraType, option.docType, fromString, kycFeatureFlowType)
        val url = when (option.imageSelectionSource) {
            ImageSelectionSource.CAMERA -> "android-app://com.jar.app/camera/${
                encodeUrl(serializer.encodeToString(cameraArgs))
            }"
            ImageSelectionSource.GALLERY -> "android-app://com.jar.app/gallery"
        }
        val controller = navController ?: mNavController
        controller.navigate(
            Uri.parse(url),
            getNavOptions(shouldAnimate = true)
        )
        controller.currentBackStackEntry?.savedStateHandle
            ?.getLiveData<String>(BaseConstants.SELECTED_PATH)
            ?.observe(activity) {
                onImageSelectedListener.invoke(it)
            }

    }
}