package com.jar.app.feature_health_insurance.shared.data.models.select_premium

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Partnership(
    @SerialName("imgUrl")
    val imgUrl: String?,
    @SerialName("text")
    val text: String?
)