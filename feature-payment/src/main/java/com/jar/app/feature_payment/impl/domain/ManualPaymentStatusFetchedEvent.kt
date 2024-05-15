package com.jar.app.feature_payment.impl.domain

import com.jar.app.feature_one_time_payments_common.shared.FetchManualPaymentStatusResponse

data class ManualPaymentStatusFetchedEvent(
    val fetchManualPaymentStatusResponse: FetchManualPaymentStatusResponse
)