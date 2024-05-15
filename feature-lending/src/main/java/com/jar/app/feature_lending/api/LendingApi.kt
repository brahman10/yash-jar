package com.jar.app.feature_lending.api


/**
 * Lending Api (to be used by other modules)
 * **/
interface LendingApi {
    fun openLendingFlowV2(flowType: String, apiCallback: (String?,Boolean) -> Unit)
    fun openRealTimeLendingFlow(flowType: String, apiStateCallback: (String?,Boolean) -> Unit)
    fun openRealTimeFindingBestOfferScreen(flowType: String)

    fun openLendingRepeatWithdrawal(flowType: String)
    fun openCheckCreditReport(flowType: String)

    fun openEmiCalculatorLaunchingSoonScreen()
}