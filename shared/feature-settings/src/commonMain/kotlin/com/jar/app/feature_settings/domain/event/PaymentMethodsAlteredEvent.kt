package com.jar.app.feature_settings.domain.event

data class PaymentMethodsAlteredEvent(val position: Int, val paymentMethodId: String? = null)