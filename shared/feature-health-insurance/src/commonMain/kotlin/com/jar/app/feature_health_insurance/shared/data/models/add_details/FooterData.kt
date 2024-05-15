package com.jar.app.feature_health_insurance.shared.data.models.add_details

import com.jar.app.core_base.domain.model.card_library.TextData
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FooterData(
    @SerialName("images")
    val images: List<String>,
    @SerialName("text")
    val text: List<TextData>,
    @SerialName("providerIcon")
    val providerIcon: String
)
