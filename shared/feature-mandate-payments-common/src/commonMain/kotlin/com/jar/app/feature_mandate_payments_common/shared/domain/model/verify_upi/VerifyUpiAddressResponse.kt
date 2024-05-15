package com.jar.app.feature_mandate_payments_common.shared.domain.model.verify_upi

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class VerifyUpiAddressResponse(
    @SerialName("name")
    val name: String,

    @SerialName("valid")
    val valid: Boolean,

    @SerialName("isEligibleForMandate")
    val isEligibleForMandate: Boolean,

    @SerialName("vpa")
    val vpa: String
)