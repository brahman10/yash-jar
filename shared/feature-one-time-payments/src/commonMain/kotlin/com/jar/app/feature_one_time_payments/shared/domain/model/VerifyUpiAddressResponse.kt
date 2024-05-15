package com.jar.app.feature_one_time_payments.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class VerifyUpiAddressResponse(
    @SerialName("name")
    val name: String,

    @SerialName("valid")
    val valid: Boolean,

    @SerialName("vpa")
    val vpa: String
)