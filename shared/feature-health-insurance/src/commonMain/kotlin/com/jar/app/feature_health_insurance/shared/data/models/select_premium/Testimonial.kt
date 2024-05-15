package com.jar.app.feature_health_insurance.shared.data.models.select_premium

import com.jar.app.core_base.domain.model.card_library.TextData
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Testimonial(
    @SerialName("text")
    val text: List<TextData>,
    @SerialName("imageUrl")
    val imageUrl: String
)
