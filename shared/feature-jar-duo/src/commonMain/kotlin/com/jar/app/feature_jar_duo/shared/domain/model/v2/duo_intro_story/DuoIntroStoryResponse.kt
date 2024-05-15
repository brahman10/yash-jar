package com.jar.app.feature_jar_duo.shared.domain.model.v2.duo_intro_story

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class DuoIntroStoryResponse(
    @SerialName("data")
    val duoIntroData: DuoIntroData,
    @SerialName("success")
    val success: Boolean
)