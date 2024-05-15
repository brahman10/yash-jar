package com.jar.app.feature_user_api.domain.model

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class UserKycStatus(
    @SerialName("title")
    val title: String? = null,

    @SerialName("kycStatus")
    val kycStatus: String? = null,

    @SerialName("kycScreenData")
    val kycScreenData: KycScreenData? = null
) : Parcelable {
    fun isVerified() =
        !kycStatus.isNullOrEmpty() && kycStatus == UserKycStatusEnum.VERIFIED.name
}

@Parcelize
@kotlinx.serialization.Serializable
data class KycScreenData(
    @SerialName("title")
    val title: String? = null,

    @SerialName("description")
    val desc: String? = null,

    @SerialName("allRetryExhausted")
    val allRetryExhausted: Boolean? = null,

    @SerialName("contactShareMessage")
    val contactShareMsg: String? = null,

    @SerialName("shouldTryAgain")
    val shouldTryAgain: Boolean? = null
) : Parcelable

enum class UserKycStatusEnum {
    VERIFIED,
    FAILED,
    PENDING
}