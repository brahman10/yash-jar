package com.jar.app.feature_vasooli.impl.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class VasooliEntryResponse(
    @SerialName("handLoanId")
    val handLoanId: String
)