package com.jar.app.feature_user_api.domain.model

@kotlinx.serialization.Serializable
data class DetectedSpendsData(
    val isPGEnabled: Boolean,

    val initialInvestment: Boolean,

    val fullPaymentInfo: FullPaymentInfo?,

    val partPaymentInfo: PartPaymentInfo?,

    val promptEnabled: Boolean,

    val investPromptTitle: String?,

    val investPromptSubTitle: String?,

    val investPromptAmt: Float?,

    val investPromptSuggestions: List<SuggestedAmount>?
)