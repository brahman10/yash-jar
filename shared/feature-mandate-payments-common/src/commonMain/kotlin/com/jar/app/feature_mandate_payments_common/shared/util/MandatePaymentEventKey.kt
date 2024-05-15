package com.jar.app.feature_mandate_payments_common.shared.util

object MandatePaymentEventKey {

    const val Button = "Button"
    const val Action = "Action"
    const val Data = "Data"
    const val Shown = "Shown"
    const val MandatePaymentGateway = "MandatePaymentGateway"
    const val UpiApp = "UpiApp"
    const val PaymentMethod = "PaymentMethod"
    const val MandatePayment = "MandatePayment"
    const val AvailableUpiAppName = "AvailableUpiAppName"
    const val AvailableUpiAppPackageName = "AvailableUpiAppPackageName"
    const val UpiAppsShown = "UpiAppsShown"
    const val UpiId = "UpiId"
    const val Proceed = "Proceed"
    const val Error = "Error"
    const val PreferredBank = "PreferredBank"
    const val BankName = "bankName"
    const val Back = "Back"
    const val Close = "Close"
    const val BestAmount = "BestAmount"
    const val MandateAmount = "MandateAmount"
    const val VerifyAndProceed = "VerifyAndProceed"
    const val PennyDrop = "PennyDrop"
    const val Transaction = "Transaction"
    const val AutopayMethod = "autopayMethod"
    const val FeatureFlow = "FeatureFlow"
    const val UserLifecycle = "UserLifecycle"
    const val Coupon = "Coupon"
    const val Status = "Status"
    const val SavingFrequency = "SavingFrequency"
    const val AuthWorkflowType = "AuthWorkflowType"

    object FeatureFlows {
        const val SetupDailySaving = "SetupDailySaving"
        const val UpdateDailySaving = "UpdateDailySaving"
        const val RetryDailySaving = "RetryDailySaving"
        const val SetupRoundoff = "SetupRoundoff"
        const val AutomateRoundoff = "AutomateRoundoff"
        const val SetupSavingsPlan = "SetupSavingsPlan"
        const val RetrySetupSavingsPlan = "RetrySetupSavingsPlan"
        const val UpdateSavingsPlan = "UpdateSavingsPlan"
    }

    object SavingFrequencies {
        const val Daily = "Daily"
        const val Weekly = "Weekly"
        const val Monthly = "Monthly"
    }

    const val MandatePaymentScreen_Shown = "MandatePaymentScreen_Shown"
    const val DsCoupon = "DS Coupon"
    const val Clicked_UPIApp_MandatePaymentScreen = "Clicked_UPIApp_MandatePaymentScreen"
    const val Clicked_UPIApp_MandatePaymentScreen_Shown =
        "Clicked_UPIApp_MandatePaymentScreen_Shown"
    const val Shown_AutopayCompleteScreen = "Shown_AutopayCompleteScreen"
    const val Shown_AvailableUpiApps = "Shown_AvailableUpiApps"
    const val Mandate_UpiAppInitiated = "Mandate_UpiAppInitiated"
    const val Shown_Mandate_Education_Video = "Shown_Mandate_Education_Video"
    const val Shown_Preferred_Bank_Card = "Shown_Preferred_Bank_Card"
    const val Shown_Paytm_CashBack_Banner = "Shown_Paytm_CashBack_Banner"

}