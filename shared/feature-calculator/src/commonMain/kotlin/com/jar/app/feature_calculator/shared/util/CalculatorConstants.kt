package com.jar.app.feature_calculator.shared.util

import com.jar.app.core_base.util.BaseConstants

object CalculatorConstants {
     object LottieUrls {
        const val EMI_CALCULATOR =
            BaseConstants.CDN_BASE_URL + "/LottieFiles/Lending_Feature/EMI_CAL_Loader.lottie" //Get More loan animation
    }

    internal object Endpoints {
        const val FETCH_LENDING_CALCULATOR_DATA = "v2/api/lending/features/calculator"
        const val FETCH_GOLD_CALCULATOR_DATA = "v1/api/feature_calculator/savings"
    }

    object EventKey {


        const val EmiCalculator_MainScreenLaunched = "EmiCalculator_MainScreenLaunched"
        const val EmiCalculator_BackButtonClicked = "EmiCalculator_BackButtonClicked"
        const val EmiCalculator_NeedHelpClicked = "EmiCalculator_NeedHelpClicked"
        const val SavingsCalculator_Shown = "SavingsCalculator_Shown"
        const val SavingsCalculator_Clicked = "SavingsCalculator_Clicked"



        //Keys
        const val action = "action"
        const val type="type"
        const val screen_name = "screen_name"
        const val amount = "amount"
        const val tenure = "tenure"
        const val interest = "interest"
        const val monthly_emi="monthly_emi"
        const val tenure_type="tenure_type"
        const val Time_Period="Time_Period"


        //Values
        const val loading_screen_shown = "loading_screen_shown"
        const val calculator_screen_shown = "calculator_screen_shown"
        const val launching_soon_screen_shown = "launching_soon_screen_shown"
        const val amount_changed = "amount_changed"
        const val interest_changed = "interest_changed"
        const val Growth_Rate = "Growth_Rate"
        const val tenure_changed = "tenure_changed"
        const val tenure_type_changed = "tenure_type_changed"
        const val calculator_screen_updated = "calculator_screen_updated"
        const val get_instant_loan_clicked = "get_instant_loan_clicked"
        const val go_back_clicked = "go_back_clicked"
        const val calculator_screen = "calculator_screen"
        const val launching_soon_screen = "launching_soon_screen"
        const val month = "month"
        const val year = "year"
        const val from = "from"
        const val to = "to"
        const val slider = "slider"
        const val typed = "typed"
        const val FAQ = "FAQ"
    }
}