package com.jar.app.feature_lending_kyc.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class DigiLockerScreenContent(
    @SerialName("digiLockerScreenContent")
    val digiLockerScreenContent: DigiLockerScreenData? = null,
)


@kotlinx.serialization.Serializable
data class DigiLockerScreenData(
    @SerialName("consent")
    val consentString: String? = null,

    @SerialName("heading")
    val heading: String? = null,

    @SerialName("digiLockerImageURL")
    val digiLockerImageURL: String? = null,

    @SerialName("digiLockerTitle")
    val digiLockerTitle: String? = null,

    @SerialName("digiLockerDesc")
    val digiLockerDesc: String? = null,

    @SerialName("isDigiLockerAvailable")
    val isDigiLockerAvailable: Boolean? = null,

    @SerialName("digiLockerDownMessage")
    val digiLockerDownMessage: String? = null,

    @SerialName("manualKycImageURL")
    val manualKycImageURL: String? = null,

    @SerialName("manualKycTitle")
    val manualKycTitle: String? = null,

    @SerialName("manualKycDesc")
    val manualKycDesc: String? = null,

    @SerialName("isManualKycAvailable")
    val isManualKycAvailable: Boolean? = null,

    @SerialName("manualKycDownMessage")
    val manualKycDownMessage: String? = null,

    @SerialName("areBothFlowsNotAvailable")
    val areBothFlowDown: Boolean? = null,

    @SerialName("isDigilockerPreferred")
    val isDigilockerPreferred: Boolean? = null

    )