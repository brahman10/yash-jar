package com.jar.app.feature_user_api.data.dto

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class DetectedSpendsDTO(
    @SerialName("isPGEnabled")
    val isPGEnabled: Boolean,

    @SerialName("initialInvestment")
    val initialInvestment: Boolean,

    @SerialName("manualPaymentInfo")
    val fullPaymentInfoDTO: FullPaymentInfoDTO? = null,

    @SerialName("partPaymentInfo")
    val partPaymentInfoDTO: PartPaymentInfoDTO? = null,

    @SerialName("promptEnabled")
    val promptEnabled: Boolean,

    @SerialName("investPromptTitle")
    val investPromptTitle: String? = null,

    @SerialName("investPromptSubTitle")
    val investPromptSubTitle: String? = null,

    @SerialName("investPromptAmt")
    val investPromptAmt: Float? = null,

    @SerialName("investPromptSuggestions")
    val investPromptSuggestions: List<SuggestedAmountDTO>? = null
)