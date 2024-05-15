package com.jar.app.feature_vasooli.impl.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class UpdateEntryRequest(
    @SerialName("loanId")
    val loanId: String,

    @SerialName("borrowerName")
    val borrowerName: String? = null,

    @SerialName("lentOn")
    val lentOn: Long? = null,

    @SerialName("dueDate")
    val dueDate: Long? = null
)