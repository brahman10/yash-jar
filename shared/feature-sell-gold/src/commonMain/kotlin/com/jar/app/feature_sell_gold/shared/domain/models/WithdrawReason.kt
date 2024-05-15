package com.jar.app.feature_sell_gold.shared.domain.models

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class WithdrawReason(
    @SerialName("reason")
    val reason: String? = null,
    //For UI purpose
    @SerialName("isSelected")
    var isSelected: Boolean? = null
)