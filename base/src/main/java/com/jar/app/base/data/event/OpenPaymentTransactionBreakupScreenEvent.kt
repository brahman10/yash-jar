package com.jar.app.base.data.event

data class OpenPaymentTransactionBreakupScreenEvent(
    val orderId: String?,
    val type: String? = "GENERIC",
    val title: String,
    val description: String,
)