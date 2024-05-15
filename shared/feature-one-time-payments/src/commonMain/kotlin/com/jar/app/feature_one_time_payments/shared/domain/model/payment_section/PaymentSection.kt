package com.jar.app.feature_one_time_payments.shared.domain.model.payment_section

sealed interface PaymentSection {
    var id: PaymentSectionType
}