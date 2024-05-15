package com.jar.app.feature_user_api.domain.model

@kotlinx.serialization.Serializable
data class UserSettings(
    val autoInvestNoSpends: Boolean?,
    
    val isAuspiciousDateAlertEnabled: Boolean?,
    
    val isAutopayEnabled: Boolean?,
    
    val isGiftingEnabled: Boolean?,
    
    val isGoldPriceAlertEnabled: Boolean?,
    
    val isPushNotificationEnabled: Boolean?,
    
    val isReminderSetup: Boolean?,
    
    val isSavingsPaused: Boolean?,
    
    val roundOffTo: String?,

    val whatsAppOptin: Boolean?,

    val showPromoCodeScreen: Boolean?,

    val investRoundOffs: Boolean?,

    val deliveryChargeDiscountPercentage: Float?,

    val deliveryChargeText: String?,

    val email: String?,
)