package com.jar.app.feature_homepage.shared.domain.model.single_home_feed

import com.jar.app.core_base.domain.model.card_library.TextData
import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class ButtonText(
    @SerialName("icon")
    val icon: String? = null,

    @SerialName("iconList")
    val iconList: List<String>? = null,

    @SerialName("textList")
    val textList: List<TextData>? = null
)