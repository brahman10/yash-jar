package com.jar.app.feature_lending.shared.domain.model.temp

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class LendingAgreement(
    @SerialName("agreementId")
    val agreementId: String? = null,

    @SerialName("agreementLink")
    val agreementLink: String? = null,

    @SerialName("metadata")
    val metadata: String? = null,

    @SerialName("status")
    val status: String? = null,

    @SerialName("agreementStatus")
    val agreementStatus: String? = null,

    @SerialName("createdAt")
    val createdAt: String? = null,

    @SerialName("acceptedAt")
    val acceptedAt: String? = null
) : Parcelable