package com.jar.app.feature_lending.impl.ui.realtime_flow.landing

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FabPosition
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import androidx.navigation.fragment.navArgs
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.util.openWhatsapp
import com.jar.app.core_base.util.BaseConstants.KEY_OTP_SUCCESS
import com.jar.app.core_compose_ui.base.BaseComposeFragment
import com.jar.app.core_compose_ui.component.JarPrimaryButton
import com.jar.app.core_compose_ui.theme.JarTypography
import com.jar.app.core_compose_ui.views.ExpandableCardModel
import com.jar.app.core_compose_ui.views.ExpandableFaqCard
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.feature_lending.LendingNavigationDirections
import com.jar.app.feature_lending.R
import com.jar.app.feature_lending.impl.domain.model.realTimeFlow.CtaType
import com.jar.app.feature_lending.impl.ui.common_component.RealTimeGenericLoading
import com.jar.app.feature_lending.impl.ui.common_component.ToolbarWithHelpButton
import com.jar.app.feature_lending.impl.ui.realtime_flow.bottom_sheet.RealTimeRefreshCreditScoreBottomSheet
import com.jar.app.feature_lending.impl.ui.realtime_flow.bottom_sheet.RealTimeRefreshCreditScoreBottomSheetViewModelAndroid
import com.jar.app.feature_lending.shared.domain.ui_event.RefreshCreditScoreBottomSheetEvent
import com.jar.app.feature_lending.impl.ui.realtime_flow.components.CreditScoreCard
import com.jar.app.feature_lending.impl.ui.realtime_flow.components.RealTimeLandingBenefitsCard
import com.jar.app.feature_lending.impl.ui.realtime_flow.components.RealTimeLandingGenericCard
import com.jar.app.feature_lending.impl.ui.realtime_flow.components.RealTimeLandingStepsCard
import com.jar.app.feature_lending.shared.domain.LendingEventKeyV2
import com.jar.app.feature_lending.shared.domain.model.realTimeFlow.RealTimeLanding
import com.jar.app.feature_lending.shared.util.LendingConstants
import com.jar.app.feature_lending_kyc.api.LendingKycApi
import com.jar.app.core_base.data.dto.KycFeatureFlowType
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@AndroidEntryPoint
internal class RealTimeReadyCashLandingFragment : BaseComposeFragment() {

    @Inject
    lateinit var prefs: PrefsApi

    @Inject
    lateinit var analyticsApi: AnalyticsApi

    @Inject
    lateinit var remoteConfigApi: RemoteConfigApi

    @Inject
    lateinit var lendingKycApi: LendingKycApi


    private val args by navArgs<RealTimeReadyCashLandingFragmentArgs>()
    private val viewModelProvider by viewModels<RealTimeReadyCashLandingViewModelAndroid> { defaultViewModelProviderFactory }
    private val viewModel by lazy{
        viewModelProvider.getInstance()
    }
    private val realTimeRefreshCreditScoreBottomSheetViewModelProvider by viewModels<RealTimeRefreshCreditScoreBottomSheetViewModelAndroid> { defaultViewModelProviderFactory }
    private val realTimeRefreshCreditScoreBottomSheetViewModel by lazy{
        realTimeRefreshCreditScoreBottomSheetViewModelProvider.getInstance()
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getLandingData()
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchLeadStatus()
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
                            viewModel.getLandingData()
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
            panNumberForCreditReport = panNumber?.ifEmpty { null}
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
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                realTimeRefreshCreditScoreBottomSheetViewModel.isRefreshingCreditScore.collect {
                    when (it.status) {
                        RestClientResult.Status.NONE,
                        RestClientResult.Status.LOADING -> {
                            showProgressBar()
                        }

                        RestClientResult.Status.SUCCESS -> {
                            dismissProgressBar()
                            viewModel.getLandingData()
                        }

                        RestClientResult.Status.ERROR -> {
                            dismissProgressBar()
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
        realTimeRefreshCreditScoreBottomSheetViewModel.uiState.collectAsState()
        val screenData by viewModel.realTimeStaticContent.collectAsState()
        val scope = rememberCoroutineScope()


        when (screenData.status) {
            RestClientResult.Status.NONE,
            RestClientResult.Status.LOADING -> {
                analyticsApi.postEvent(
                    LendingEventKeyV2.RLENDING_INITIAL_FLOW,
                    mapOf(
                        LendingEventKeyV2.screen_name to LendingEventKeyV2.FETCHING_CREDIT_SCORE_SCREEN,
                        LendingEventKeyV2.LENDING_EXPERIMENT_TYPE to screenData.data?.data?.realTime?.type.orEmpty(),
                        LendingEventKeyV2.action to LendingEventKeyV2.FETCHING_CREDIT_SCREEN_LOADING,
                        LendingEventKeyV2.source to args.source
                    )
                )
                RealTimeGenericLoading(modifier = Modifier)
            }

            RestClientResult.Status.SUCCESS -> {
                screenData.data?.data?.realTime?.let { data ->
                    val keyboardController = LocalSoftwareKeyboardController.current

                    val modalBottomSheetState = rememberModalBottomSheetState(
                        initialValue = ModalBottomSheetValue.Hidden,
                        skipHalfExpanded = true
                    )
                    ModalBottomSheetLayout(
                        sheetState = modalBottomSheetState,
                        scrimColor = MaterialTheme.colors.onSurface.copy(alpha = 0.5f),
                        sheetContent = {
                            RealTimeRefreshCreditScoreBottomSheet(
                                uiState = bottomSheetUiState,
                                onPanChange = realTimeRefreshCreditScoreBottomSheetViewModel::uiEvent,
                                onNameChange = realTimeRefreshCreditScoreBottomSheetViewModel::uiEvent,
                                onCrossClick = {
                                    analyticsApi.postEvent(
                                        LendingEventKeyV2.RLENDING_INITIAL_FLOW,
                                        mapOf(
                                            LendingEventKeyV2.screen_name to LendingEventKeyV2.ENTER_DETAILS_BS,
                                            LendingEventKeyV2.action to LendingEventKeyV2.BS_CROSS_BUTTON_CLICKED,
                                            LendingEventKeyV2.LENDING_EXPERIMENT_TYPE to (screenData.data?.data?.realTime?.type).orEmpty(),
                                            LendingEventKeyV2.source to args.source
                                        )
                                    )
                                    scope.launch {
                                        modalBottomSheetState.hide()
                                        keyboardController?.hide()
                                    }
                                },
                                onProceedButtonClick = {
                                    analyticsApi.postEvent(
                                        LendingEventKeyV2.RLENDING_INITIAL_FLOW,
                                        mapOf(
                                            LendingEventKeyV2.screen_name to LendingEventKeyV2.ENTER_DETAILS_BS,
                                            LendingEventKeyV2.action to LendingEventKeyV2.ENTER_DETAILSBS_SUBMIT_CLICKED,
                                            LendingEventKeyV2.LENDING_EXPERIMENT_TYPE to LendingEventKeyV2.RLENDING_CREDIT_SCORE
                                        )
                                    )
                                    scope.launch {
                                        modalBottomSheetState.hide()
                                        keyboardController?.hide()
                                    }
                                    if (bottomSheetUiState.experianConsentRequired) {
                                        openOtpBottomScreen(
                                            bottomSheetUiState.name,
                                            bottomSheetUiState.panNo
                                        )
                                    } else {
                                        realTimeRefreshCreditScoreBottomSheetViewModel.uiEvent(
                                            RefreshCreditScoreBottomSheetEvent.OnClickSubmitButtonInRefreshScoreBottomSheet
                                        )
                                    }
                                }
                            )
                        },
                        sheetShape = RoundedCornerShape(topEnd = 16.dp, topStart = 16.dp)
                    ) {
                        RealTimeLandingScreen(
                            realTimeLandingData = data,
                            onBackButtonClick = {
                                handleBackPress()
                            },
                            onHelpButtonClick = {
                                openNeedHelp()
                            },
                            onProceedButtonClick = {
                                analyticsApi.postEvent(
                                    LendingEventKeyV2.RLENDING_INITIAL_FLOW,
                                    mapOf(
                                        LendingEventKeyV2.screen_name to LendingEventKeyV2.FETCHING_CREDIT_SCORE_SCREEN,
                                        LendingEventKeyV2.action to LendingEventKeyV2.CREDIT_SCORE_GETSTARTED_CLICKED,
                                        LendingEventKeyV2.source to args.source,
                                        LendingEventKeyV2.CREDIT_SCORE to (data.headerCard.creditScoreCard?.creditScore
                                            ?: LendingEventKeyV2.NOT_FOUND)
                                    )
                                )
                                navigateNext()
                            },
                            onRefreshMyScoreButtonClick = {
                                analyticsApi.postEvent(
                                    LendingEventKeyV2.RLENDING_INITIAL_FLOW,
                                    mapOf(
                                        LendingEventKeyV2.screen_name to LendingEventKeyV2.FETCHING_CREDIT_SCORE_SCREEN,
                                        LendingEventKeyV2.LENDING_EXPERIMENT_TYPE to screenData.data?.data?.realTime?.type.orEmpty(),
                                        LendingEventKeyV2.action to LendingEventKeyV2.REFRESH_MY_SCORE_CLICKED,
                                        LendingEventKeyV2.source to args.source
                                    )
                                )
                                scope.launch {
                                    modalBottomSheetState.show()
                                }
                            },
                            analyticsApi = analyticsApi,
                            args = args,
                            screenData = screenData.data?.data?.realTime
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
        viewModel.leadStatus?.let {
            when (it.status.orEmpty()) {
                LendingConstants.RealTimeLeadStatus.BANK_DETAILS_SUBMITTED -> { //Bank Details submitted move to bank statement upload
                    navigateTo(
                        LendingNavigationDirections.actionToUploadBankStatementFragment(),
                        shouldAnimate = true
                    )
                }

                LendingConstants.RealTimeLeadStatus.PARTIAL_BANK_STATEMENT_UPLOADED -> { //Partial bank statement uploaded
                    navigateTo(
                        "android-app://com.jar.app/confirmYourBankStatementsFragment/${CtaType.SUBMIT.name}",
                        shouldAnimate = true
                    )
                }

                LendingConstants.RealTimeLeadStatus.BANK_STATEMENT_UPLOADED -> { //Bank statement upload done show Best Offer page.
                    navigateTo(
                        "android-app://com.jar.app/findingBestOfferFragment",
                        shouldAnimate = true,
                        popUpTo = R.id.realTimeReadyCashLandingFragment,
                        inclusive = true
                    )
                }

                else -> {
                    navigateTo(
                        LendingNavigationDirections.actionToAddBankDetailsFragment(),
                        shouldAnimate = true
                    )
                }
            }
        } ?: run {
            navigateTo(
                LendingNavigationDirections.actionToAddBankDetailsFragment(),
                shouldAnimate = true
            )
        }
    }

    private fun handleBackPress() {
        popBackStack()
    }

    private fun openNeedHelp() {
        val sendTo = remoteConfigApi.getWhatsappNumber()
        val number = prefs.getUserPhoneNumber()
        val name = prefs.getUserName()
        val message = getCustomStringFormatted(
            com.jar.app.feature_lending.shared.MR.strings.feature_lending_kyc_contact_support_real_time_help_s_s,
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
internal fun RealTimeLandingScreen(
    modifier: Modifier = Modifier,
    realTimeLandingData: RealTimeLanding,
    onBackButtonClick: () -> Unit = {},
    onHelpButtonClick: () -> Unit = {},
    onProceedButtonClick: () -> Unit = {},
    onRefreshMyScoreButtonClick: () -> Unit = {},
    analyticsApi: AnalyticsApi,
    args: RealTimeReadyCashLandingFragmentArgs,
    screenData: RealTimeLanding?
) {
    Scaffold(
        modifier = modifier
            .fillMaxSize(),
        topBar = {
            ToolbarWithHelpButton(
                onBackButtonClick = onBackButtonClick,
                title = realTimeLandingData.toolbarTitle,
                onHelpButtonClick = onHelpButtonClick
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = colorResource(id = com.jar.app.core_ui.R.color.color_272239))
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Divider(
                        color = colorResource(id = com.jar.app.core_ui.R.color.purple400),
                        thickness = 2.dp
                    )
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(16.dp)
                    )
                    JarPrimaryButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                        text = stringResource(id = com.jar.app.feature_lending.shared.MR.strings.feature_lending_get_started.resourceId),
                        isAllCaps = false,
                        onClick = onProceedButtonClick
                    )
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        backgroundColor = colorResource(id = com.jar.app.core_ui.R.color.bgColor)
    ) { padding ->
        val faqSelectedIndex = rememberSaveable { mutableStateOf<Int>(-1) }
        val faqModel = remember(key1 = realTimeLandingData.faqs) {
            realTimeLandingData.faqs.mapIndexed { index, faq ->
                ExpandableCardModel(
                    id = index,
                    faqHeaderText = faq.question,
                    faqExpandableContentText = faq.answer
                )
            }
        }
        LazyColumn(
            modifier = Modifier
                .padding(padding)
        ) {
            item(key = "header_card") { //header card
                realTimeLandingData.headerCard.genericCard?.let {

                    LaunchedEffect(key1 = Unit) {
                        analyticsApi.postEvent(
                            LendingEventKeyV2.RLENDING_INITIAL_FLOW,
                            mapOf(
                                LendingEventKeyV2.screen_name to LendingEventKeyV2.FETCHING_CREDIT_SCORE_SCREEN,
                                LendingEventKeyV2.action to LendingEventKeyV2.CREDIT_SCORE_LANDING_SCREEN_SHOWN,
                                LendingEventKeyV2.LENDING_EXPERIMENT_TYPE to screenData?.type.orEmpty(),
                                LendingEventKeyV2.CREDIT_STATUS to LendingEventKeyV2.NOT_FOUND,
                                LendingEventKeyV2.source to args.source
                            )
                        )
                    }
                    RealTimeLandingGenericCard(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
                        genericCard = it
                    )
                }
                realTimeLandingData.headerCard.creditScoreCard?.let {
                    LaunchedEffect(key1 = Unit) {
                        analyticsApi.postEvent(
                            LendingEventKeyV2.RLENDING_INITIAL_FLOW,
                            mapOf(
                                LendingEventKeyV2.screen_name to LendingEventKeyV2.FETCHING_CREDIT_SCORE_SCREEN,
                                LendingEventKeyV2.action to LendingEventKeyV2.CREDIT_SCORE_LANDING_SCREEN_SHOWN,
                                LendingEventKeyV2.LENDING_EXPERIMENT_TYPE to screenData?.type.orEmpty(),
                                LendingEventKeyV2.CREDIT_STATUS to LendingEventKeyV2.FOUND,
                                LendingEventKeyV2.source to args.source
                            )
                        )
                    }
                    CreditScoreCard(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
                        creditScoreCard = it
                    )
                }
                if (realTimeLandingData.headerCard.refreshCreditScore) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(top = 0.dp, bottom = 16.dp, start = 20.dp, end = 20.dp)
                                .clickable { onRefreshMyScoreButtonClick.invoke() },
                            text = stringResource(id = com.jar.app.feature_lending.shared.MR.strings.feature_lending_refresh_my_score.resourceId),
                            textDecoration = TextDecoration.Underline,
                            fontSize = 12.sp,
                            fontWeight = FontWeight(600),
                            color = colorResource(id = com.jar.app.core_ui.R.color.color_D5CDF2),
                            lineHeight = 20.sp
                        )
                    }
                }
            }
            item(key = "steps_card") {  //Steps card
                RealTimeLandingStepsCard(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    stepsCard = realTimeLandingData.stepsCard
                )
            }
            item(key = "benefits_card") { //Benefits Card
                RealTimeLandingBenefitsCard(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
                    benefitsCard = realTimeLandingData.benefitsCard
                )
            }
            item(key = "faq_card_title") {//Faq Card
                Column(
                    modifier = Modifier
                        .padding(start = 16.dp, end = 16.dp, top = 8.dp)
                        .fillMaxWidth()
                        .background(
                            color = colorResource(id = com.jar.app.core_ui.R.color.lightBgColor),
                            shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                        )
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 20.dp, top = 16.dp, bottom = 16.dp, end = 16.dp),
                        text = stringResource(id = com.jar.app.feature_lending.shared.MR.strings.feature_lending_faqs.resourceId),
                        style = JarTypography.h5,
                        color = Color(0xFFEEEAFF),
                        lineHeight = 24.sp,
                        fontSize = 16.sp,
                        fontWeight = FontWeight(700),
                        textAlign = TextAlign.Start
                    )
                    Divider(
                        thickness = 1.dp,
                        color = colorResource(id = com.jar.app.core_ui.R.color.color_ACA1D3_opacity_10)
                    )
                }
            }
            items(items = faqModel, key = { it.id }) {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    ExpandableFaqCard(
                        card = it,
                        onCardArrowClick = {
                            faqSelectedIndex.value =
                                if (faqSelectedIndex.value == it.id) -1 else it.id
                        },
                        expanded = faqSelectedIndex.value == it.id,
                        listBackgroundColor = com.jar.app.core_ui.R.color.lightBgColor,
                        elevation = 0.dp,
                        answerTextColor = colorResource(id = com.jar.app.core_ui.R.color.color_D5CDF2),
                        questionTextColor = colorResource(id = com.jar.app.core_ui.R.color.white),
                        questionTextSize = 14.sp,
                        answerTextSize = 14.sp,
                    )
                    if (it.id != faqModel.lastIndex) {
                        Divider(
                            modifier = Modifier
                                .fillMaxWidth(),
                            thickness = 1.dp,
                            color = colorResource(id = com.jar.app.core_ui.R.color.color_ACA1D3_opacity_10)
                        )
                    }
                }
            }
            item(key = "spacer") {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .height(16.dp)
                            .fillMaxWidth()
                            .background(
                                color = colorResource(id = com.jar.app.core_ui.R.color.lightBgColor),
                                shape = RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp)
                            )
                    ) {}
                    Spacer(
                        modifier = Modifier
                            .height(20.dp)
                            .fillMaxWidth()
                    )
                }
            }
        }

    }
}

@Composable
internal fun RealTimeLandingError(
    modifier: Modifier = Modifier,
    title: String = "",
    subTitle: String = ""
) {

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .align(Alignment.Center)
        ) {
            Image(
                modifier = Modifier
                    .size(72.dp)
                    .align(Alignment.CenterHorizontally),
                painter = painterResource(id = R.drawable.feature_leanding_ic_alert),
                contentDescription = null
            )
            Text(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .align(Alignment.CenterHorizontally),
                text = title,
                style = JarTypography.h5,
                color = colorResource(id = com.jar.app.core_ui.R.color.white),
                lineHeight = 24.sp,
                fontSize = 18.sp,
                fontWeight = FontWeight(700),
                textAlign = TextAlign.Center
            )
            if (subTitle.isNotEmpty())
                Text(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .align(Alignment.CenterHorizontally),
                    text = subTitle,
                    style = JarTypography.body2,
                    color = colorResource(id = com.jar.app.core_ui.R.color.color_D5CDF2),
                    fontSize = 14.sp,
                    fontWeight = FontWeight(700),
                    textAlign = TextAlign.Center
                )
        }
    }

}

