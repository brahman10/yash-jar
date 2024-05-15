package com.jar.app.feature_daily_investment_cancellation.impl.ui.intro_screen

import android.annotation.SuppressLint
import android.app.Application
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.jar.android.feature_post_setup.api.PostSetupApi
import com.jar.app.base.data.event.HandleDeepLinkEvent
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.util.getFormattedDate
import com.jar.app.base.util.isPackageInstalled
import com.jar.app.base.util.orFalse
import com.jar.app.core_analytics.EventKey
import com.jar.app.core_analytics.EventKey.is_Permanently_Cancel_flow
import com.jar.app.core_base.data.event.RefreshDailySavingEvent
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.orZero
import com.jar.app.core_compose_ui.base.BaseComposeFragment
import com.jar.app.core_compose_ui.views.ScreenTopBannerForRestart
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.feature_daily_investment.api.data.DailyInvestmentApi
import com.jar.app.feature_daily_investment.api.domain.event.SetupMandateEvent
import com.jar.app.feature_daily_investment_cancellation.impl.ui.component.DailyInvestmentSetupDetailsBlock
import com.jar.app.feature_daily_investment_cancellation.impl.ui.component.DailyInvestmentStepsFeaturesDetailsBlock
import com.jar.app.feature_daily_investment_cancellation.impl.ui.component.DailySavingFeaturesBoxWithClickAction
import com.jar.app.feature_daily_investment_cancellation.impl.ui.component.IntroScreenToolBar
import com.jar.app.feature_daily_investment_cancellation.impl.ui.component.SecondPartOfSpendTrackerScreenInfo
import com.jar.app.feature_daily_investment_cancellation.impl.ui.component.StopDailySavingBottomSheet
import com.jar.app.feature_daily_investment_cancellation.impl.ui.component.TotalAmountSavedDetailsBlock
import com.jar.app.feature_daily_investment_cancellation.impl.ui.pause_daily_saving.PauseDailySavingBottomSheetViewModel
import com.jar.app.feature_daily_investment_cancellation.impl.ui.pause_daily_saving.RenderPauseDailySavingStaticBottomSheet
import com.jar.app.feature_daily_investment_cancellation.impl.ui.stop_daily_saving.RenderStopDailySavingKnowledgeBottomSheet
import com.jar.app.feature_daily_investment_cancellation.impl.ui.stop_daily_saving.RenderStopDailySavingStaticBottomSheet
import com.jar.app.feature_daily_investment_cancellation.impl.ui.stop_daily_saving.StopDailySavingBottomSheetViewModel
import com.jar.app.feature_daily_investment_cancellation.impl.util.DailyInvestmentCancellationEnum
import com.jar.app.feature_daily_investment_cancellation.impl.util.DailyInvestmentCancellationEventKey
import com.jar.app.feature_daily_investment_cancellation.impl.util.DailyInvestmentCancellationEventKey.Button_type
import com.jar.app.feature_daily_investment_cancellation.impl.util.DailyInvestmentCancellationEventKey.DSCancellation_PageClicked
import com.jar.app.feature_daily_investment_cancellation.impl.util.DailyInvestmentCancellationEventKey.DSCancellation_PageLaunched
import com.jar.app.feature_daily_investment_cancellation.impl.util.DailyInvestmentCancellationEventKey.isbottomreached
import com.jar.app.feature_daily_investment_cancellation.impl.util.DailyInvestmentCancellationKey
import com.jar.app.feature_daily_investment_cancellation.impl.util.DailyInvestmentCancellationKey.DailyInvestmentSettingsV2
import com.jar.app.feature_daily_investment_cancellation.impl.util.DailyInvestmentPauseKey
import com.jar.app.feature_daily_investment_cancellation.impl.util.DailyInvestmentStatusScreen
import com.jar.app.feature_daily_investment_cancellation.impl.util.DailyInvestmentStopKey
import com.jar.app.feature_daily_investment_cancellation.impl.util.ProgressScreenData
import com.jar.app.feature_daily_investment_cancellation.shared.domain.model.StatisticsContent
import com.jar.app.feature_daily_investment_tempering.R
import com.jar.app.feature_kyc.api.KycApi
import com.jar.app.feature_mandate_payment.BuildConfig
import com.jar.app.feature_mandate_payment.impl.util.MandatePaymentEventKey
import com.jar.app.feature_mandate_payments_common.shared.domain.model.initiate_mandate.MandateWorkflowType
import com.jar.internal.library.jar_core_network.api.util.collectUnwrapped
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.Lazy
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.Date
import javax.inject.Inject

@AndroidEntryPoint
class DailyInvestmentSettingsV2Fragment : BaseComposeFragment() {
    private val viewModel by viewModels<DailyInvestmentSettingsV2ViewModel> { defaultViewModelProviderFactory }
    private val stopDailySavingViewModel by viewModels<StopDailySavingBottomSheetViewModel> { defaultViewModelProviderFactory }
    private val pauseDailySavingViewModel by viewModels<PauseDailySavingBottomSheetViewModel> { defaultViewModelProviderFactory }


    @Inject
    lateinit var dailyInvestmentApi: DailyInvestmentApi

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    @Inject
    lateinit var mApp: Application

    @Inject
    lateinit var prefs: PrefsApi

    @Inject
    lateinit var kycApi: KycApi

    @Inject
    lateinit var postSetupApiRef: Lazy<PostSetupApi>

    private val postSetupApi by lazy {
        postSetupApiRef.get()
    }


    private var backPressCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            analyticsHandler.postEvent(
                DSCancellation_PageClicked,
                mapOf(
                    Button_type to DailyInvestmentStatusScreen.Back,
                    DailyInvestmentCancellationKey.State to viewModel.pauseState
                )
            )
            popBackStack()
        }
    }

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun setup(savedInstanceState: Bundle?) {
        registerBackPressDispatcher()
        observeApiResponse()
    }

    fun getData() {
        viewModel.fetchUserDSDetailsFlow()
        viewModel.fetchSeekBarDataFlow()
        viewModel.fetchSettingsFragmentDataFlow()
        stopDailySavingViewModel.cancellationKnowledgeBottomSheetData()
        stopDailySavingViewModel.cancellationStatisticsBottomSheetData()
        pauseDailySavingViewModel.fetchPauseDetailsDataFlow()
    }


    private fun navigateToFaqScreen() {
        analyticsHandler.postEvent(
            DSCancellation_PageClicked, mapOf(
                Button_type to DailyInvestmentStatusScreen.FAQ,
                DailyInvestmentCancellationKey.State to viewModel.pauseState
            )
        )
        EventBus.getDefault()
            .post(HandleDeepLinkEvent(BaseConstants.BASE_EXTERNAL_DEEPLINK + BaseConstants.ExternalDeepLinks.HELP_SUPPORT))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
        getData()
    }

    @SuppressLint("StateFlowValueCalledInComposition")
    @Preview
    @Composable
    @OptIn(ExperimentalMaterialApi::class)
    @Preview(showSystemUi = true, showBackground = true)
    override fun RenderScreen() {
        val dailyInvestmentSettingsDetails by viewModel.settingsScreenDataFlow.collectAsState(initial = null)
        val userSavingDetails by viewModel.userSavingDetailsFlow.collectAsState(initial = null)
        val dailySavingSetupInfo by viewModel.dsSeekBarFlow.collectAsState(initial = null)
        val pauseStatusValue = remember { derivedStateOf { userSavingDetails?.data?.data?.pauseStatus } }.value?.savingsPaused
        val dailySavingAmount = remember { derivedStateOf { userSavingDetails?.data?.data?.subscriptionAmount } }
        val totalUpiApps = remember { mutableStateOf(totalUpiApps()) }

        val coroutineScope = rememberCoroutineScope()
        val bottomSheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden, skipHalfExpanded = true)
        val stopDailySavingBottomSheetDetails = stopDailySavingViewModel.confirmActionDetailsFlow.collectAsState(initial = null)
        val pauseDailySavingBottomSheetDetails = pauseDailySavingViewModel.pauseDetailsFlow.collectAsState(initial = null)
        val isPauseBottomSheetClicked = remember { mutableStateOf(false) }
        val whichBottomSheetType = remember { mutableStateOf<BottomSheetType>(BottomSheetType.NONE) }
        val isV2StopBottomSheetClicked = remember { mutableStateOf(false) }
        val scrollState = rememberLazyListState()
        val visibleItemIndex = remember { derivedStateOf { scrollState.firstVisibleItemIndex } }

        if (visibleItemIndex.value == 1) {
            analyticsHandler.postEvent(
                DSCancellation_PageLaunched, mapOf(
                    isbottomreached to true
                ), true
            )
        }


        ModalBottomSheetLayout(
            sheetState = bottomSheetState,
            sheetContent = {
                when (whichBottomSheetType.value) {
                    BottomSheetType.PAUSE_BOTTOM_SHEET -> {
                        pauseDailySavingBottomSheetDetails.value?.data?.data?.let {
                            RenderPauseDailySavingStaticBottomSheet(
                                pauseDetails = it,
                                viewModel = pauseDailySavingViewModel,
                                onDismissClick = {
                                    coroutineScope.launch {
                                        bottomSheetState.hide()
                                        whichBottomSheetType.value = BottomSheetType.NONE
                                    }
                                },
                                childFragmentManager = childFragmentManager,
                                requireContext = requireContext(),
                                analyticsHandler = analyticsHandler
                            )
                        }
                    }

                    BottomSheetType.STATIC_STOP_BOTTOM_SHEET -> {
                        stopDailySavingBottomSheetDetails.value?.data?.data?.let {
                            RenderStopDailySavingStaticBottomSheet(
                                stopDailySavingBottomSheetDetails = it,
                                onDismissClick = {
                                    coroutineScope.launch {
                                        bottomSheetState.hide()
                                    }
                                },
                                onContinueDailySavingClicked = {
                                    coroutineScope.launch {
                                        continueDailySavingClicked(isPauseBottomSheetClicked,
                                            stopDailySavingBottomSheetDetails.value?.data?.data?.statisticsContent
                                        )
                                    }
                                },
                                onStopDailySavingClicked = {
                                    coroutineScope.launch {
                                        stopDailySavingClicked(isPauseBottomSheetClicked,
                                            stopDailySavingBottomSheetDetails.value?.data?.data?.statisticsContent
                                        )
                                    }
                                },
                                analyticsHandler = analyticsHandler
                            )
                        }
                    }

                    BottomSheetType.KNOWLEDGE_STOP_BOTTOM_SHEET -> {
                        stopDailySavingBottomSheetDetails.value?.data?.data?.let {
                            RenderStopDailySavingKnowledgeBottomSheet(
                                stopDailySavingBottomSheetDetails = it,
                                onDismissClick = {
                                    coroutineScope.launch {
                                        bottomSheetState.hide()
                                    }
                                },
                                onContinueDailySavingClicked = {
                                    coroutineScope.launch {
                                        continueDailySavingClicked(isPauseBottomSheetClicked,
                                            stopDailySavingBottomSheetDetails.value?.data?.data?.statisticsContent
                                        )
                                    }
                                },
                                onStopDailySavingClicked = {
                                    coroutineScope.launch {
                                        stopDailySavingClicked(isPauseBottomSheetClicked,
                                            stopDailySavingBottomSheetDetails.value?.data?.data?.statisticsContent
                                        )
                                    }
                                },
                                analyticsHandler = analyticsHandler
                            )
                        }
                    }

                    BottomSheetType.V2_STOP_BOTTOM_SHEET -> {
                        pauseDailySavingBottomSheetDetails.value?.data?.data?.let {
                            StopDailySavingBottomSheet(
                                pauseDetails = it,
                                onDismissClick = {
                                    coroutineScope.launch {
                                        bottomSheetState.hide()
                                    }
                                },
                                pauseViewModel = pauseDailySavingViewModel,
                                stopDailySavingViewModel = stopDailySavingViewModel,
                                analyticsHandler = analyticsHandler
                            )
                        }
                    }

                    else -> {}
                }
            },
            sheetShape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
            sheetBackgroundColor = colorResource(id = com.jar.app.core_ui.R.color.color_272239),
            sheetElevation = 12.dp
        ) {

            Column(Modifier.fillMaxSize()) {
                IntroScreenToolBar(RightSectionClick = {
                    navigateToFaqScreen()
                }) {

                    analyticsHandler.postEvent(
                        DSCancellation_PageClicked, mapOf(
                            Button_type to DailyInvestmentStatusScreen.Back,
                            DailyInvestmentCancellationKey.State to viewModel.pauseState
                        )
                    )
                    popBackStack()
                }
                if (pauseStatusValue == true) {
                    ScreenTopBannerForRestart(
                        onClick = {
                            viewModel.updateAutoInvestPauseDurationFlow(pause = false, pauseDuration = null, whichClick =
                            if (viewModel.setupVersion.value == DailyInvestmentCancellationEnum.V3.name)
                                DailyInvestmentStatusScreen.Restart_Now_Banner
                            else
                                DailyInvestmentStatusScreen.Resume_Now_Banner)
                        },
                        performTaskString = dailyInvestmentSettingsDetails?.data?.data?.savingsDetails?.stateView?.buttonText.orEmpty(),
                        performTaskStringColor = colorResource(id = com.jar.app.core_ui.R.color.color_724508),
                        logo = R.drawable.pause_intro,
                        logoBg = colorResource(id = com.jar.app.core_ui.R.color.color_E9C796),
                        backgroundColor = colorResource(id = com.jar.app.core_ui.R.color.color_EBB46A),
                        text = dailyInvestmentSettingsDetails?.data?.data?.savingsDetails?.`stateView`?.text.orEmpty(),
                        textColor = colorResource(id = com.jar.app.core_ui.R.color.color_2E2942)
                    )
                } else {
                    Box(modifier = Modifier.size(0.dp))
                }
                LazyColumn(modifier = Modifier.fillMaxSize(), state = scrollState) {
                    item {
                        dailyInvestmentSettingsDetails?.data?.data?.savingsDetails?.let {
                            TotalAmountSavedDetailsBlock(
                                modifier = Modifier,
                                onTrackClick = {
                                    analyticsHandler.postEvent(
                                        DSCancellation_PageClicked,
                                        mapOf(Button_type to DailyInvestmentStatusScreen.Track_Your_Savings)
                                    )
                                    postSetupApi.openPostSetupDetails()
                                },
                                dailySavingPause = pauseStatusValue == true,
                                savingsDetails = it
                            )
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.heightIn(20.dp))
                        dailyInvestmentSettingsDetails?.data?.data?.let { it ->
                            SecondPartOfSpendTrackerScreenInfo(
                                stepsFeaturesDetails = it.stepsFeaturesDetails!!
                            )
                            Spacer(modifier = Modifier.heightIn(34.dp))
                            if (it.subVersion == DailyInvestmentCancellationEnum.V3.name || it.subVersion == DailyInvestmentCancellationEnum.V4.name) {
                                DailySavingFeaturesBoxWithClickAction(
                                    screenData = it,
                                    onStopClick = {
                                        it.stepsFeaturesDetails!!.featureOrderTextDeeplinks?.get(0).let {
                                            if (it == null ) {
                                                coroutineScope.launch {
                                                    scrollState.animateScrollToItem(
                                                        index = scrollState.layoutInfo.totalItemsCount - 1
                                                    )
                                                }
                                            } else {
                                                EventBus.getDefault().post(
                                                    HandleDeepLinkEvent(
                                                        deepLink = it,
                                                        fromScreen = DailyInvestmentSettingsV2,
                                                        fromSection = null,
                                                        fromCard = null
                                                    )
                                                )
                                            }
                                        }
                                    },
                                    onWithdrawClick = {
                                        it.stepsFeaturesDetails!!.featureOrderTextDeeplinks?.get(1)
                                            ?.let { it1 ->
                                                EventBus.getDefault().post(
                                                    HandleDeepLinkEvent(
                                                        deepLink = it1,
                                                        fromScreen = DailyInvestmentSettingsV2,
                                                        fromSection = null,
                                                        fromCard = null
                                                    )
                                                )
                                            }
                                    },
                                    onGetGoldClick = {
                                        it.stepsFeaturesDetails!!.featureOrderTextDeeplinks?.get(2)
                                            ?.let { it1 ->
                                                EventBus.getDefault().post(
                                                    HandleDeepLinkEvent(
                                                        deepLink = it1,
                                                        fromScreen = DailyInvestmentSettingsV2,
                                                        fromSection = null,
                                                        fromCard = null
                                                    )
                                                )
                                            }
                                    },
                                    analyticsHandler = analyticsHandler
                                )
                            } else {
                                DailyInvestmentStepsFeaturesDetailsBlock(
                                    modifier = Modifier,
                                    stepsFeaturesDetails = it.stepsFeaturesDetails
                                )
                            }
                        }
                    }

                    item {
                        dailyInvestmentSettingsDetails?.data?.data?.let {
                            if (pauseStatusValue != null) {
                                DailyInvestmentSetupDetailsBlock(
                                    Modifier,
                                    analyticsHandler = analyticsHandler,
                                    onStopClick = {
                                        when (it.subVersion) {
                                            DailyInvestmentCancellationEnum.V3.name -> {
                                                isV2StopBottomSheetClicked.value = true
                                                analyticsHandler.postEvent(
                                                    DailyInvestmentCancellationEventKey.DSCancellation_StopDSpopupShown, mapOf(
                                                        DailyInvestmentStatusScreen.Source to "Cancellation Page",
                                                        is_Permanently_Cancel_flow to true
                                                    )
                                                )
                                                val bottomSheetValue = getBottomSheetType(
                                                    isPauseBottomSheetClicked,
                                                    stopDailySavingBottomSheetDetails.value?.data?.data?.statisticsContent,
                                                    isV2StopBottomSheetClicked
                                                )
                                                coroutineScope.launch {
                                                    whichBottomSheetType.value = bottomSheetValue
                                                    bottomSheetState.show()
                                                }
                                            }
                                            DailyInvestmentCancellationEnum.V4.name -> {
                                                analyticsHandler.postEvent(
                                                    EventKey.DSCancellation_StopDSpopupShown, mapOf(
                                                    is_Permanently_Cancel_flow to false,
                                                    EventKey.DailyInvestmentStatusScreenSource to DailyInvestmentStatusScreen.Cancellation_Page
                                                ))
                                                navigateTo(
                                                    navDirections = DailyInvestmentSettingsV2FragmentDirections.actionDailyInvestmentSettingsV2FragmentToStopDailySavingsScreenForV3(),
                                                    shouldAnimate = true
                                                )
                                            }
                                            else -> {
                                                isPauseBottomSheetClicked.value = false
                                                val bottomSheetValue = getBottomSheetType(
                                                    isPauseBottomSheetClicked,
                                                    stopDailySavingBottomSheetDetails.value?.data?.data?.statisticsContent
                                                )
                                                coroutineScope.launch {
                                                    whichBottomSheetType.value = bottomSheetValue
                                                    bottomSheetState.show()
                                                }

                                                analyticsHandler.postEvent(
                                                    DSCancellation_PageClicked,
                                                    mapOf(
                                                        Button_type to DailyInvestmentStatusScreen.Stop_Daily_Savings,
                                                        DailyInvestmentCancellationKey.State to viewModel.pauseState
                                                    )
                                                )
                                                analyticsHandler.postEvent(
                                                    DailyInvestmentCancellationEventKey.DSCancellation_StopDSpopupShown,
                                                    mapOf(
                                                        DailyInvestmentStopKey.type to getAnalyticsString(
                                                            bottomSheetValue
                                                        )
                                                    )
                                                )
                                            }
                                        }
                                    },
                                    onPauseClick = {
                                        isPauseBottomSheetClicked.value = true
                                        if (!pauseStatusValue) {
                                            analyticsHandler.postEvent(
                                                DSCancellation_PageClicked,
                                                mapOf(
                                                    Button_type to DailyInvestmentStatusScreen.Pause_Daily_Savings,
                                                    DailyInvestmentCancellationKey.State to viewModel.pauseState
                                                )
                                            )
                                            analyticsHandler.postEvent(DailyInvestmentCancellationEventKey.DSCancellation_PauseDSpopupShown)
                                            coroutineScope.launch {
                                                whichBottomSheetType.value = getBottomSheetType(isPauseBottomSheetClicked, stopDailySavingBottomSheetDetails.value?.data?.data?.statisticsContent)
                                                bottomSheetState.show()
                                            }
                                        } else {
                                            viewModel.updateAutoInvestPauseDurationFlow(pause = false, pauseDuration = null, whichClick =
                                            if (viewModel.setupVersion.value == DailyInvestmentCancellationEnum.V3.name)
                                                DailyInvestmentStatusScreen.Restart_Now_CTA
                                            else
                                                DailyInvestmentStatusScreen.Resume_Now_CTA)
                                        }
                                    },
                                    onAmountClick = { onAmountClick(dailySavingAmount.value.orZero()) },
                                    onSavingSourceClick = {
                                        dailyInvestmentApi.updateDailySavingAndSetupItsAutopay(
                                            mandateAmount = userSavingDetails?.data?.data?.mandateAmount.orZero(),
                                            source = MandatePaymentEventKey.FeatureFlows.UpdateDailySaving,
                                            authWorkflowType = MandateWorkflowType.PENNY_DROP,
                                            newDailySavingAmount = it.setupDetails?.amount.orZero(),
                                            popUpToId = R.id.dailyInvestmentSettingsV2Fragment,
                                            userLifecycle = prefs.getUserLifeCycleForMandate()
                                        )
                                    },
                                    dailySavingAmount = dailySavingAmount,
                                    dailySavingPause = pauseStatusValue,
                                    setupDetails = it.setupDetails,
                                    totalUpiApps = totalUpiApps.value,
                                    setUpVersion = it.subVersion.orEmpty()
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    private fun getBottomSheetType(
        isPause: MutableState<Boolean>,
        statisticsContent: StatisticsContent?,
        isV2StopBottomSheet: MutableState<Boolean>? = null
    ): BottomSheetType {
        val bottomSheetType =  when {
            isPause.value -> {
                BottomSheetType.PAUSE_BOTTOM_SHEET
            }
            isV2StopBottomSheet?.value.orFalse() -> {
                BottomSheetType.V2_STOP_BOTTOM_SHEET
            }
            statisticsContent != null -> {
                BottomSheetType.STATIC_STOP_BOTTOM_SHEET
            }
            else -> {
                BottomSheetType.KNOWLEDGE_STOP_BOTTOM_SHEET
            }
        }
        return bottomSheetType
    }

    private fun navigateToStopDailySavingScreen() {
        navigateTo(
            DailyInvestmentSettingsV2FragmentDirections.actionDailyInvestmentSettingsV2FragmentToProgressRedirectionFragment(
                progressScreenData = ProgressScreenData(
                heading = requireContext().resources.getString(com.jar.app.feature_daily_investment_cancellation.shared.R.string.your_daily_saving_is_cancelled),
                subHeading = requireContext().resources.getString(com.jar.app.feature_daily_investment_cancellation.shared.R.string.no_money_will_be_debited_from_your_account),
                stopDailySaving = true,
                pauseDailySaving = false,
                resumeDailySaving = false,
                continueDailySaving = false,
                highlightedText = "",
                version = viewModel.setupVersion.value,
                numberOfDays = pauseDailySavingViewModel.selectedDaysFlow.value.orZero().toString(),
            )),
            shouldAnimate = true
        )
    }

    private fun getAnalyticsString(bottomSheetType: BottomSheetType): String {
        return when (bottomSheetType) {
            BottomSheetType.PAUSE_BOTTOM_SHEET -> DailyInvestmentStatusScreen.Pause
            BottomSheetType.STATIC_STOP_BOTTOM_SHEET -> DailyInvestmentStatusScreen.Statistic
            BottomSheetType.KNOWLEDGE_STOP_BOTTOM_SHEET -> DailyInvestmentStatusScreen.Knowledge
            BottomSheetType.V2_STOP_BOTTOM_SHEET -> DailyInvestmentStatusScreen.V2Stop
            BottomSheetType.NONE -> DailyInvestmentStatusScreen.None
        }
    }

    private fun navigateToDailySavingCancellationScreen(days: String) {
        analyticsHandler.postEvent(
            DailyInvestmentCancellationEventKey.Shown_Success_PauseDailySavingsPopUp,
            mapOf(
                DailyInvestmentPauseKey.Final_selected_days to days,
            )
        )
        navigateTo(
            DailyInvestmentSettingsV2FragmentDirections.actionDailyInvestmentSettingsV2FragmentToProgressRedirectionFragment(
                progressScreenData = ProgressScreenData(
                heading = requireContext().resources.getString(com.jar.app.feature_daily_investment_cancellation.shared.R.string.savings_paused),
                subHeading = requireContext().resources.getString(com.jar.app.feature_daily_investment_cancellation.shared.R.string.we_have_paused_your_daily_saving),
                stopDailySaving = false,
                pauseDailySaving = true,
                resumeDailySaving = false,
                continueDailySaving = false,
                highlightedText = "$days days",
                version = viewModel.setupVersion.value,
                numberOfDays = pauseDailySavingViewModel.selectedDaysFlow.value.orZero().toString()
            )), shouldAnimate = true
        )
    }

    private fun stopDailySavingClicked(isPauseClicked: MutableState<Boolean>, stopStaticContent: StatisticsContent?) {
        analyticsHandler.postEvent(
            DailyInvestmentCancellationEventKey.DSCancellation_StopDSpopupClicked,
            mapOf(
                Button_type to requireContext().resources.getString(
                    com.jar.app.feature_daily_investment_cancellation.shared.R.string.stop_daily_savings
                ),
                DailyInvestmentStopKey.type to getAnalyticsString(
                    getBottomSheetType(
                        isPauseClicked,
                        stopStaticContent
                    )
                )
            )
        )
        stopDailySavingViewModel.disableDailySavings()
    }

    private fun continueDailySavingClicked(isPauseClicked: MutableState<Boolean>, stopStaticContent: StatisticsContent?) {
        analyticsHandler.postEvent(
            DailyInvestmentCancellationEventKey.DSCancellation_StopDSpopupClicked,
            mapOf(
                Button_type to requireContext().resources.getString(
                    com.jar.app.feature_daily_investment_cancellation.shared.R.string.continue_daily_savings
                ),
                DailyInvestmentStopKey.type to (getBottomSheetType(isPauseClicked, stopStaticContent))
            )
        )
        navigateTo(
            DailyInvestmentSettingsV2FragmentDirections.actionDailyInvestmentSettingsV2FragmentToProgressRedirectionFragment(
                progressScreenData = ProgressScreenData(
                heading = requireContext().resources.getString(com.jar.app.feature_daily_investment_cancellation.shared.R.string.hurray),
                subHeading = requireContext().resources.getString(com.jar.app.feature_daily_investment_cancellation.shared.R.string.we_are_glad_to_see_you_continue),
                stopDailySaving = false,
                pauseDailySaving = false,
                resumeDailySaving = false,
                continueDailySaving = true,
                highlightedText = "",
                version = viewModel.settingsScreenDataFlow.value.data?.data?.subVersion.orEmpty(),
                numberOfDays = pauseDailySavingViewModel.selectedDaysFlow.value.orZero().toString()
            )),
            shouldAnimate = true
        )
    }

    private fun totalUpiApps(): Int {
        var totalUpiApps = 0
        if (mApp.applicationContext.isPackageInstalled(BuildConfig.PHONEPE_PACKAGE)) {
            totalUpiApps++
        }
        if (mApp.applicationContext.isPackageInstalled(BuildConfig.PAYTM_PACKAGE)) {
            totalUpiApps++
        }
        return totalUpiApps
    }

    private fun onAmountClick(recommendedAmount: Float) {
        analyticsHandler.postEvent(
            DSCancellation_PageClicked,
            mapOf(
                Button_type to DailyInvestmentStatusScreen.Change_Amount,
                DailyInvestmentCancellationKey.State to viewModel.pauseState
            )
        )
        navigateTo("${BaseConstants.InternalDeepLinks.EDIT_DAILY_SAVING_AMOUNT}/${recommendedAmount}/true")
    }

    private fun observeApiResponse() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                stopDailySavingViewModel.disableDailySavingFlow.collectUnwrapped(
                    onSuccess = {
                        prefs.setDailyInvestmentCancellationV2Date(
                            Date().getFormattedDate("dd MMM''yy")
                        )
                        EventBus.getDefault().post(RefreshDailySavingEvent())
                        navigateToStopDailySavingScreen()
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                stopDailySavingViewModel.confirmActionDetailsFlow.collect()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                pauseDailySavingViewModel.updatePauseDurationFlow.collectUnwrapped(
                    onSuccess = {
                        EventBus.getDefault().post(RefreshDailySavingEvent())

                        if (viewModel.setupVersion.value != DailyInvestmentCancellationEnum.V3.name) {
                            if (pauseDailySavingViewModel.customDaysFlow.value == null) {
                                analyticsHandler.postEvent(
                                    DailyInvestmentCancellationEventKey.DSCancellation_PauseDSpopupClicked,
                                    mapOf(
                                        Button_type to DailyInvestmentStatusScreen.Pause_Now,
                                        DailyInvestmentPauseKey.Final_paused_days to pauseDailySavingViewModel.differenceDaysFlow.value.orZero(),
                                        DailyInvestmentPauseKey.is_calendar_used to "false"
                                    )
                                )
                            } else {
                                analyticsHandler.postEvent(
                                    DailyInvestmentCancellationEventKey.DSCancellation_PauseDSpopupClicked,
                                    mapOf(
                                        Button_type to DailyInvestmentStatusScreen.Pause_Now,
                                        DailyInvestmentPauseKey.Final_paused_days to pauseDailySavingViewModel.differenceDaysFlow.value.orZero(),
                                        DailyInvestmentPauseKey.is_calendar_used to "true"
                                    )
                                )
                            }
                        }
                        navigateToDailySavingCancellationScreen(
                            pauseDailySavingViewModel.differenceDaysFlow.value.orZero().toString()
                        )
                    }
                )
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                pauseDailySavingViewModel.pauseDetailsFlow.collect()
            }
        }


        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.settingsScreenDataFlow.collectUnwrapped(
                    onSuccess = { dailyInvestmentSettingsDetails ->
                        viewModel.setupVersion.value = dailyInvestmentSettingsDetails.data?.subVersion.orEmpty()
                        analyticsHandler.postEvent(
                            DailyInvestmentCancellationEventKey.DSCancellation_PageLaunched,
                            mapOf(
                                DailyInvestmentCancellationKey.From_screen to DailyInvestmentStatusScreen.Settings,
                                DailyInvestmentCancellationKey.State to dailyInvestmentSettingsDetails.data?.setupDetails?.status.toString(),
                                DailyInvestmentCancellationKey.Total_Amount_saved to dailyInvestmentSettingsDetails.data?.savingsDetails?.totalDsAmount.toString(),
                                DailyInvestmentCancellationKey.Current_payment_platform to dailyInvestmentSettingsDetails.data?.setupDetails?.subProvider.toString(),
                                DailyInvestmentCancellationKey.DS_Mandate_Amount to dailyInvestmentSettingsDetails.data?.stepsFeaturesDetails?.recurringAmount.toString()
                            )
                        )
                        dismissProgressBar()
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.updatePauseDurationFlow.collectUnwrapped(
                    onSuccess = {
                        if (it.data.isSavingPaused == false) {
                            navigateTo(
                                DailyInvestmentSettingsV2FragmentDirections.actionDailyInvestmentSettingsV2FragmentToProgressRedirectionFragment(
                                    progressScreenData = ProgressScreenData(
                                    heading = requireContext().resources.getString(com.jar.app.feature_daily_investment_cancellation.shared.R.string.hurray),
                                    subHeading = requireContext().resources.getString(com.jar.app.feature_daily_investment_cancellation.shared.R.string.we_are_glad_to_see_you_continue),
                                    stopDailySaving = false,
                                    pauseDailySaving = false,
                                    resumeDailySaving = true,
                                    continueDailySaving = false,
                                    highlightedText = "",
                                    version = viewModel.settingsScreenDataFlow.value.data?.data?.subVersion.orEmpty(),
                                    numberOfDays = pauseDailySavingViewModel.selectedDaysFlow.value.orZero().toString()
                                )),
                                shouldAnimate = true
                            )
                        }
                    }
                )
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSetupMandateEvent(setupMandateEvent: SetupMandateEvent) {
        setupMandateEvent.newDailySavingAmount?.let {
            dailyInvestmentApi.updateDailySavingAndSetupItsAutopay(
                mandateAmount = setupMandateEvent.newMandateAmount,
                source = MandatePaymentEventKey.FeatureFlows.UpdateDailySaving,
                authWorkflowType = MandateWorkflowType.PENNY_DROP,
                newDailySavingAmount = it,
                popUpToId = com.jar.app.feature_daily_investment.R.id.updateSetupDailySavingsBottomSheet,
                userLifecycle = prefs.getUserLifeCycleForMandate()
            )
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRefreshDailySavingEvent(refreshDailySavingEvent: RefreshDailySavingEvent) {
        viewModel.fetchUserDSDetailsFlow()
        getData()
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    private fun registerBackPressDispatcher() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner, backPressCallback
        )
    }

}

enum class BottomSheetType {
    PAUSE_BOTTOM_SHEET,
    STATIC_STOP_BOTTOM_SHEET,
    KNOWLEDGE_STOP_BOTTOM_SHEET,
    V2_STOP_BOTTOM_SHEET,
    NONE
}