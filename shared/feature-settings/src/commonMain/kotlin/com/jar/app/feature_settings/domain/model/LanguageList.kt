package com.jar.app.feature_settings.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class LanguageList(
    @SerialName("languages")
    val languages: List<Language>
)
