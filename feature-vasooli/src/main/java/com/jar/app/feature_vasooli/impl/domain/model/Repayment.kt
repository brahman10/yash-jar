package com.jar.app.feature_vasooli.impl.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class Repayment(
    @SerialName("amount")
    val amount: Int,

    @SerialName("repaidOn")
    val repaidOn: Long,

    @SerialName("paymentMode")
    val paymentMode: String? = null
)