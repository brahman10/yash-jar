package com.jar.app.feature_one_time_payments.shared.data.model.base

data class InitiatePaymentEvent(
    val initiatePaymentResponse: InitiatePaymentResponse,
    val flowType: String? = null,
    val showWeeklyChallengeAnimation: Boolean = false,
    val flowContextName: String? = null
)