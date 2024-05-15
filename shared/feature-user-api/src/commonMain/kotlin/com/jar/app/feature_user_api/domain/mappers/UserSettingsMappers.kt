package com.jar.app.feature_user_api.domain.mappers

import com.jar.app.feature_user_api.data.dto.UserSettingsDTO
import com.jar.app.feature_user_api.domain.model.UserSettings

fun UserSettingsDTO.toUserSettings(): UserSettings {
    return UserSettings(
        autoInvestNoSpends = autoInvestNoSpends,
        isAuspiciousDateAlertEnabled = isAuspiciousDateAlertEnabled,
        isAutopayEnabled = isAutopayEnabled,
        isGiftingEnabled = isGiftingEnabled,
        isGoldPriceAlertEnabled = isGoldPriceAlertEnabled,
        isPushNotificationEnabled = isPushNotificationEnabled,
        isReminderSetup = isReminderSetup,
        isSavingsPaused = isSavingsPaused,
        roundOffTo = roundOffTo,
        whatsAppOptin = whatsAppOptin,
        showPromoCodeScreen = showPromoCodeScreen,
        investRoundOffs = investRoundOffs,
        deliveryChargeDiscountPercentage = deliveryChargeDiscountPercentage,
        deliveryChargeText = deliveryChargeText,
        email = email
    )
}