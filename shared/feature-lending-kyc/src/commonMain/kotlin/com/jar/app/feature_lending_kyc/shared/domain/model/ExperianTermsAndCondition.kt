package com.jar.app.feature_lending_kyc.shared.domain.model

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class ExperianTermsAndCondition(
    @SerialName("title")
    val title: String,
    @SerialName("description")
    val description: String,
    @SerialName("hyperlink")
    val hyperLink:String? = null
) : Parcelable


@kotlinx.serialization.Serializable
data class ExperianTnCResponse(
    @SerialName("experianTnC")
    val experianTermsAndCondition: ExperianTermsAndCondition
)