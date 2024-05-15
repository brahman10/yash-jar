package com.jar.app.feature_user_api.domain.model

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class RequestOtpData(
    @SerialName("otpService")
    val otpService: String? = null,

    @SerialName("reqId")
    val reqId: String,

    @SerialName("length")
    val length: Int = 6
) : Parcelable