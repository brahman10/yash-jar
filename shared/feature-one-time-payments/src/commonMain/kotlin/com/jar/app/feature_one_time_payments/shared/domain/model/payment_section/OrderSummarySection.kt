package com.jar.app.feature_one_time_payments.shared.domain.model.payment_section

data class OrderSummarySection(
    val amount: Float,
    override var id: PaymentSectionType = PaymentSectionType.ORDER_SUMMARY
) : PaymentSection