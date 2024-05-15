package com.jar.app.feature_settings.domain.model

data class SavedCardPaymentMethod(
    val cards: List<SavedCard>,
    override val position: Int
) : PaymentMethod()