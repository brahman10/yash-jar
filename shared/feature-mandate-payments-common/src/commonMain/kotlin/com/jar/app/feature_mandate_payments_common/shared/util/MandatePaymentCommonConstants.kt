package com.jar.app.feature_mandate_payments_common.shared.util

object MandatePaymentCommonConstants {

    const val MANDATE_PAYMENT_RESPONSE_FROM_SDK = "MANDATE_PAYMENT_RESPONSE_FROM_SDK"

    const val MANDATE_PAYMENT_STATUS_FROM_API = "MANDATE_PAYMENT_STATUS_FROM_API"

    const val BACK_PRESSED_FROM_PAYMENT_SCREEN = "BACK_PRESSED_FROM_PAYMENT_SCREEN"

    const val SAVE_DAILY = "SAVE_DAILY"

    internal object Endpoints {
        const val VERIFY_VPA = "v2/api/payments/verify-vpa"
        const val INITIATE_MANDATE_PAYMENT = "v2/api/autopay/initiate"
        const val FETCH_MANDATE_PAYMENT_STATUS = "v2/api/autopay/status"
        const val FETCH_MANDATE_EDUCATION = "v2/api/dashboard/static"
        const val FETCH_PREFERRED_BANK = "v1/api/autopay/autopayPreferredBank"
        const val FETCH_ENABLED_PAYMENT_METHODS = "v2/api/paymentMethods/enabled"
        const val FETCH_RECENTLY_USED_PAYMENT_METHODS = "v2/api/autopay/recently-used"
    }

    enum class MandateStaticContentType{
        DAILY_SAVINGS_MANDATE_EDUCATION,
        ROUND_OFFS_MANDATE_EDUCATION,
        WEEKLY_SAVINGS_MANDATE_EDUCATION,
        MONTHLY_SAVINGS_MANDATE_EDUCATION,
        INSURANCE_AUTOPAY_SETUP
    }
}