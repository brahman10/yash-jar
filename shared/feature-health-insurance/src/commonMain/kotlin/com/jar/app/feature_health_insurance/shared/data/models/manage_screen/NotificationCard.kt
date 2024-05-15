package com.jar.app.feature_health_insurance.shared.data.models.manage_screen

import com.jar.app.core_base.domain.model.card_library.TextData
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NotificationCard (
    @SerialName("text") val text: List<TextData>,
    @SerialName("icon") val icon: String? = null,
    @SerialName("showOnTop") val showOnTop: Boolean? = null,
)
