package com.jar.health_insurance.impl.ui.payment_status_page

import com.jar.app.feature_health_insurance.shared.data.models.payment_status.PaymentStatusResponse


data class PaymentStatusState(
    val paymentStatus: PaymentStatusResponse?,

    )

sealed class PaymentStatusEvent {
    data class fetchPaymentStatus(val insuranceId: String) : PaymentStatusEvent()
    data class onTopSectionCtaClicked(val deepLink: String) : PaymentStatusEvent()
    data class onGoToHomeClicked(val deepLink: String) : PaymentStatusEvent()
    data class onContactSupportClicked(val deepLink: String) : PaymentStatusEvent()
}