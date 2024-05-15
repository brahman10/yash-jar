package com.jar.app.feature_health_insurance.shared.data.models.manage_screen

import com.jar.app.core_base.domain.model.card_library.Infographic
import com.jar.app.core_base.domain.model.card_library.TextData
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class InsuranceExpiredScreen(
    @SerialName("header") val header: List<TextData>? = null,
    @SerialName("description") val description: List<TextData>? = null,
    @SerialName("infoGraphic") val infoGraphic: Infographic? = null,
    @SerialName("cta") val cta: InsuranceCTA? = null,
)
