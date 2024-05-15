package com.jar.app.feature_homepage.shared.domain.model.hamburger

import com.jar.app.core_base.domain.model.IconBackgroundTextComponent
import com.jar.app.core_base.domain.model.card_library.InfographicType
import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class HamburgerItems(
    @SerialName("hamburgerItemList")
    val hamburgerItems: List<HamburgerItem>? = null,

    @SerialName("hamburgerHeader")
    val hamburgerHeader: IconBackgroundTextComponent? = null
)

@kotlinx.serialization.Serializable
data class HamburgerItem(
    @SerialName("text")
    val text: String? = null,
    @SerialName("logo")
    val logo: String? = null,
    @SerialName("itemType")
    val itemType: String? = null,
    @SerialName("enabled")
    val enabled: Boolean,
    @SerialName("deepLink")
    val deepLink: String,
    @SerialName("infographicType")
    val infographicType: String? = null,
    @SerialName("highlighted")
    val isHighlighted: Boolean? = null,
    @SerialName("showNewTag")
    val showNewTag: Boolean? = false,
    @SerialName("showShimmer")
    val showShimmer: Boolean? = false,
    @SerialName("type")
    val type: String? = null,
) {
    fun getInfoGraphicType(): InfographicType {
        return InfographicType.values().find { it.name == infographicType } ?: InfographicType.IMAGE
    }
}