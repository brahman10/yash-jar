package com.jar.app.feature_kyc.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class KycPanOcrResponse (
    @SerialName("documentId")
    val documentId: String? = null,

    @SerialName("name")
    val name: String? = null,

    @SerialName("dob")
    val dob: String? = null,
)