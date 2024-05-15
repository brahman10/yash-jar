package com.jar.app.core_image_picker.impl.data

import android.os.Parcelable
import com.jar.app.core_base.data.dto.KycFeatureFlowType
import com.jar.app.core_image_picker.api.data.CameraType
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class CameraArguments(
    val cameraType: CameraType,
    val docType: String,
    val fromScreen: String,
    val kycFeatureFlowType: KycFeatureFlowType = KycFeatureFlowType.UNKNOWN
) : Parcelable
