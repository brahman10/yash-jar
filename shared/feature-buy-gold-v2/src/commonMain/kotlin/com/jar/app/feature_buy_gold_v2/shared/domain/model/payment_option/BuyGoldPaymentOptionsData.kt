package com.jar.app.feature_buy_gold_v2.shared.domain.model.payment_option

import kotlinx.serialization.Serializable

@Serializable
data class BuyGoldPaymentOptionsData(
    val context: String,
    val maxPaymentMethodsCount: Int,
    val ctaText: String?
)