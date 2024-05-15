package com.jar.app.feature_buy_gold_v2.shared.domain.model

import com.jar.app.feature_user_api.domain.model.SuggestedAmountOptions
import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class SuggestedAmountData(
    @SerialName("buyGoldOptions")
    val suggestedAmount: SuggestedAmountOptions
)