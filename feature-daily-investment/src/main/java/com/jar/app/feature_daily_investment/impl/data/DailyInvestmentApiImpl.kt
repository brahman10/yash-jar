package com.jar.app.feature_daily_investment.impl.data

import android.net.Uri
import androidx.navigation.NavController
import com.jar.app.base.data.model.FeatureFlowData
import com.jar.app.base.ui.BaseNavigation
import com.jar.app.base.util.DispatcherProvider
import com.jar.app.base.util.encodeUrl
import com.jar.app.base.util.orFalse
import com.jar.app.core_base.data.event.RefreshDailySavingEvent
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.orZero
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.feature_daily_investment.R
import com.jar.app.feature_daily_investment.api.data.DailyInvestmentApi
import com.jar.app.feature_daily_investment.impl.util.DailySavingConstants
import com.jar.app.feature_daily_investment.shared.domain.use_case.UpdateDailyInvestmentStatusUseCase
import com.jar.app.feature_mandate_payment.api.MandatePaymentApi
import com.jar.app.feature_mandate_payment.impl.util.MandatePaymentEventKey
import com.jar.app.feature_mandate_payments_common.shared.domain.model.initiate_mandate.MandatePaymentResultFromSDK
import com.jar.app.feature_mandate_payments_common.shared.domain.model.payment_page.PaymentPageHeaderDetail
import com.jar.app.feature_mandate_payments_common.shared.domain.model.verify_status.FetchMandatePaymentStatusResponse
import com.jar.app.feature_mandate_payments_common.shared.domain.model.verify_status.MandatePaymentProgressStatus
import com.jar.app.feature_mandate_payments_common.shared.util.MandatePaymentCommonConstants
import com.jar.app.feature_savings_common.shared.domain.model.SavingsType
import com.jar.app.feature_savings_common.shared.domain.use_case.FetchUserSavingsDetailsUseCase
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jar_core_network.api.util.collectUnwrapped
import dagger.Lazy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

internal class DailyInvestmentApiImpl @Inject constructor(
    private val fetchUserSavingsDetailsUseCase: FetchUserSavingsDetailsUseCase,
    private val updateDailyInvestmentStatusUseCase: UpdateDailyInvestmentStatusUseCase,
    private val mandatePaymentApi: MandatePaymentApi,
    private val serializer: Serializer,
    navControllerRef: Lazy<NavController>,
    private val appScope: CoroutineScope,
    private val dispatcher: DispatcherProvider,
) : DailyInvestmentApi, BaseNavigation {

    private val navController by lazy {
        navControllerRef.get()
    }
    private var dailyInvestmentJob: Job? = null

    private var mandatePaymentJob: Job? = null

    @Inject
    lateinit var remoteConfigManager: RemoteConfigApi

    override fun openDailySavingsOnboarding(isFromOnboarding: Boolean) {
        val uri = "android-app://com.jar.app/dailySavingOnboardingAnimationFragment"
        navController.navigate(Uri.parse(uri), getNavOptions(shouldAnimate = true))
    }

    override fun openDSCustomOnboardingFragment(
        isFromOnboarding: Boolean,
        version: String?,
        id: Int?,
        fromScreen: String?
    ) {
        val currentEpochTimeMillis = System.currentTimeMillis()
        when (version) {
            DailySavingConstants.DailySavingVariants.V1 -> {
                openSetup(
                    fromSettingsFlow = false,
                    featureFlowData = FeatureFlowData(
                        fromScreen = fromScreen?:DailySavingConstants.ONBOARDING
                    ),
                    popUpToId = id,
                    isFromOnboarding = isFromOnboarding
                )
            }

            DailySavingConstants.DailySavingVariants.V2 -> {
                navController.navigate(
                    Uri.parse("android-app://com.jar.app/dailyInvestOnboardingFragment/$isFromOnboarding"),
                    getNavOptions(
                        shouldAnimate = true,
                        popUpToId = id,
                        inclusive = true
                    )
                )
            }

            DailySavingConstants.DailySavingVariants.V3 -> {
                navController.navigate(
                    Uri.parse("${BaseConstants.InternalDeepLinks.DAILY_ONBOARDING_VARIANT_FRAGMENT}/$version/${isFromOnboarding}/${fromScreen}/${currentEpochTimeMillis}"),
                    getNavOptions(
                        shouldAnimate = true,
                        popUpToId = id,
                        inclusive = true
                    )
                )
            }

            DailySavingConstants.DailySavingVariants.V4 -> {
                navController.navigate(
                    Uri.parse("${BaseConstants.InternalDeepLinks.DAILY_ONBOARDING_VARIANT_FRAGMENT}/$version/${isFromOnboarding}/${fromScreen}/${currentEpochTimeMillis}"),
                    getNavOptions(
                        shouldAnimate = true,
                        popUpToId = id,
                        inclusive = true
                    )
                )
            }
        }
    }

    override fun openDailySavingSetupStatusFragment(
        dailySavingAmount: Float,
        fetchAutoInvestStatusResponse: FetchMandatePaymentStatusResponse,
        mandatePaymentResultFromSDK: MandatePaymentResultFromSDK,
        isFromOnboarding: Boolean,
        flowName: String,
        popUpToId: Int?,
        isMandateBottomSheetFlow: Boolean?,
        userLifecycle: String?
    ) {
        if (navController.currentDestination?.id != R.id.dailySavingSetupStatusFragment) {
            val encodedFetchAutoInvestStatusResponse =
                encodeUrl(serializer.encodeToString(fetchAutoInvestStatusResponse))
            val encodedMandatePaymentResultFromSDK =
                encodeUrl(serializer.encodeToString(mandatePaymentResultFromSDK))
            val dailySavingSetupStatusData = encodeUrl(
                serializer.encodeToString(
                    DailySavingSetupStatusData(
                        dailySavingAmount,
                        isFromOnboarding,
                        flowName,
                        isMandateBottomSheetFlow,
                        userLifecycle
                    )
                )
            )
            navController.navigate(
                Uri.parse("android-app://com.jar.app/dailySavingSetupStatus/$encodedFetchAutoInvestStatusResponse/$encodedMandatePaymentResultFromSDK/$dailySavingSetupStatusData"),
                if (popUpToId != null)
                    getNavOptions(shouldAnimate = true, popUpToId = popUpToId, inclusive = true)
                else
                    getNavOptions(shouldAnimate = true)
            )
        }
    }

    override fun updateDailySavingAndSetupItsAutopay(
        mandateAmount: Float,
        source: String,
        authWorkflowType: com.jar.app.feature_mandate_payments_common.shared.domain.model.initiate_mandate.MandateWorkflowType,
        newDailySavingAmount: Float,
        popUpToId: Int?,
        userLifecycle: String?
    ) {
        mandatePaymentJob?.cancel()
        mandatePaymentJob = appScope.launch(dispatcher.main) {
            mandatePaymentApi.initiateMandatePayment(
                paymentPageHeaderDetails = PaymentPageHeaderDetail(
                    toolbarHeader = navController.context.getString(R.string.feature_daily_savings),
                    title = navController.context.getString(
                        R.string.daily_investment_lets_automate_your_rs_d,
                        newDailySavingAmount.toInt()
                    ),
                    toolbarIcon = 0,
                    featureFlow = source,
                    savingFrequency = MandatePaymentEventKey.SavingFrequencies.Daily,
                    userLifecycle = userLifecycle,
                    mandateSavingsType = MandatePaymentCommonConstants.MandateStaticContentType.DAILY_SAVINGS_MANDATE_EDUCATION
                ),
                initiateMandatePaymentRequest = com.jar.app.feature_mandate_payments_common.shared.domain.model.initiate_mandate.InitiateMandatePaymentRequest(
                    mandateAmount = mandateAmount.orZero(),
                    authWorkflowType = authWorkflowType,
                    subscriptionType = SavingsType.DAILY_SAVINGS.name,
                    subsSetupType = if (source == MandatePaymentEventKey.FeatureFlows.UpdateDailySaving) BaseConstants.SavingsSubscriptionSetupType.UPDATE.name else BaseConstants.SavingsSubscriptionSetupType.SETUP.name
                )
            ).collectUnwrapped(
                onSuccess = { pair ->
                    if (pair.second.getAutoInvestStatus() == com.jar.app.feature_mandate_payments_common.shared.domain.model.verify_status.MandatePaymentProgressStatus.SUCCESS) {
                        updateDailyInvestmentStatusUseCase.updateDailyInvestmentStatus(amount = newDailySavingAmount)
                            .collect {
                                EventBus.getDefault().post(RefreshDailySavingEvent())
                                openDailySavingSetupStatusFragment(
                                    dailySavingAmount = newDailySavingAmount.orZero(),
                                    fetchAutoInvestStatusResponse = pair.second,
                                    mandatePaymentResultFromSDK = pair.first,
                                    flowName = source,
                                    popUpToId = popUpToId,
                                    userLifecycle = userLifecycle
                                )
                            }
                    } else {
                        EventBus.getDefault().post(RefreshDailySavingEvent())
                        openDailySavingSetupStatusFragment(
                            newDailySavingAmount.orZero(),
                            fetchAutoInvestStatusResponse = pair.second,
                            mandatePaymentResultFromSDK = pair.first,
                            flowName = source,
                            popUpToId = popUpToId,
                            userLifecycle = userLifecycle
                        )
                    }
                },
                onError = { _, _ -> }
            )
        }
    }

    override fun openDailySavingFlow(
        fromSettingsFlow: Boolean,
        featureFlowData: FeatureFlowData,
        shouldOpenDSIntroBS: Boolean,
        fromAbandonFlow: Boolean,
        popUpToId: Int?
    ) {
        dailyInvestmentJob?.cancel()
        dailyInvestmentJob = appScope.launch {
            fetchUserSavingsDetailsUseCase.fetchSavingsDetails(SavingsType.DAILY_SAVINGS)
                .collect(onSuccess = {
                    withContext(dispatcher.main) {
                        if (it.enabled.orFalse()) {
                            openDailySavingsV2Settings()
                        } else {
                            val status = it.subscriptionStatus
                                ?: MandatePaymentProgressStatus.SUCCESS.name
                            val currentTime = System.currentTimeMillis()

                            if (remoteConfigManager.shouldShowDailySavingsV2Flow()) {
                                val data = encodeUrl(serializer.encodeToString(featureFlowData))
                                val uri =
                                    "android-app://com.jar.app/dailySavingAmountSelection/$data/$shouldOpenDSIntroBS/$fromAbandonFlow/$currentTime"
                                navController.navigate(
                                    Uri.parse(uri),
                                    getNavOptions(
                                        shouldAnimate = true,
                                        popUpToId = popUpToId,
                                        inclusive = true
                                    ),
                                )
                            } else {
                                openSetup(
                                    fromSettingsFlow,
                                    MandatePaymentProgressStatus.valueOf(status),
                                    featureFlowData,
                                    popUpToId
                                )
                            }
                        }
                    }
                })
        }
    }

    private fun openSetup(
        fromSettingsFlow: Boolean,
        status: MandatePaymentProgressStatus = MandatePaymentProgressStatus.SUCCESS,
        featureFlowData: FeatureFlowData,
        popUpToId: Int? = null,
        isFromOnboarding: Boolean = false,
    ) {
        if (fromSettingsFlow) {
            val uri = "android-app://com.jar.app/preSetupFragment/${status.name}"
            navController.navigate(
                Uri.parse(uri),
                getNavOptions(shouldAnimate = true, popUpToId = popUpToId, inclusive = true)
            )
        } else {
            openDSCustomOnboardingFragment(
                isFromOnboarding = isFromOnboarding,
                version = DailySavingConstants.DailySavingVariants.V4,
                id = null,
                fromScreen = featureFlowData.fromScreen
            )
        }
    }

    private fun openDailySavingsV2Settings() {
        val uri = "android-app://com.jar.app/dailySavings"
        navController.navigate(Uri.parse(uri), getNavOptions(shouldAnimate = true))
    }

    override fun openDailySavingEducation(isSetupFlow: Boolean?) {
        val uri = "android-app://com.jar.app/dailySavingEducation/${isSetupFlow ?: true}"
        navController.navigate(Uri.parse(uri), getNavOptions(shouldAnimate = true))
    }

    override fun openUpdateDailySavingV2(flow: String) {
        val uri = "android-app://com.jar.app/updateDailySavingV2/$flow"
        navController.navigate(Uri.parse(uri), getNavOptions(shouldAnimate = true))
    }

    override fun openSingleHomeFeedCTA(flow: String) {
        navController.navigate(Uri.parse(flow.substring(1)), getNavOptions(shouldAnimate = true))
    }

    override fun openPreDailySavingAutopay(flowType: String, dsAmount: Int) {
        val uri = "android-app://com.jar.app/preDailySavingAutopay/$flowType/$dsAmount"
        navController.navigate(Uri.parse(uri), getNavOptions(shouldAnimate = true))
    }

    override fun openDailySavingOnboardingStories() {
        val uri = "android-app://com.jar.app/dailySavingOnboardingStoriesFragment"
        navController.navigate(Uri.parse(uri), getNavOptions(shouldAnimate = true))
    }

    override fun initiateDailySavingCustomUIMandateBottomSheet(
        customMandateUiFragmentId: Int,
        newDailySavingAmount: Float,
        mandateWorkflowType: com.jar.app.feature_mandate_payments_common.shared.domain.model.initiate_mandate.MandateWorkflowType,
        flowSource: String,
        customBottomSheetDeeplink: String,
        popUpToId: Int?,
        userLifecycle: String?
    ) {
        mandatePaymentJob?.cancel()
        mandatePaymentJob = appScope.launch(Dispatchers.Main) {
            mandatePaymentApi.initiateMandatePaymentWithCustomUI(
                customMandateUiFragmentId = customMandateUiFragmentId,
                fragmentDeepLink = customBottomSheetDeeplink,
                paymentPageHeaderDetails = PaymentPageHeaderDetail(
                    toolbarHeader = navController.context.getString(R.string.feature_daily_savings),
                    toolbarIcon = R.drawable.feature_daily_investment_ic_daily_saving_tab,
                    title = navController.context.getString(R.string.feature_daily_investment_auto_save),
                    featureFlow = flowSource,
                    userLifecycle = userLifecycle,
                    savingFrequency = MandatePaymentEventKey.SavingFrequencies.Daily,
                    mandateSavingsType = MandatePaymentCommonConstants.MandateStaticContentType.DAILY_SAVINGS_MANDATE_EDUCATION,
                ),
                initiateMandatePaymentRequest = com.jar.app.feature_mandate_payments_common.shared.domain.model.initiate_mandate.InitiateMandatePaymentRequest(
                    mandateAmount = newDailySavingAmount,
                    authWorkflowType = mandateWorkflowType,
                    subscriptionType = SavingsType.DAILY_SAVINGS.name
                )
            ).collectUnwrapped(
                onSuccess = { pair ->
                    if (pair.second.getAutoInvestStatus() == MandatePaymentProgressStatus.SUCCESS) {
                        updateDailyInvestmentStatusUseCase.updateDailyInvestmentStatus(amount = newDailySavingAmount)
                            .collect {
                                EventBus.getDefault().post(RefreshDailySavingEvent())
                                openDailySavingSetupStatusFragment(
                                    dailySavingAmount = newDailySavingAmount.orZero(),
                                    fetchAutoInvestStatusResponse = pair.second,
                                    mandatePaymentResultFromSDK = pair.first,
                                    flowName = flowSource,
                                    popUpToId = popUpToId,
                                    userLifecycle = userLifecycle,
                                )
                            }
                    } else {
                        EventBus.getDefault().post(RefreshDailySavingEvent())
                        openDailySavingSetupStatusFragment(
                            newDailySavingAmount.orZero(),
                            fetchAutoInvestStatusResponse = pair.second,
                            mandatePaymentResultFromSDK = pair.first,
                            flowName = flowSource,
                            popUpToId = popUpToId,
                            userLifecycle = userLifecycle,
                        )
                    }
                    EventBus.getDefault()
                        .post(RefreshDailySavingEvent(isSetupFlow = true))
                    openDailySavingSetupStatusFragment(
                        dailySavingAmount = newDailySavingAmount,
                        fetchAutoInvestStatusResponse = pair.second,
                        mandatePaymentResultFromSDK = pair.first,
                        flowName = MandatePaymentEventKey.FeatureFlows.SetupDailySaving,
                        popUpToId = popUpToId,
                        isMandateBottomSheetFlow = true,
                        isFromOnboarding = flowSource == DailySavingConstants.ONBOARDING,
                        userLifecycle = userLifecycle,
                    )
                },
                onError = { _, _ -> }
            )
        }
    }

    override fun openDailyInvestmentUpdateFragment(){
        val uri = "android-app://com.jar.app/updateDailySavingsV3Fragment"
        navController.navigate(Uri.parse(uri), getNavOptions(shouldAnimate = true))
    }

}