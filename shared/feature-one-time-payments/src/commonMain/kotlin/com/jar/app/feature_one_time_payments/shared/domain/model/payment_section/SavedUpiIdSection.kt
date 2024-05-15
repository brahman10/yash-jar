package com.jar.app.feature_one_time_payments.shared.domain.model.payment_section

data class SavedUpiIdSection(
    val savedUpiIds: List<String>,
    override var id: PaymentSectionType = PaymentSectionType.SAVED_UPI_PAYMENT_SECTION
) : PaymentSection