package com.jar.app.feature_gold_sip.shared.util

import com.jar.app.core_base.util.BaseConstants

object GoldSipConstants {
    const val DAY_OR_DATE_UPDATED = "DAY_OR_DATE_UPDATED"
    const val SELECT_DAY_OR_DATE_BOTTOM_SHEET_CLOSED = "SELECT_DAY_OR_DATE_BOTTOM_SHEET_CLOSED"
    const val UPDATE_SIP_BOTTOM_SHEET_CLOSED = "UPDATE_SIP_BOTTOM_SHEET_CLOSED"
    const val PAUSE_SIP = "PAUSE_SIP"

    object LottieUrl {
        const val GOLD_SIP_INFO =
            "${BaseConstants.CDN_BASE_URL}/LottieFiles/GoldSip/savings_plan_info.lottie"
    }

    object IllustrationUrl {
        const val CONTINUE_SAVINGS_PLAN =
            "${BaseConstants.CDN_BASE_URL}/Images/GoldSip/ic_continue_savings_plan.png"
    }

    internal object Endpoints {
        const val FETCH_GOLD_SIP_INTRO = "v2/api/dashboard/static"
        const val FETCH_IS_ELIGIBLE_FOR_GOLD_SIP = "v2/api/dashboard/static"
        const val FETCH_GOLD_SIP_DETAILS = "v1/api/user/settings/goldSipDetails"
        const val UPDATE_GOLD_SIP_DETAILS = "v1/api/user/settings/updateGoldSip"
        const val DISABLE_GOLD_SIP = "v1/api/user/settings/disableGoldSip"
        const val FETCH_GOLD_SIP_SETUP_INFO = "v1/api/user/settings/getGoldSipSetupInfo"
    }
}