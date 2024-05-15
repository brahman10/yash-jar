package com.jar.app.feature_contact_sync_common.shared.domain.model

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class LocalContact(
    @SerialName("name")
    val name: String,

    @SerialName("countryCode")
    val countryCode: String,

    @SerialName("phoneNumber")
    val phoneNumber: String
) : Parcelable
