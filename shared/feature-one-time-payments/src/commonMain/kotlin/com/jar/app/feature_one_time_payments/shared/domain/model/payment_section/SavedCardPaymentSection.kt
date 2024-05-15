package com.jar.app.feature_one_time_payments.shared.domain.model.payment_section

import com.jar.app.feature_one_time_payments.shared.domain.model.juspay.SavedCard

data class SavedCardPaymentSection(
    val cards: List<SavedCard>,
    override var id: PaymentSectionType = PaymentSectionType.SAVED_CARDS_PAYMENT_SECTION
) : PaymentSection