package com.jar.app.feature_user_api.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class ValidatePinCodeResponse(
    @SerialName("city")
    val city: String? = null,

    @SerialName("pinCode")
    val pinCode: String? = null,

    @SerialName("state")
    val state: String? = null,

    @SerialName("status")
    val status: Int
) {
    fun getEligibilityStatus(): PinCodeEligibility {
        return when (status) {
            1 -> PinCodeEligibility.DELIVERABLE
            else -> PinCodeEligibility.NOT_DELIVERABLE
        }
    }
}

enum class PinCodeEligibility {
    DELIVERABLE, NOT_DELIVERABLE
}