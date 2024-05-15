package com.jar.app.feature_homepage.shared.domain.event.detected_spends

import com.jar.app.core_base.util.BaseConstants
import com.jar.app.feature_round_off.shared.domain.model.InitiateDetectedRoundOffsPaymentRequest

data class InitiateDetectedRoundOffsPaymentEvent(
    val initiateDetectedRoundOffsPaymentRequest: InitiateDetectedRoundOffsPaymentRequest,
    val source: String = BaseConstants.ManualPaymentFlowType.Roundups_Card
)