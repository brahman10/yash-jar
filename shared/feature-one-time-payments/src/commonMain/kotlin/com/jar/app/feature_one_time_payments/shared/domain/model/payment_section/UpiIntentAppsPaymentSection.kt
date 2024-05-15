package com.jar.app.feature_one_time_payments.shared.domain.model.payment_section

import com.jar.app.feature_one_time_payments.shared.domain.model.UpiApp

data class UpiIntentAppsPaymentSection(
    val availableUpiApps: List<UpiApp>,
    override var id: PaymentSectionType = PaymentSectionType.UPI_INTENT_APP_PAYMENT_SECTION
) : PaymentSection