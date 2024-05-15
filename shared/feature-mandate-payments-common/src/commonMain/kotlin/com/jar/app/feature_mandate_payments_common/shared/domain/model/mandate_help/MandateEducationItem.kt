package com.jar.app.feature_mandate_payments_common.shared.domain.model.mandate_help

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class MandateEducationItem(
    @SerialName("title")
    val title: String,
    @SerialName("imageUrl")
    val imageUrl: String
)