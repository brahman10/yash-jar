package com.jar.app.feature_health_insurance.shared.data.models.transaction_details

import com.jar.app.core_base.domain.model.card_library.TextData
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Notification(
    @SerialName("icon")
    val icon: String,
    @SerialName("text")
    val text: List<TextData>
)