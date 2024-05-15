package com.jar.app.feature_lending.shared.domain.model

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class OtpVerifyRequestData(
    @SerialName("applicationId")
    val applicationId: String,
    @SerialName("otp")
    val otp: String,
    @SerialName("type")
    val type: String = "LOAN_AGREEMENT",
    @SerialName("ipAddress")
    val ipAddress: String? = null
) : Parcelable
