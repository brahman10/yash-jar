package com.jar.app.feature_lending.shared.domain.model.v2

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class Withdrawal(
    @SerialName("amount")
    val amount: Float? = null,
    @SerialName("countDownTimeInMillis")
    val countDownTimeInMillis: Long? = null,
    @SerialName("status")
    val status: String? = null,
    @SerialName("authorizedPartner")
    val authorizedPartner: KeyValueData? = null
)