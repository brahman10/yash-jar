package com.jar.app.feature_transactions_common.shared

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class NewTransactionRoutine(
    @SerialName("title")
    val title: String? = null,

    @SerialName("value")
    val value: String? = null,

    @SerialName("description")
    val description: String? = null,

    @SerialName("txnRoutineCtaDetails")
    val txnRoutineCtaDetails: TxnRoutineCtaDetails? = null,

    @SerialName("statusText")
    val statusText: String? = null,

    @SerialName("status")
    private val status: String? = null,

    @SerialName("currentStep")
    val currentStep: Boolean? = null
) {
    fun getTxnRoutineStatus() = NewTransactionRoutineStatus.values().find { it.name == status } ?: NewTransactionRoutineStatus.INACTIVE
}

@kotlinx.serialization.Serializable
data class TxnRoutineCtaDetails(
    @SerialName("ctaButtonText")
    val ctaButtonText: String? = null,

    @SerialName("ctaButtonDeeplink")
    val ctaButtonDeeplink: String? = null
)