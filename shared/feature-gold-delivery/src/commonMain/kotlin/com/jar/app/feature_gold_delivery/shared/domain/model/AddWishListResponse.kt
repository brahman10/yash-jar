package com.jar.app.feature_gold_delivery.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class AddWishListResponse(
    @SerialName("id")
    val id: String? = null
)