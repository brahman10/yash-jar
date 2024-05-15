package com.jar.app.feature_one_time_payments.shared.domain.model.payment_section

data class SecurePaymentSection(
    override var id: PaymentSectionType = PaymentSectionType.SECURE_PAYMENT_SECTION,
) : PaymentSection