package com.jar.app.feature_user_api.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class SuggestedAmount(
    @SerialName("amount")
    val amount: Float,

    @SerialName("unit")
    val unit: String? = null,

    //Recommended is used for prefill amount
    @SerialName("recommended")
    val recommended: Boolean? = null,

    //Prefill is used for best tag
    @SerialName("prefill")
    val prefill: Boolean? = null,

    //For UI
    var isBestTag: Boolean? = false
)