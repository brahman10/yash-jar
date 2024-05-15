package com.jar.app.feature_round_off.shared.domain.event

import com.jar.app.feature_one_time_payments.shared.data.model.base.InitiatePaymentResponse

data class ManualRoundOffPaymentEvent(val initiatePaymentResponse: InitiatePaymentResponse)
