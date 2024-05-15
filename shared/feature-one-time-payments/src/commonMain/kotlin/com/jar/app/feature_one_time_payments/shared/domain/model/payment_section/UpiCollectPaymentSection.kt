package com.jar.app.feature_one_time_payments.shared.domain.model.payment_section

data class UpiCollectPaymentSection(
    val appLogoUrl: String,
    val errorMessage: String? = null,
    override var id: PaymentSectionType = PaymentSectionType.UPI_COLLECT_PAYMENT_SECTION
) : PaymentSection