package com.jar.app.feature_health_insurance.shared.data.models.landing1

import com.jar.app.core_base.domain.model.card_library.TextData
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AbandonScreenBenefits(
    @SerialName("icon")
    val icon: String? = null,
    @SerialName("text")
    val text: List<TextData>? = null
)
