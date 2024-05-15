package com.jar.app.feature_lending_kyc.shared.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DigilockerRedirectionData(
    @SerialName("state")
    val state:String,
    @SerialName("code")
    val code:String
)
