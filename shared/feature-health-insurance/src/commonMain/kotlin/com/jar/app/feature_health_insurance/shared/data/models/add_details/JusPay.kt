package com.jar.app.feature_health_insurance.shared.data.models.add_details

data class JusPay(
    val callbackUrl: String,
    val clientAuthToken: String,
    val id: String,
    val orderId: String,
    val paymentLinks: Any,
    val utmBankValue: Any,
    val uuid: String
)