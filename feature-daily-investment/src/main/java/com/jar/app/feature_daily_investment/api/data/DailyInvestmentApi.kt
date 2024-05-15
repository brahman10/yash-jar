package com.jar.app.feature_daily_investment.api.data

import com.jar.app.base.data.model.FeatureFlowData

interface DailyInvestmentApi {

    fun openDailySavingsOnboarding(isFromOnboarding: Boolean)

    fun openDailySavingSetupStatusFragment(
        dailySavingAmount: Float,
        fetchAutoInvestStatusResponse: com.jar.app.feature_mandate_payments_common.shared.domain.model.verify_status.FetchMandatePaymentStatusResponse,
        mandatePaymentResultFromSDK: com.jar.app.feature_mandate_payments_common.shared.domain.model.initiate_mandate.MandatePaymentResultFromSDK,
        isFromOnboarding: Boolean = false,
        flowName: String,
        popUpToId: Int?,
        isMandateBottomSheetFlow: Boolean? = false,
        userLifecycle: String?
    )

    fun updateDailySavingAndSetupItsAutopay(
        mandateAmount: Float,
        source: String,
        authWorkflowType: com.jar.app.feature_mandate_payments_common.shared.domain.model.initiate_mandate.MandateWorkflowType,
        newDailySavingAmount: Float,
        popUpToId: Int?,
        userLifecycle: String?
    )

    fun openDailySavingFlow(
        fromSettingsFlow: Boolean,
        featureFlowData: FeatureFlowData,
        shouldOpenDSIntroBS: Boolean = false,
        fromAbandonFlow: Boolean = false,
        popUpToId: Int? = null
    )

    fun openDailySavingEducation(isSetupFlow: Boolean?)

    fun openUpdateDailySavingV2(flow: String)

    fun openSingleHomeFeedCTA(flow: String)

    fun openPreDailySavingAutopay(flowType: String, dsAmount: Int)

    fun openDailySavingOnboardingStories()

    fun openDSCustomOnboardingFragment(
        isFromOnboarding: Boolean,
        version: String?,
        id: Int?,
        fromScreen: String?
    )

    fun initiateDailySavingCustomUIMandateBottomSheet(
        customMandateUiFragmentId: Int,
        newDailySavingAmount: Float,
        mandateWorkflowType: com.jar.app.feature_mandate_payments_common.shared.domain.model.initiate_mandate.MandateWorkflowType,
        flowSource: String,
        customBottomSheetDeeplink: String,
        popUpToId: Int?,
        userLifecycle: String?
    )

    fun openDailyInvestmentUpdateFragment()
}