package com.jar.app.feature_user_api.domain.model

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class PhoneNumberWithCountryCode(
    @SerialName("phoneNumber")
    val phoneNumber: String,

    @SerialName("countryCode")
    val countryCode: String
): Parcelable