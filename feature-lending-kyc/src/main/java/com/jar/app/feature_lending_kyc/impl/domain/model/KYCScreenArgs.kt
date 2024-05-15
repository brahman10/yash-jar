package com.jar.app.feature_lending_kyc.impl.domain.model

import android.os.Parcelable
import com.jar.app.core_base.data.dto.KycFeatureFlowType
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class KYCScreenArgs(
    @SerialName("fromScreen")
    val fromScreen: String = "",
    @SerialName("kycFeatureFlowType")
    val kycFeatureFlowType: KycFeatureFlowType = KycFeatureFlowType.UNKNOWN,
    @SerialName("lenderName")
    val lenderName: String? = null,
    @SerialName("applicationId")
    val applicationId: String? = null,
    @SerialName("url")
    val url: String? = null,
    @SerialName("webhookUrl")
    val webhookUrl: String? = null,
):Parcelable
