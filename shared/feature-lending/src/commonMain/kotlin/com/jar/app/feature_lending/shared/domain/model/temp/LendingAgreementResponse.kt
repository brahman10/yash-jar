package com.jar.app.feature_lending.shared.domain.model.temp

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class LendingAgreementResponse(
    @SerialName("agreementStatus")
    val agreementStatus: String,

    @SerialName("documentInBase64")
    val documentInBase64: String,

    @SerialName("documentId")
    val documentId: String,
) : Parcelable