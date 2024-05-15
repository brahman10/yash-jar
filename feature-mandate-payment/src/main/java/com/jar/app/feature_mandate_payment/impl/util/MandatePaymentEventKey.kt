package com.jar.app.feature_mandate_payment.impl.util

object MandatePaymentEventKey {

    const val Button = "Button"
    const val Action = "Action"
    const val Data = "Data"
    const val Coupon = "Coupon"
    const val Shown = "Shown"
    const val UpiApp = "UpiApp"
    const val AvailableUpiAppName = "AvailableUpiAppName"
    const val AvailableUpiAppPackageName = "AvailableUpiAppPackageName"
    const val UpiId = "UpiId"
    const val Proceed = "Proceed"
    const val PreferredBank = "PreferredBank"
    const val BankName = "bankName"
    const val Back = "Back"
    const val Skip = "Skip"
    const val Close = "Close"
    const val BestAmount = "BestAmount"
    const val MandateAmount = "MandateAmount"
    const val VerifyAndProceed = "VerifyAndProceed"
    const val PennyDrop = "PennyDrop"
    const val Transaction = "Transaction"
    const val AutopayMethod = "autopayMethod"
    const val FeatureFlow = "FeatureFlow"
    const val UserLifecycle = "UserLifecycle"
    const val Status = "Status"
    const val SavingFrequency = "SavingFrequency"
    const val AuthWorkflowType = "AuthWorkflowType"

    object FeatureFlows {
        const val SetupDailySaving = "SetupDailySaving"
        const val UpdateDailySaving = "UpdateDailySaving"
        const val DailySavingAbandon = "DailySavingAbandon"
        const val RetryDailySaving = "RetryDailySaving"
        const val SetupRoundoff = "SetupRoundoff"
        const val AutomateRoundoff = "AutomateRoundoff"
        const val MonthlySavingsPlan = "SetupSavingsPlan"
        const val WeeklySavingsPlan = "SetupSavingsPlan"
        const val RetrySetupSavingsPlan = "RetrySetupSavingsPlan"
        const val UpdateSavingsPlan = "UpdateSavingsPlan"
        const val SavingsGoal = "SavingsGoal"
    }

    object SavingFrequencies {
        const val Daily = "Daily"
        const val Weekly = "Weekly"
        const val Monthly = "Monthly"
    }

    const val MandatePaymentScreen_Shown = "MandatePaymentScreen_Shown"
    const val Clicked_UPIApp_MandatePaymentScreen = "Clicked_UPIApp_MandatePaymentScreen"
    const val Clicked_UPIApp_MandatePaymentScreen_Shown =
        "Clicked_UPIApp_MandatePaymentScreen_Shown"
    const val Shown_AutopayCompleteScreen = "Shown_AutopayCompleteScreen"
    const val Shown_AvailableUpiApps = "Shown_AvailableUpiApps"
    const val Shown_Mandate_Education_Video ="Shown_Mandate_Education_Video"
    const val Shown_Preferred_Bank_Card = "Shown_Preferred_Bank_Card"
    const val Shown_Paytm_CashBack_Banner = "Shown_Paytm_CashBack_Banner"

}