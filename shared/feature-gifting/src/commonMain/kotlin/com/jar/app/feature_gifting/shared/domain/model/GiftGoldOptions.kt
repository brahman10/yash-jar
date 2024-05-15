package com.jar.app.feature_gifting.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class GiftGoldOptions(
    @SerialName("giftGoldOptions")
    val giftGoldOptions: SuggestedAmountOptions
)