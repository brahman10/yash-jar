package com.jar.app.feature_settings.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class Language(
    @SerialName("code")
    val code: String,

    @SerialName("language")
    val language: String,

    @SerialName("text")
    val text: String,

    var isSelected: Boolean = false
)