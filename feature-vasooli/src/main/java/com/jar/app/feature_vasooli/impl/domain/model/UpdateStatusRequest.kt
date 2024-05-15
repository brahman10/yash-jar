package com.jar.app.feature_vasooli.impl.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class UpdateStatusRequest(
    @SerialName("loanId")
    val loanId: String,

    @SerialName("status")
    val status: String
)