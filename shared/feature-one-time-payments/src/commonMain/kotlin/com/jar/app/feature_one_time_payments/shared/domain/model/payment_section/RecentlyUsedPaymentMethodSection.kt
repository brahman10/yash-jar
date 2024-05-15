package com.jar.app.feature_one_time_payments.shared.domain.model.payment_section

import com.jar.app.feature_one_time_payments.shared.domain.model.payment_method.PaymentMethod

data class RecentlyUsedPaymentMethodSection(
    val recentlyUsedPaymentMethods: List<PaymentMethod>,
    override var id: PaymentSectionType = PaymentSectionType.RECENTLY_USED_PAYMENT_SECTION
) : PaymentSection