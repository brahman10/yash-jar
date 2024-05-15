package com.jar.app.feature_savings_common.shared.domain.model

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class SavingSuggestedAmount(
    @SerialName("amount")
    val amount: Float,

    @SerialName("recommended")
    val recommended: Boolean,

    @SerialName("unit")
    val unit: String? = null,
) : Parcelable