package com.jar.feature_gold_price_alerts.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class CreateAlertRequest(
    @SerialName("liveBuyPriceWhileSettingAlert")
    val liveBuyPriceWhileSettingAlert: LiveBuyPrice,
    @SerialName("alertOnPriceDropBy")
    val alertOnPriceDropBy: Float,
)