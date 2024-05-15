package com.jar.app.feature_lending.shared.domain.model.v2

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class DrawDownRequestV2(
    @SerialName("applicationId")
    val applicationId: String? = null,
    @SerialName("drawdownDetails")
    val drawdownDetails: DrawdownDetails? = null
)