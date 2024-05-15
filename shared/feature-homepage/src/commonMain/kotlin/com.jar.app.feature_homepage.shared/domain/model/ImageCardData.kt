package com.jar.app.feature_homepage.shared.domain.model

import com.jar.app.core_base.domain.model.card_library.Label
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ImageCardData(
    @SerialName("labelTop")
    val label: Label,
    @SerialName("imageCards")
    val images: List<ImageCards>,
    @SerialName("deeplink")
    val deeplink: String,
    @SerialName("cardType")
    val cardType: String
)

@Serializable
data class ImageCards(
    @SerialName("id")
    val id: String,
    @SerialName("imageUrl")
    val imageUrl: String
)
