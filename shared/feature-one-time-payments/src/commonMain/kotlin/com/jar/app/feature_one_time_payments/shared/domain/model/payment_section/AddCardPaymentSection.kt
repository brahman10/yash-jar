package com.jar.app.feature_one_time_payments.shared.domain.model.payment_section

data class AddCardPaymentSection(
    val bankLogoUrl: String,
    override var id: PaymentSectionType = PaymentSectionType.ADD_CARD
) : PaymentSection