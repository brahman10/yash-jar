package com.jar.app.feature_homepage.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class HomeStaticCardOrderingData(
    @SerialName("contentType")
    val contentType: String,
    @SerialName("cardsOrder")
    val homeStaticCardOrdering: HomeStaticCardOrdering
)
