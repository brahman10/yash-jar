package com.jar.app.feature_round_off.shared.util

import com.jar.app.core_base.util.BaseConstants

object RoundOffConstants {

    const val OPEN_PAUSE_ROUND_OFF_DIALOG = "OPEN_PAUSE_ROUND_OFF_DIALOG"
    const val PAUSE_ROUND_OFFS = "PAUSE_ROUND_OFFS"
    const val DISABLE_ROUND_OFF = "DISABLE_ROUND_OFF"
    const val ROUND_OFF_AUTO_SAVE_DISABLED = "ROUND_OFF_AUTO_SAVE_DISABLED"

    object Illustration {
        const val SMS_PERMISSION = "/Images/RoundOff/sms_permission.webp"
        const val ROUND_OFF_ACTIVATED = "/Images/RoundOff/round_offs_activated.webp"
        const val INVEST_MORE_MONEY = "/Images/RoundOff/invest_more_money.webp"
    }

    object Lottie {
        const val ROUND_OFF_EDUCATION =
            "${BaseConstants.CDN_BASE_URL}/LottieFiles/RoundOff/round-off-education.lottie"
        const val ADDING_ROUND_OFF =
            "${BaseConstants.CDN_BASE_URL}/LottieFiles/RoundOff/adding_round_off.lottie"
        const val ROUND_OFF_MANUAL_PAYMENT = "/LottieFiles/RoundOff/round_off_manual_payment.json"
        const val ROUND_OFF_AUTOMATIC_PAYMENT =
            "/LottieFiles/RoundOff/round_off_automatic_payment.json"
    }

    enum class RoundOffSetupFlowViewType {
        TEXT_ONLY, LOTTIE
    }

    internal object Endpoints {
        const val FETCH_INITIAL_ROUND_OFF_DATA = "v1/api/payments/initialRoundoffsData"
        const val FETCH_PAYMENT_TRANSACTION_BREAKUP = "v1/api/wallet/transactions"
        const val MAKE_DETECTED_SPEND_PAYMENT = "v2/api/payments/initiate"
        const val FETCH_ROUND_OFF_SETUP_STEPS = "v2/api/dashboard/static"
    }
}