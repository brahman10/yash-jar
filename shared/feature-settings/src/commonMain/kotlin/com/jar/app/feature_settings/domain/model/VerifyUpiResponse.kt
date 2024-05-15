package com.jar.app.feature_settings.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class VerifyUpiResponse(
    @SerialName("name")
    val name: String? = null,

    @SerialName("valid")
    val valid: Boolean,

    @SerialName("eligibleForMandate")
    val eligibleForMandate: Boolean? = null,

    @SerialName("vpa")
    val vpa: String
)