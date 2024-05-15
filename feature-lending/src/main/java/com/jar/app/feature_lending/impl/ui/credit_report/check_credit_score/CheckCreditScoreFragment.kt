package com.jar.app.feature_lending.impl.ui.credit_report.check_credit_score

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.util.openWhatsapp
import com.jar.app.base.util.orFalse
import com.jar.app.core_base.util.BaseConstants.KEY_OTP_SUCCESS
import com.jar.app.core_base.util.orZero
import com.jar.app.core_compose_ui.base.BaseComposeFragment
import com.jar.app.core_compose_ui.component.JarPrimaryButton
import com.jar.app.core_compose_ui.component.JarSecondaryButton
import com.jar.app.core_compose_ui.theme.JarTypography
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.feature_lending.R
import com.jar.app.feature_lending.impl.ui.common_component.RealTimeGenericLoading
import com.jar.app.feature_lending.impl.ui.common_component.ToolbarWithHelpButton
import com.jar.app.feature_lending.impl.ui.credit_report.components.CheckCreditScoreImageCard
import com.jar.app.feature_lending.impl.ui.credit_report.components.CheckCreditScorePointsCard
import com.jar.app.feature_lending.impl.ui.realtime_flow.bottom_sheet.RealTimeRefreshCreditScoreBottomSheet
import com.jar.app.feature_lending.impl.ui.realtime_flow.bottom_sheet.RealTimeRefreshCreditScoreBottomSheetViewModelAndroid
import com.jar.app.feature_lending.shared.domain.ui_event.RefreshCreditScoreBottomSheetEvent
import com.jar.app.feature_lending.impl.ui.realtime_flow.components.CreditScoreCard
import com.jar.app.feature_lending.impl.ui.realtime_flow.landing.RealTimeLandingError
import com.jar.app.feature_lending.shared.MR
import com.jar.app.feature_lending.shared.domain.LendingEventKeyV2
import com.jar.app.feature_lending.shared.domain.model.creditReport.CreditReport
import com.jar.app.feature_lending_kyc.api.LendingKycApi
import com.jar.app.core_base.data.dto.KycFeatureFlowType
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@AndroidEntryPoint
internal class CheckCreditScoreFragment : BaseComposeFragment() {

    @Inject
    lateinit var prefs: PrefsApi

    @Inject
    lateinit var remoteConfigApi: RemoteConfigApi

    @Inject
    lateinit var lendingKycApi: LendingKycApi

    private val viewModelProvider by viewModels<CheckCreditScoreViewModelAndroid> { defaultViewModelProviderFactory }
    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }
    private val creditScoreBottomSheetViewModelProvider by viewModels<RealTimeRefreshCreditScoreBottomSheetViewModelAndroid> { defaultViewModelProviderFactory }
    private val creditScoreBottomSheetViewModel by lazy {
        creditScoreBottomSheetViewModelProvider.getInstance()
    }

    private var backPressCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                handleBackPress()
            }
        }

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    private fun openOtpBottomScreen(name: String, panNumber: String?) {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                findNavController().currentBackStackEntry?.savedStateHandle
                    ?.getStateFlow(KEY_OTP_SUCCESS, false)?.collectLatest {
                        if (it) {
                            findNavController().currentBackStackEntry?.savedStateHandle?.remove<Boolean>(
                                KEY_OTP_SUCCESS
                            )
                            viewModel.getStaticCreditReportData()
                        }
                    }
            }
        }
        lendingKycApi.openPANFetchFlow(
            kycFeatureFlowType = KycFeatureFlowType.LENDING,
            childNavController = findNavController(),
            shouldOpenPanInBackground = false,
            shouldNotifyAfterOtpSuccess = true,
            nameForCreditReport = name,
            panNumberForCreditReport = panNumber?.ifEmpty { null }
        ) { isLoading, error ->
            error?.snackBar(requireView())
            if (isLoading) {
                showProgressBar()
            } else {
                dismissProgressBar()
            }
        }
    }

    private fun observeFlowData() {
        viewModel.getStaticCreditReportData()
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                creditScoreBottomSheetViewModel.isRefreshingCreditScore.collect {
                    when (it.status) {
                        RestClientResult.Status.NONE,
                        RestClientResult.Status.LOADING -> {
                            showProgressBar()
                        }

                        RestClientResult.Status.SUCCESS -> {
                            dismissProgressBar()
                            viewModel.setFlagForRefresh(true)
                            viewModel.getStaticCreditReportData()
                        }

                        RestClientResult.Status.ERROR -> {
                            dismissProgressBar()
                            it.message?.snackBar(requireView())
                        }
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
    @Composable
    override fun RenderScreen() {
        val bottomSheetUiState by
        creditScoreBottomSheetViewModel.uiState.collectAsState()
        val screenData by viewModel.creditStaticContent.collectAsState()
        val scope = rememberCoroutineScope()
        when (screenData.status) {
            RestClientResult.Status.NONE,
            RestClientResult.Status.LOADING -> {
                RealTimeGenericLoading(modifier = Modifier)
            }

            RestClientResult.Status.SUCCESS -> {
                screenData.data?.data?.creditReport?.let { _ ->
                    LaunchedEffect(key1 = Unit) {
                        if (!screenData.data?.data?.creditReport?.creditReportExist.orFalse()) {
                            viewModel.sendAnalyticsCreditsScreenLaunchedEvent(
                                action = LendingEventKeyV2.shown,
                                creditScoreStatus = LendingEventKeyV2.NOT_FOUND
                            )
                        } else {
                            viewModel.sendAnalyticsCreditsShownEvent(
                                action = LendingEventKeyV2.shown,
                                creditScoreStatus = LendingEventKeyV2.FOUND
                            )
                        }
                    }

                    val keyboardController = LocalSoftwareKeyboardController.current
                    val modalBottomSheetState = rememberModalBottomSheetState(
                        initialValue = ModalBottomSheetValue.Hidden,
                        skipHalfExpanded = true
                    )
                    if (modalBottomSheetState.currentValue == ModalBottomSheetValue.Hidden) {
                        keyboardController?.hide()
                    }
                    ModalBottomSheetLayout(
                        sheetState = modalBottomSheetState,
                        scrimColor = MaterialTheme.colors.onSurface.copy(alpha = 0.5f),
                        sheetContent = {
                            RealTimeRefreshCreditScoreBottomSheet(

                                uiState = bottomSheetUiState,
                                onPanChange = creditScoreBottomSheetViewModel::uiEvent,
                                onNameChange = creditScoreBottomSheetViewModel::uiEvent,
                                onCrossClick = {
                                    viewModel.sendAnalyticsCreditsScreenBSLaunchedEvent(
                                        LendingEventKeyV2.details_bs_closed
                                    )
                                    scope.launch {
                                        keyboardController?.hide()
                                        modalBottomSheetState.hide()

                                    }
                                },
                                onProceedButtonClick = {
                                    viewModel.sendAnalyticsCreditsScreenBSLaunchedEvent(
                                        LendingEventKeyV2.details_bs_submit_clicked
                                    )
                                    scope.launch {
                                        keyboardController?.hide()
                                        modalBottomSheetState.hide()

                                    }
                                    if (bottomSheetUiState.experianConsentRequired) {
                                        openOtpBottomScreen(
                                            bottomSheetUiState.name,
                                            bottomSheetUiState.panNo
                                        )
                                    } else {
                                        creditScoreBottomSheetViewModel.uiEvent(
                                            RefreshCreditScoreBottomSheetEvent.OnClickSubmitButtonInRefreshScoreBottomSheet
                                        )
                                    }
                                }, isCheckCreditScore = true,isvisible = modalBottomSheetState.isVisible
                            )
                        },
                        sheetShape = RoundedCornerShape(topEnd = 16.dp, topStart = 16.dp)
                    ) {
                        CreditScoreScreen(
                            viewModel.getFlagForRefresh(),
                            onGoToHomeScreenClick = {
                                viewModel.sendAnalyticsCreditsShownEvent(
                                    action = LendingEventKeyV2.go_to_homescreen_clicked,
                                    creditScoreStatus = LendingEventKeyV2.NOT_FOUND
                                )
                                handleBackPress()
                            },
                            onBackButtonClick = {
                                handleBackPress()
                            },
                            onHelpButtonClick = {
                                openNeedHelp()
                            },
                            onProceedButtonClick = {
                                viewModel.sendAnalyticsCreditsScreenLaunchedEvent(
                                    action = LendingEventKeyV2.get_credit_score_clicked,
                                    creditScoreStatus = LendingEventKeyV2.NOT_FOUND
                                )
                                viewModel.sendAnalyticsCreditsScreenBSLaunchedEvent(
                                    LendingEventKeyV2.details_bs_shown
                                )
                                scope.launch {
                                    modalBottomSheetState.show()
                                }
                            },
                            onDetailedCreditReportButtonClick = {
                                viewModel.sendAnalyticsCreditsShownEvent(
                                    action = LendingEventKeyV2.detailed_credit_report_clicked,
                                    creditScoreStatus = LendingEventKeyV2.FOUND
                                )
                                navigateNext()
                            },
                            screenData = screenData.data?.data?.creditReport
                        )
                    }
                }
            }

            RestClientResult.Status.ERROR -> {
                RealTimeLandingError(title = screenData.message.orEmpty())
            }
        }
    }

    private fun navigateNext() {
        navigateTo(
            CheckCreditScoreFragmentDirections.actionToCreditSummaryFragment("CreditScore"),
            shouldAnimate = true,
            popUpTo = R.id.checkCreditScoreFragment,
            inclusive = true
        )
    }

    private fun handleBackPress() {
        viewModel.sendAnalyticsBackButtonEvent()
        popBackStack()
    }

    private fun openNeedHelp() {
        viewModel.sendAnalyticsNeedHelpEvent()
        val sendTo = remoteConfigApi.getWhatsappNumber()
        val number = prefs.getUserPhoneNumber()
        val name = prefs.getUserName()
        val message = getCustomStringFormatted(
            MR.strings.feature_credit_contact_support,
            name.orEmpty(),
            number.orEmpty()
        )
        requireContext().openWhatsapp(sendTo, message)
    }

    override fun setup(savedInstanceState: Bundle?) {
        registerBackPressDispatcher()
        observeFlowData()
    }

    private fun registerBackPressDispatcher() {
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, backPressCallback)
    }

    override fun onDestroyView() {
        backPressCallback.isEnabled = false
        super.onDestroyView()
    }
}

@Composable
internal fun CreditScoreScreen(
    isRefreshed: Boolean,
    modifier: Modifier = Modifier,
    onGoToHomeScreenClick: () -> Unit = {},
    onBackButtonClick: () -> Unit = {},
    onHelpButtonClick: () -> Unit = {},
    onProceedButtonClick: () -> Unit = {},
    onDetailedCreditReportButtonClick: () -> Unit = {},
    screenData: CreditReport?,
) {
    Scaffold(
        modifier = modifier
            .fillMaxSize(),
        topBar = {
            ToolbarWithHelpButton(
                onBackButtonClick = onBackButtonClick,
                title = stringResource(id = MR.strings.feature_lending_check_credit_score_title.resourceId),
                onHelpButtonClick = onHelpButtonClick
            )
        },
        bottomBar = {
           if (!screenData?.creditReportExist.orFalse() && !isRefreshed) {
                JarPrimaryButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                    text = stringResource(id = MR.strings.feature_lending_get_credit_score.resourceId),
                    isAllCaps = false,
                    onClick = onProceedButtonClick
                )
            } else {
                Row(
                    modifier = Modifier.padding(bottom = 16.dp)
                        .fillMaxWidth(), horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        modifier = Modifier.align(Alignment.CenterVertically),
                        text = stringResource(id = MR.strings.feature_lending_powered_by.resourceId),
                        style = JarTypography.body1.copy(
                            fontSize = 14.sp,
                            color = colorResource(id = com.jar.app.core_ui.R.color.color_ACA1D3)
                        )
                    )
                    Image(
                        modifier = Modifier
                            .padding(start = 10.dp),
                        painter = painterResource(id = R.drawable.feature_lending_ic_experian_logo),
                        contentDescription = ""
                    )
                }
            }
        },
        backgroundColor = colorResource(id = com.jar.app.core_ui.R.color.bgColor)
    ) { padding ->

        if (!screenData?.creditReportExist.orFalse() && !isRefreshed) { //isAlredayRefresed
            //1st time it comes here
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
            ) {
                item(key = "header_card") {
                    CheckCreditScoreImageCard(screenData?.creditInformation?.title.orEmpty())
                }
                item(key = "list_tick_lines") {
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 20.dp)
                            .background(
                                color = colorResource(id = com.jar.app.core_ui.R.color.color_2E2942),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 16.dp)
                    ) {
                        screenData?.creditInformation?.realTimeBenefits?.let {
                            screenData.creditInformation?.realTimeBenefits?.forEachIndexed { _, realTimeBenefits ->
                                CheckCreditScorePointsCard(realTimeBenefits)
                            }
                        }
                    }
                }
            }
        } else if (!screenData?.creditReportExist.orFalse() && isRefreshed) { //isAlreadyRefreshed

            //2nd time it comes here
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    modifier = Modifier
                        .size(width = 145.dp, height = 110.dp)
                        .align(alignment = Alignment.CenterHorizontally),
                    painter = painterResource(id = R.drawable.feature_lending_bg_credit_score_not_found),
                    contentDescription = ""
                )

                Text(
                    modifier = Modifier
                        .fillMaxWidth(),
                    text = stringResource(id = MR.strings.feature_lending_credit_score_not_found.resourceId),
                    style = JarTypography.h6.copy(
                        fontSize = 18.sp,
                        lineHeight = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(id = com.jar.app.core_ui.R.color.color_EEEAFF)
                    ),
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )

                Text(
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .fillMaxWidth(),
                    text = stringResource(id = MR.strings.feature_lending_no_credit_history.resourceId),
                    style = JarTypography.body1.copy(
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        color = colorResource(id = com.jar.app.core_ui.R.color.color_D5CDF2)
                    ),
                    textAlign = TextAlign.Center,
                    maxLines = 2
                )

                Row(
                    modifier = Modifier
                        .padding(top = 32.dp)
                        .background(color = colorResource(id = com.jar.app.core_ui.R.color.bgColor))
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center

                ) {
                    JarSecondaryButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 80.dp)
                            .align(Alignment.CenterVertically),
                        text = stringResource(id = MR.strings.feature_lending_go_to_home_screen_caps.resourceId),
                        onClick = { onGoToHomeScreenClick() },
                        isAllCaps = false
                    )
                }
                Spacer(
                    Modifier
                        .height(100.dp)
                        .fillMaxWidth()
                )
            }
        } else {
            //if credit report exist it comes in this flow
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                CreditScoreCard(
                    creditScoreCard = com.jar.app.feature_lending.shared.domain.model.realTimeFlow.CreditScoreCard(
                        backgroundColor = "#272239",
                        footerText = "",
                        creditScore = screenData?.reportDetails?.creditScore.orZero(),
                        creditScoreResult = screenData?.reportDetails?.creditScoreResult.orEmpty()
                    ), shouldShowDivider = false
                )
                if (!screenData?.creditReportExist.orFalse()) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth(),
                        text = stringResource(id = MR.strings.feature_lending_credit_score_not_found.resourceId),
                        style = JarTypography.h6.copy(
                            fontSize = 18.sp,
                            lineHeight = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorResource(id = com.jar.app.core_ui.R.color.color_EEEAFF)
                        ),
                        textAlign = TextAlign.Center,
                        maxLines = 1
                    )
                } else {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth(),
                        text = screenData?.reportDetails?.footerText.orEmpty(),
                        style = JarTypography.body1.copy(
                            fontSize = 12.sp,
                            lineHeight = 18.sp,
                            color = colorResource(id = com.jar.app.core_ui.R.color.color_D5CDF2)
                        ),
                        textAlign = TextAlign.Center,
                        maxLines = 2
                    )
                }
                Row(
                    modifier = Modifier
                        .padding(top = 32.dp)
                        .background(color = colorResource(id = com.jar.app.core_ui.R.color.bgColor))
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center

                ) {
                    JarSecondaryButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 80.dp)
                            .align(Alignment.CenterVertically),
                        text = stringResource(id = MR.strings.feature_lending_detailed_credit_report.resourceId),
                        onClick = { onDetailedCreditReportButtonClick() },
                        isAllCaps = false
                    )
                }
                Text(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .wrapContentWidth()
                        .align(Alignment.CenterHorizontally)
                        .clickable {
                            onGoToHomeScreenClick()

                        },
                    text = stringResource(id = MR.strings.feature_lending_go_to_home_screen.resourceId),
                    style = JarTypography.body1.copy(
                        fontSize = 12.sp,
                        lineHeight = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colorResource(id = com.jar.app.core_ui.R.color.color_D5CDF2)
                    ),
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    textDecoration = TextDecoration.Underline
                )
                Spacer(
                    Modifier
                        .height(100.dp)
                        .fillMaxWidth()
                )
            }
        }
    }
}

