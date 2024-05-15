package com.jar.app.feature_mandate_payments_common.shared.domain.model.phonepe

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class PhonePeAutoPayResultData(
    @SerialName("authReqId")
    val authReqId: String
): Parcelable