package com.jar.app.feature_daily_investment_cancellation.impl.ui.stop_daily_saving

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.jar.app.base.data.event.HandleDeepLinkEvent
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.util.getFormattedDate
import com.jar.app.core_analytics.EventKey.DSCancellation_StopDSpopupShown
import com.jar.app.core_analytics.EventKey.DailyInvestmentStatusScreenSource
import com.jar.app.core_analytics.EventKey.is_Permanently_Cancel_flow
import com.jar.app.core_base.data.event.RefreshDailySavingEvent
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.orZero
import com.jar.app.core_compose_ui.base.BaseComposeFragment
import com.jar.app.core_compose_ui.component.JarButton
import com.jar.app.core_compose_ui.component.JarImage
import com.jar.app.core_compose_ui.component.JarSecondaryButton
import com.jar.app.core_compose_ui.theme.jarFontFamily
import com.jar.app.core_compose_ui.utils.generateSpannedFromHtmlString
import com.jar.app.core_compose_ui.utils.toAnnotatedString
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_ui.R
import com.jar.app.feature_daily_investment_cancellation.impl.ui.component.IntroScreenToolBar
import com.jar.app.feature_daily_investment_cancellation.impl.ui.component.RatingCard
import com.jar.app.feature_daily_investment_cancellation.impl.ui.intro_screen.DailyInvestmentSettingsV2ViewModel
import com.jar.app.feature_daily_investment_cancellation.impl.ui.pause_daily_saving.PauseDailySavingBottomSheetViewModel
import com.jar.app.feature_daily_investment_cancellation.impl.ui.pause_daily_saving.RenderPauseDailySavingStaticBottomSheet
import com.jar.app.feature_daily_investment_cancellation.impl.util.DailyInvestmentCancellationEventKey
import com.jar.app.feature_daily_investment_cancellation.impl.util.DailyInvestmentCancellationEventKey.Button_type
import com.jar.app.feature_daily_investment_cancellation.impl.util.DailyInvestmentCancellationEventKey.DSCancellation_StopDSpopup_Clicked
import com.jar.app.feature_daily_investment_cancellation.impl.util.DailyInvestmentCancellationEventKey.Shown_Success_PauseDailySavingsPopUp
import com.jar.app.feature_daily_investment_cancellation.impl.util.DailyInvestmentCancellationKey
import com.jar.app.feature_daily_investment_cancellation.impl.util.DailyInvestmentPauseKey
import com.jar.app.feature_daily_investment_cancellation.impl.util.DailyInvestmentStatusScreen
import com.jar.app.feature_daily_investment_cancellation.impl.util.DailyInvestmentStatusScreen.Cancellation_Page
import com.jar.app.feature_daily_investment_cancellation.impl.util.ProgressScreenData
import com.jar.app.feature_daily_investment_cancellation.shared.domain.model.DailyInvestmentConfirmActionDetails
import com.jar.internal.library.jar_core_network.api.util.collectUnwrapped
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.util.Date
import javax.inject.Inject


@AndroidEntryPoint
@OptIn(ExperimentalMaterialApi::class)
internal class StopDailySavingsV4Screen : BaseComposeFragment() {
    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    @Inject
    lateinit var prefs: PrefsApi

    private val viewModel by viewModels<DailyInvestmentSettingsV2ViewModel> { defaultViewModelProviderFactory }
    private val pauseDailySavingViewModel by viewModels<PauseDailySavingBottomSheetViewModel> { defaultViewModelProviderFactory }
    private val stopDailySavingViewModel by viewModels<StopDailySavingBottomSheetViewModel> { defaultViewModelProviderFactory }

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        stopDailySavingViewModel.cancellationStatisticsBottomSheetData()
        getData()
    }

    @Preview
    @Composable
    override fun RenderScreen() {
        val bottomSheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden, skipHalfExpanded = true)
        val pauseDailySavingBottomSheetDetails = pauseDailySavingViewModel.pauseDetailsFlow.collectAsState(initial = null)
        val stopDailySavingBottomSheetDetails = stopDailySavingViewModel.confirmActionDetailsFlow.collectAsState(initial = null)
        val coroutineScope = rememberCoroutineScope()

        ModalBottomSheetLayout(
            sheetState = bottomSheetState,
            sheetContent = {
                pauseDailySavingBottomSheetDetails.value?.data?.data?.let {
                    RenderPauseDailySavingStaticBottomSheet(
                        pauseDetails = it,
                        viewModel = pauseDailySavingViewModel,
                        onDismissClick = {
                            coroutineScope.launch {
                                bottomSheetState.hide()
                            }
                        },
                        childFragmentManager = childFragmentManager,
                        requireContext = requireContext(),
                        analyticsHandler = analyticsHandler
                    )
                }

            },
            sheetShape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
            sheetElevation = 12.dp
        ) {
            StopDailySavingsV3ScreenContent(
                popBackStack = {
                    popBackStack()
                },
                onPauseClick = {
                    coroutineScope.launch {
                        bottomSheetState.show()
                    }
                },
                onStopClick = {
                    stopDailySavingViewModel.disableDailySavings()
                },
                navigateToFaqScreen = {
                    navigateToFaqScreen()
                },
                screenDetails = stopDailySavingBottomSheetDetails.value?.data?.data,
                analyticsHandler = analyticsHandler
            )
        }
    }

    override fun setup(savedInstanceState: Bundle?) {
        registerBackPressDispatcher()
        observeApiResponse()
    }

    private var backPressCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            analyticsHandler.postEvent(
                DailyInvestmentCancellationEventKey.DSCancellation_PauseDSpopupClicked,
                mapOf(
                    Button_type to DailyInvestmentStatusScreen.Back,
                )
            )
            popBackStack()
        }
    }

    private fun getData() {
        viewModel.fetchSettingsFragmentDataFlow()
        pauseDailySavingViewModel.fetchPauseDetailsDataFlow()
    }

    private fun registerBackPressDispatcher() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner, backPressCallback
        )
    }

    private fun navigateToFaqScreen() {
        EventBus.getDefault()
            .post(HandleDeepLinkEvent(BaseConstants.BASE_EXTERNAL_DEEPLINK + BaseConstants.ExternalDeepLinks.HELP_SUPPORT))
    }

    private fun navigateToDailySavingCancellationScreen(days: String) {
        analyticsHandler.postEvent(
            DailyInvestmentCancellationEventKey.Shown_Success_PauseDailySavingsPopUp,
            mapOf(
                DailyInvestmentPauseKey.Final_selected_days to days,
            )
        )
        navigateTo(
            StopDailySavingsV4ScreenDirections.actionStopDailySavingsScreenForV3ToProgressRedirectionFragment(
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

    private fun navigateToStopDailySavingScreen() {
        navigateTo(
            StopDailySavingsV4ScreenDirections.actionStopDailySavingsScreenForV3ToProgressRedirectionFragment(
                progressScreenData = ProgressScreenData(
                heading = requireContext().resources.getString(com.jar.app.feature_daily_investment_cancellation.shared.R.string.your_daily_saving_is_cancelled),
                subHeading = requireContext().resources.getString(com.jar.app.feature_daily_investment_cancellation.shared.R.string.no_money_will_be_debited_from_your_account),
                stopDailySaving = true,
                pauseDailySaving = false,
                resumeDailySaving = false,
                continueDailySaving = false,
                highlightedText = "",
                version = viewModel.setupVersion.value,
                numberOfDays = pauseDailySavingViewModel.selectedDaysFlow.value.orZero().toString()
            )),
            shouldAnimate = true
        )
    }


    private fun observeApiResponse() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                pauseDailySavingViewModel.pauseDetailsFlow.collect()
            }
        }

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
                viewModel.settingsScreenDataFlow.collectUnwrapped(
                    onSuccess = {
                        viewModel.setupVersion.value = it.data?.subVersion.orEmpty()
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                stopDailySavingViewModel.confirmActionDetailsFlow.collectUnwrapped(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                    },
                    onError = { _, _ ->
                        dismissProgressBar()
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                pauseDailySavingViewModel.updatePauseDurationFlow.collectUnwrapped(
                    onSuccess = {
                        EventBus.getDefault().post(RefreshDailySavingEvent())

                        pauseDailySavingViewModel.customDaysFlow.collectLatest { customDaysFlow ->
                            pauseDailySavingViewModel.differenceDaysFlow.collectLatest { differenceDaysFlow ->
                                if (customDaysFlow == null) {
                                    analyticsHandler.postEvent(
                                        DailyInvestmentCancellationEventKey.DSCancellation_PauseDSpopupClicked,
                                        mapOf(
                                            DailyInvestmentCancellationEventKey.Button_type to DailyInvestmentStatusScreen.Pause_Now,
                                            DailyInvestmentPauseKey.Final_paused_days to differenceDaysFlow.toString(),
                                            DailyInvestmentPauseKey.is_calendar_used to "false"
                                        )
                                    )
                                } else {
                                    analyticsHandler.postEvent(
                                        DailyInvestmentCancellationEventKey.DSCancellation_PauseDSpopupClicked,
                                        mapOf(
                                            DailyInvestmentCancellationEventKey.Button_type to DailyInvestmentStatusScreen.Pause_Now,
                                            DailyInvestmentPauseKey.Final_paused_days to differenceDaysFlow.toString(),
                                            DailyInvestmentPauseKey.is_calendar_used to "true"
                                        )
                                    )
                                }
                                navigateToDailySavingCancellationScreen(differenceDaysFlow.toString())
                            }
                        }
                    }
                )
            }
        }
    }

}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun StopDailySavingsV3ScreenContent(
    popBackStack: () -> Unit,
    onStopClick: () -> Unit,
    onPauseClick: () -> Unit,
    navigateToFaqScreen: () -> Unit,
    analyticsHandler: AnalyticsApi,
    screenDetails: DailyInvestmentConfirmActionDetails?
    ) {
    Box(modifier = Modifier
        .fillMaxSize()
        .background(colorResource(id = R.color.color_272239)),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IntroScreenToolBar(RightSectionClick = {
                navigateToFaqScreen()
            }) {
                popBackStack()
            }
            Image(
                modifier = Modifier
                    .align(Alignment.Start)
                    .fillMaxWidth(),
                painter = painterResource(id = com.jar.app.core_ui.R.drawable.lamp_logo),
                alignment = Alignment.TopStart,
                contentScale = ContentScale.FillWidth,
                contentDescription = ""
            )
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Bottom
    ) {
        Text(
            modifier = Modifier
                .padding(top = 72.dp, start = 16.dp, bottom = 42.dp),
            text = screenDetails?.header.orEmpty(),
            fontFamily = jarFontFamily,
            fontWeight = FontWeight.W400,
            color = Color.White,
            lineHeight = 24.sp,
            fontSize = 16.sp
        )

        JarImage(
            modifier = Modifier
                .padding(start = 16.dp)
                .size(height = 40.dp, width = 100.dp),
            imageUrl = BaseConstants.ImageUrlConstants.GROUP_USER_LOGO_V2,
            contentDescription = ""
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            modifier = Modifier
                .padding(start = 20.dp),
            text = generateSpannedFromHtmlString(
                screenDetails?.statisticsContent?.title.orEmpty(),
                true
            ).toAnnotatedString(),
            fontFamily = jarFontFamily,
            lineHeight = 30.sp,
            fontWeight = FontWeight.W700,
            color = Color.White,
            fontSize = 22.sp
        )

        Spacer(modifier = Modifier.height(7.dp))

        RatingCard(
            screenDetails?.statisticsContent?.rating.orEmpty(),
            screenDetails?.statisticsContent?.downloads.orEmpty()
        )

        Spacer(modifier = Modifier.height(16.dp))

        JarButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp)
                .padding(end = 16.dp),
            text = "Pause for Sometime",
            elevation = 0.dp,
            isAllCaps = false,
            onClick = {
                analyticsHandler.postEvent(DSCancellation_StopDSpopup_Clicked, mapOf(
                    Button_type to DailyInvestmentStatusScreen.Pause,
                    is_Permanently_Cancel_flow to false
                ))
                onPauseClick()
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        JarSecondaryButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp)
                .padding(end = 16.dp),
            text = "Stop Permanently",
            isAllCaps = false,
            color = colorResource(id = R.color.color_272239),
            onClick = {
                analyticsHandler.postEvent(DSCancellation_StopDSpopup_Clicked, mapOf(
                    Button_type to DailyInvestmentStatusScreen.Stop,
                    is_Permanently_Cancel_flow to false
                ))
                onStopClick()
            }
        )

        Spacer(modifier = Modifier.height(43.dp))
    }
}