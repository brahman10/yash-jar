package com.jar.app.feature_payment.impl.domain

import com.jar.app.feature_one_time_payments.shared.data.model.base.InitiatePaymentResponse

data class RetryManualPaymentEvent(
    val initiatePaymentResponse: InitiatePaymentResponse
)