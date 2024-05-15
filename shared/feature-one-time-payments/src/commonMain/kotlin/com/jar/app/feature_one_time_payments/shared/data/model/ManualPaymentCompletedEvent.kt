package com.jar.app.feature_one_time_payments.shared.data.model

import com.jar.app.feature_one_time_payments_common.shared.ManualPaymentStatus

data class ManualPaymentCompletedEvent(
    val manualPaymentStatus: ManualPaymentStatus
)
