package com.jar.app.feature_health_insurance.shared.data.models.select_premium

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Header(
    @SerialName("infoImgUrl")
    val infoImgUrl: String?,
    @SerialName("infoText")
    val infoText: String?,
    @SerialName("text1")
    val text1: String?,
    @SerialName("text2")
    val text2: String?
)