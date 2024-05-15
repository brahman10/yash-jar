package com.jar.app.feature_lending_kyc.shared.domain.model

import com.jar.app.core_base.data.dto.KycFeatureFlowType
import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class ManualPanEntryScreenArguments(
    @SerialName("fromScreen")
    val fromScreen: String,
    @SerialName("isPanAadhaarMismatch")
    val isPanAadhaarMismatch: Boolean = false,
    @SerialName("jarVerifiedPAN")
    val jarVerifiedPAN: Boolean = false,
    @SerialName("kycFeatureFlowType")
    val kycFeatureFlowType: KycFeatureFlowType = KycFeatureFlowType.UNKNOWN,
    @SerialName("lenderName")
    val lenderName: String? = null,
) : Parcelable
