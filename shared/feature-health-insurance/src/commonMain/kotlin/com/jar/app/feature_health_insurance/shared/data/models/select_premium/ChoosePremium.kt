package com.jar.app.feature_health_insurance.shared.data.models.select_premium

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChoosePremium(
    @SerialName("infoImgUrl")
    val infoImgUrl: String?,
    @SerialName("infoText")
    val infoText: String?,
    @SerialName("text")
    val text: String?
)