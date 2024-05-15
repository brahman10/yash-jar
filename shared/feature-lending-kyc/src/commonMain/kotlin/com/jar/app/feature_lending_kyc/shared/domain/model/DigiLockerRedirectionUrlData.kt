package com.jar.app.feature_lending_kyc.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class DigiLockerRedirectionUrlData(
    @SerialName("url")
    val redirectionUrl: String,
    @SerialName("webhookUrl")
    val webhookUrl: String? = null
)

