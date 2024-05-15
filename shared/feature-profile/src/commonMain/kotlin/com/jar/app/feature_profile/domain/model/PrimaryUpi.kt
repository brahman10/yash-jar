package com.jar.app.feature_profile.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class PrimaryUpi(
    @SerialName("primaryUpiId")
    val primaryUpiId: String? = null
)
