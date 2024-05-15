package com.jar.app.feature_gold_redemption.shared.data.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductFilter(
    @SerialName("title")
    val title: String? = null,
    @SerialName("type")
    val type: String? = null,
)