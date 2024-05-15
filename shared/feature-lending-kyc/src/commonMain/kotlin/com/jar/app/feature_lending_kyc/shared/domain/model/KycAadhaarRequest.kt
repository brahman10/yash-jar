package com.jar.app.feature_lending_kyc.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class KycAadhaarRequest(
    @SerialName("panNumber")
    val panNumber: String,

    @SerialName("dob")
    val dob: String
)