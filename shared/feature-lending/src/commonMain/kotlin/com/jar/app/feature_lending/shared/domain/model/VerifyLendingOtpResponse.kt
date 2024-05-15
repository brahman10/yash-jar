package com.jar.app.feature_lending.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class VerifyLendingOtpResponse(
    @SerialName("docId")
    val docId: String,

    @SerialName("message")
    val message: String,

    @SerialName("pdfContent")
    val pdfContent: String
)