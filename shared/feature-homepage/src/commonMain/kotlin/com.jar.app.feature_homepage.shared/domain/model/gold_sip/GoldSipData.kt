package com.jar.app.feature_homepage.shared.domain.model.gold_sip

data class GoldSipData(
    val amount: Int,
    val subscriptionType: String,
    val subscriptionStatus: String? = null,
    val postSipTitleText: String? = null,
    val orderId: String? = null,
)