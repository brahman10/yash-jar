package com.jar.app.feature_health_insurance.shared.data.models.add_details

data class PaymentConfigResponse(
    val jusPay: JusPay,
    val orderId: String,
    val paymentProvider: String,
    val paytm: Any,
    val priceResponse: Any,
    val transactionType: String,
    val txnAmount: Double
)