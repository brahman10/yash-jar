package com.jar.app.feature_lending_kyc.shared.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DigiLockerVerificationStatus(
    @SerialName("status")
    val status: String,
)
