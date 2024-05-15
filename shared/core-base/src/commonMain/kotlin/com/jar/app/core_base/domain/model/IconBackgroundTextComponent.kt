package com.jar.app.core_base.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class IconBackgroundTextComponent(
    @SerialName("iconUrl")
    val iconUrl: String? = null,
    @SerialName("bgColor")
    val bgColor: String? = null,
    @SerialName("text")
    val text: String? = null
)