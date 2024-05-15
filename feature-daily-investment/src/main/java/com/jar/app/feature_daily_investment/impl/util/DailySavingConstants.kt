package com.jar.app.feature_daily_investment.impl.util

import com.jar.app.core_base.util.BaseConstants

object DailySavingConstants {

    const val FromScreen = "fromScreen"
    const val AbandonScreen = "Abandon screen"
    const val CrossPromotion = "CrossPromotion"
    const val Amount = "Amount"
    const val BREAK_DOWN_TIME_SELECTION = "BREAK_DOWN_TIME_SELECTION"
    const val DAILY_SAVING_AMOUNT_EDIT = "DAILY_SAVING_AMOUNT_EDIT"
    const val EXIT_DAILY_SAVING_AMOUNT_SELECTION_FLOW =
        "EXIT_DAILY_SAVING_AMOUNT_SELECTION_FLOW"
    const val OPEN_PAUSE_SAVINGS_DIALOG = "open_pause"
    const val DISABLE_DAILY_SAVING = "DISABLE_DAILY_SAVING"

    object LottieUrl {
        const val DS_UPDATE_SCREEN_LOTTIE =
            "${BaseConstants.CDN_BASE_URL}/LottieFiles/DailyInvestment/ds_update_graph.lottie"
    }

    const val JAR_1_MONTHS = 3
    const val JAR_2_MONTHS = 6
    const val JAR_3_MONTHS = 9

    const val DAYS_IN_MONTH = 30

    const val DEFAULT_DS_AMOUNT = 30f

    const val VIEW_TYPE_CURRENT_MANDATE = 1
    const val VIEW_TYPE_OTHER_MANDATE = 2
    const val BACK = "Back"
    const val iS_VIEWED = "isViewed"
    const val COUNT = "count"
    const val ONBOARDING = "ONBOARDING"
    const val OnboardingDailySaving = "Daily Investment Onboarding Default"
    const val SetupDailySaving = "Setup Daily Saving"
    const val Update_daily_savings = "Daily Savings Update FLow"

    object DailySavingVariants {
        const val V1 = "v1"
        const val V2 = "v2"
        const val V3 = "v3"
        const val V4 = "v4"
    }
}

