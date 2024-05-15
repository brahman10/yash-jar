package com.jar.app.feature_homepage.shared.domain.model.viba

import com.jar.app.core_base.domain.model.card_library.CTA
import com.jar.app.core_base.domain.model.card_library.CardBackground
import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class VibaHorizontalCardData(
    @SerialName("background")
    val background: CardBackground,

    @SerialName("cardType")
    val cardType: String,

    @SerialName("cta")
    val cta: CTA,

    @SerialName("description")
    val description: List<Description>? = null,

    @SerialName("icon")
    val icon: String,

    @SerialName("priority")
    val priority: Int,

    @SerialName("title")
    val title: String? = null
)

@kotlinx.serialization.Serializable
data class Description(
    @SerialName("description")
    val description: String,

    @SerialName("title")
    val title: String
)


enum class VibaCardType {
    FIRST,
    MIDDLE,
    END
}