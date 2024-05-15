package com.jar.app.feature_settings.domain

object SettingsEventKey {

    //SETTINGS
    const val yes = "yes"
    const val no = "no"
    const val currentState = "currentState"
    const val CurrentStatus = "CurrentStatus"
    const val Clicked_AutoInvest_SettingsScreen = "Clicked_AutoInvest_SettingsScreen"
    const val Clicked_DailySavings_SettingsScreen = "Clicked_DailySavings_SettingsScreen"
    const val Clicked_RoundOff_SettingsScreen = "Clicked_RoundOff_SettingsScreen"
    const val Clicked_GoldSip_SettingsScreen = "Clicked_GoldSip_SettingsScreen"
    const val Clicked_Notifications_SettingsTab = "Clicked_Notifications_SettingsTab"
    const val Clicked_Language_SettingsScreen = "Clicked_Language_SettingsScreen"
    const val Clicked_BatteryOptimization_SettingsScreen =
        "Clicked_BatteryOptimization_SettingsScreen"
    const val Clicked_SecurityShield_SettingsScreen = "Clicked_BatteryOptimization_SettingsScreen"
    const val Clicked_Terms_SettingsScreen = "Clicked_Terms_SettingsScreen"
    const val Clicked_Privacy_SettingsScreen = "Clicked_Privacy_SettingsScreen"
    const val Clicked_PaymentMethods_SettingsScreen = "Clicked_PaymentMethods_SettingsScreen"
    const val Shown_SettingsScreen_Account = "Shown_SettingsScreen_Account"
    const val Exit_SettingsTab_Account = "Exit_SettingsTab_Account"

    //PAYMENT METHODS
    const val noOfIds = "noOfIds"
    const val noOfCards = "noOfCards"
    const val timeSpent = "timeSpent"
    const val id = "id"
    const val errorMessage = "errorMessage"
    const val Shown_PaymentMethodsScreen = "Shown_PaymentMethodsScreen"
    const val Clicked_AddUPI_PaymentMethodsScreen = "Clicked_AddUPI_PaymentMethodsScreen"
    const val Shown_EnterUPI_AddUPIPopUp = "Shown_EnterUPI_AddUPIPopUp"
    const val Clicked_Verify_AddUPIPopUp = "Clicked_Verify_AddUPIPopUp"
    const val Clicked_Cancel_AddUPIPopUp = "Clicked_Cancel_AddUPIPopUp"
    const val Shown_Error_AddUPIPopUp = "Shown_Error_AddUPIPopUp"
    const val Shown_Success_AddUPI_PopUp = "Shown_Success_AddUPI_PopUp"
    const val Clicked_DeleteUPI_PaymentMethodsScreen = "Clicked_DeleteUPI_PaymentMethodsScreen"
    const val Shown_Delete_DeleteUPIPopUp = "Shown_Delete_DeleteUPIPopUp"
    const val Clicked_Delete_DeleteUPIPopUp = "Clicked_Delete_DeleteUPIPopUp"
    const val Clicked_Cancel_DeleteUPIPopUp = "Clicked_Cancel_DeleteUPIPopUp"
    const val Clicked_DeleteCard_PaymentMethodsScreen = "Clicked_DeleteCard_PaymentMethodsScreen"
    const val Shown_Delete_DeleteCardPopUp = "Shown_Delete_DeleteCardPopUp"
    const val Clicked_Delete_DeleteCardPopUp = "Clicked_Delete_DeleteCardPopUp"
    const val Clicked_Cancel_DeleteCardPopUp = "Clicked_Cancel_DeleteCardPopUp"
    const val Clicked_AddCard_PaymentMethodsScreen = "Clicked_AddCard_PaymentMethodsScreen"

    //ROUND OFF
    const val currentAmount = "currentAmount"
    const val amount = "amount"
    const val days = "days"
    const val finalState = "finalState"
    const val Shown_RoundOffScreen = "Shown_RoundOffScreen"
    const val Clicked_DisableToggle_RoundOffScreen = "Clicked_DisableToggle_RoundOffScreen"
    const val Shown_DisableRoundOffPopUp = "Shown_DisableRoundOffPopUp"
    const val Clicked_Pause_DisableRoundOffPopUp = "Clicked_Pause_DisableRoundOffPopUp"
    const val Clicked_Disable_DisableRoundOffPopUp = "Clicked_Disable_DisableRoundOffPopUp"
    const val Shown_Success_DisableRoundOffPopUp = "Shown_Success_DisableRoundOffPopUp"
    const val Clicked_EnableToggle_RoundOffScreen = "Clicked_EnableToggle_RoundOffScreen"
    const val Clicked_EditAmount_RoundOffScreen = "Clicked_EditAmount_RoundOffScreen"
    const val Shown_SetRoundOffPopUp = "Shown_SetRoundOffPopUp"
    const val Clicked_SetRoundOff_RoundOffPopUp = "Clicked_SetRoundOff_RoundOffPopUp"
    const val Clicked_Cancel_RoundOffPopUp = "Clicked_Cancel_RoundOffPopUp"
    const val Shown_Success_RoundOffPopUp = "Shown_Success_RoundOffPopUp"
    const val Clicked_Pause_RoundOffScreen = "Clicked_Pause_RoundOffScreen"
    const val Shown_Success_RoundOffResumedScreen = "Shown_Success_RoundOffResumedScreen"
    const val Shown_PauseRoundOffPopUp = "Shown_PauseRoundOffPopUp"
    const val Clicked_Pause_PauseRoundOffPopUp = "Clicked_Pause_PauseRoundOffPopUp"
    const val Clicked_Cancel_PauseRoundOffPopUp = "Clicked_Cancel_PauseRoundOffPopUp"
    const val Shown_Success_PauseRoundOffPopUp = "Shown_Success_PauseRoundOffPopUp"
    const val Clicked_Invest10_RoundOffScreen = "Clicked_Invest10_RoundOffScreen"

    //Notification Settings
    const val Shown_NotificationSettings_SettingsTab = "Shown_NotificationSettings_SettingsTab"
    const val Exit_NotificationSettings_SettingsTab = "Exit_NotificationSettings_SettingsTab"
    const val Clicked_AlertToggle_NotificationSettings = "Clicked_AlertToggle_NotificationSettings"

    //Language
    const val languagesShown = "LanguagesShown"
    const val currentLanguage = "currentLanguage"
    const val Shown_LanguageScreen = "Shown_LanguageSelectionScreen"
    const val Clicked_Apply_LanguageScreen = "Clicked_Apply_LanguageScreen"
    const val FromScreen = "FromScreen"
    const val settings = "Settings"
    const val defaultLanguageShown = "defaultLanguageShown"
    const val languageSelected = "languageSelected"

    //Battery Optimisation
    const val Shown_BatteryOptimizationScreen = "Shown_BatteryOptimizationScreen"
    const val Exit_BatteryOptimization_SettingsTab = "Exit_BatteryOptimization_SettingsTab"
    const val Clicked_OptimizeNow_BatteryOptimizationScreen = "Clicked_OptimizeNow_BatteryOptimizationScreen"

    //Security Shield
    const val Shown_SecurityShieldScreen = "Shown_SecurityShieldScreen"
    const val Clicked_Enable_SecurityShieldScreen = "Clicked_Enable_SecurityShieldScreen"
    const val Clicked_DoLater_SecurityShieldScreen = "Clicked_DoLater_SecurityShieldScreen"
    const val Clicked_Disable_SecurityShieldScreen = "Clicked_Disable_SecurityShieldScreen"
}