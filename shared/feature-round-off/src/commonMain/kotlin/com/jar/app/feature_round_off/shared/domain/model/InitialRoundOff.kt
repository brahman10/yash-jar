package com.jar.app.feature_round_off.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class InitialRoundOff(
    @SerialName("txnAmt")
    val transactionAmount: Float? = null,
    @SerialName("orderId")
    val orderId: String? = null,
    @SerialName("mandateAmount")
    val mandateAmount: Float? = null
)

enum class RoundOffType{
    SMS,GENERIC
}