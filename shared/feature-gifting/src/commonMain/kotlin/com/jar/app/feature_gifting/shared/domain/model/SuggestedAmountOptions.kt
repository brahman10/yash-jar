package com.jar.app.feature_gifting.shared.domain.model

import com.jar.app.feature_user_api.domain.model.SuggestedAmount
import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class SuggestedAmountOptions(
    @SerialName("suggestedAmounts")
    val options: List<SuggestedAmount>,

    @SerialName("suggestedVolumes")
    val volumeOptions: List<SuggestedAmount>,
)