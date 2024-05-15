package com.jar.app.feature_user_api.data.dto

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class UserSettingsDTO(
    @SerialName("autoInvestNoSpends")
    val autoInvestNoSpends: Boolean? = null,

    @SerialName("isAuspiciousDateAlertEnabled")
    val isAuspiciousDateAlertEnabled: Boolean? = null,

    @SerialName("isAutopayEnabled")
    val isAutopayEnabled: Boolean? = null,

    @SerialName("isGiftingEnabled")
    val isGiftingEnabled: Boolean? = null,

    @SerialName("isGoldPriceAlertEnabled")
    val isGoldPriceAlertEnabled: Boolean? = null,

    @SerialName("isPushNotificationEnabled")
    val isPushNotificationEnabled: Boolean? = null,

    @SerialName("isReminderSetup")
    val isReminderSetup: Boolean? = null,

    @SerialName("isSavingsPaused")
    val isSavingsPaused: Boolean? = null,

    @SerialName("roundOffTo")
    val roundOffTo: String? = null,

    @SerialName("whatsAppOptin")
    val whatsAppOptin: Boolean? = null,

    @SerialName("showPromoCodeScreen")
    val showPromoCodeScreen: Boolean? = null,

    @SerialName("investRoundOffs")
    val investRoundOffs: Boolean? = null,

    @SerialName("deliveryChargeDiscountPercentage")
    val deliveryChargeDiscountPercentage: Float? = null,

    @SerialName("deliveryChargeText")
    val deliveryChargeText: String? = null,

    @SerialName("email")
    val email: String? = null,
)