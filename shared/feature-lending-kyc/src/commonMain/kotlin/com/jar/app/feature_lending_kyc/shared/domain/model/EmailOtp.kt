package com.jar.app.feature_lending_kyc.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class EmailOtp(
    @SerialName("emailId")
    val emailId: String,

    @SerialName("requestedAt")
    val requestedAt: Long,

    @SerialName("status")
    val status: String,

    @SerialName("resentOTPInSeconds")
    val resentOTPInSeconds: Long? = null,

    @SerialName("validityInSeconds")
    val validityInSeconds: Long? = null,

    @SerialName("verifiedAt")
    val verifiedAt: String? = null,

    @SerialName("attemptLeft")
    val attemptLeft: Int? = null,

    @SerialName("msgId")
    val messageId: String? = null,

    @SerialName("deliveryStatus")
    val deliveryStatus: String? = null,

    @SerialName("failureReason")
    val failureReason: String? = null
)

enum class EmailDeliveryStatus {
    BOUNCE, COMPLAINT, DELIVERED
}