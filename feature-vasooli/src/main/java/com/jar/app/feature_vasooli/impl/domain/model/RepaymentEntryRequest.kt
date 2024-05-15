package com.jar.app.feature_vasooli.impl.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class RepaymentEntryRequest(
    @SerialName("loanId")
    val loanId: String,

    @SerialName("amount")
    val amount: Int? = null,

    @SerialName("paymentMode")
    val paymentMode: String? = null,

    @SerialName("repaidOn")
    val repaidOn: Long? = null
)