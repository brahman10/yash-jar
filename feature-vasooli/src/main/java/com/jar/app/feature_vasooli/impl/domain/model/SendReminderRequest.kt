package com.jar.app.feature_vasooli.impl.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class SendReminderRequest(
    @SerialName("loanId")
    val loanId: String,

    @SerialName("imageIndex")
    val imageIndex: String
)