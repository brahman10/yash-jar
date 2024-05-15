package com.jar.app.core_image_picker.impl.data

import android.os.Parcelable
import com.jar.app.core_base.data.dto.KycFeatureFlowType
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class PreviewV2Arguments(
    val previewPath: String,
    val isSelfie: Boolean = false,
    val isGalleryFlow: Boolean = false,
    val fromScreen: String,
    val kycFeatureFlowType: KycFeatureFlowType = KycFeatureFlowType.UNKNOWN
) : Parcelable
