package com.jar.health_insurance.impl.ui.select_insurance_plan

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.IconButton
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.util.encodeUrl
import com.jar.app.base.util.isPresentInBackStack
import com.jar.app.base.util.openWhatsapp
import com.jar.app.core_base.domain.model.card_library.FontType
import com.jar.app.core_base.domain.model.card_library.TextData
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.BaseConstants.InternalDeepLinks.ADD_DETAILS_DEEPLINK
import com.jar.app.core_base.util.BaseConstants.InternalDeepLinks.PAYMENT_BOTTOM_SHEET_DEEPLINK
import com.jar.app.core_base.util.BaseConstants.InternalDeepLinks.PAYMENT_STATUS_PAGE_DEEPLINK
import com.jar.app.core_base.util.orFalse
import com.jar.app.core_base.util.orZero
import com.jar.app.core_compose_ui.base.BaseComposeFragment
import com.jar.app.core_compose_ui.component.ErrorToastMessage
import com.jar.app.core_compose_ui.component.JarImage
import com.jar.app.core_compose_ui.component.JarPrimaryButton
import com.jar.app.core_compose_ui.component.debounceClickable
import com.jar.app.core_compose_ui.theme.JarTypography
import com.jar.app.core_compose_ui.theme.jarFontFamily
import com.jar.app.core_compose_ui.utils.HtmlText
import com.jar.app.core_compose_ui.views.RenderBaseToolBar
import com.jar.app.feature_health_insurance.R
import com.jar.app.feature_health_insurance.shared.data.models.landing.BenefitsDetailsV2
import com.jar.app.feature_health_insurance.shared.data.models.select_premium.Main
import com.jar.app.feature_health_insurance.shared.data.models.select_premium.PaymentType
import com.jar.app.feature_health_insurance.shared.data.models.select_premium.Plan
import com.jar.app.feature_health_insurance.shared.data.models.select_premium.PremiumOption
import com.jar.app.feature_health_insurance.shared.data.models.select_premium.SelectPremiumResponse
import com.jar.app.feature_health_insurance.shared.data.models.select_premium.Testimonial
import com.jar.app.feature_health_insurance.shared.domain.events.HealthInsuranceEvents
import com.jar.app.feature_health_insurance.shared.domain.events.HealthInsuranceEvents.Health_Insurance
import com.jar.app.feature_health_insurance.shared.domain.events.HealthInsuranceEvents.Insurance_PlanClicked
import com.jar.app.feature_health_insurance.shared.domain.events.HealthInsuranceEvents.Payment_TypeClicked
import com.jar.app.feature_health_insurance.shared.util.Constants
import com.jar.app.feature_mandate_payment.api.MandatePaymentApi
import com.jar.app.feature_mandate_payment_common.impl.ui.PaymentPageFragmentViewModelAndroid
import com.jar.app.feature_mandate_payments_common.shared.domain.model.initiate_mandate.InitiateMandatePaymentRequest
import com.jar.app.feature_mandate_payments_common.shared.domain.model.initiate_mandate.MandateWorkflowType
import com.jar.app.feature_mandate_payments_common.shared.domain.model.payment_page.PaymentPageHeaderDetail
import com.jar.app.feature_mandate_payments_common.shared.util.MandateErrorCodes
import com.jar.app.feature_mandate_payments_common.shared.util.MandatePaymentCommonConstants
import com.jar.app.feature_one_time_payments.shared.domain.event.AvailableAppEvent
import com.jar.app.feature_payment.api.PaymentManager
import com.jar.app.feature_payment.impl.ui.payment_option.PayNowSection
import com.jar.health_insurance.impl.ui.common.components.TopBarNeedHelpWhatsapp
import com.jar.health_insurance.impl.ui.components.BackPressHandler
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jar_core_network.api.util.collectUnwrapped
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import com.skydoves.balloon.ArrowOrientation
import com.skydoves.balloon.ArrowPositionRules
import com.skydoves.balloon.Balloon
import com.skydoves.balloon.BalloonSizeSpec
import com.skydoves.balloon.compose.Balloon
import com.skydoves.balloon.compose.BalloonWindow
import com.skydoves.balloon.compose.rememberBalloonBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject
import com.jar.app.core_ui.R.color as uiColor
import com.jar.app.feature_mandate_payment_common.impl.model.UpiApp as MandateUpiApp
import com.jar.app.feature_one_time_payments.shared.domain.model.UpiApp as OneTimeApp

@AndroidEntryPoint
class SelectHealthInsurancePlanScreen : BaseComposeFragment() {

    private val viewModel by viewModels<SelectHealthInsuranceViewModel> { defaultViewModelProviderFactory }

    private val args by navArgs<SelectHealthInsurancePlanScreenArgs>()

    @Inject
    lateinit var paymentManger: PaymentManager

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    @Inject
    lateinit var serializer: Serializer

    @Inject
    lateinit var mandatePaymentApi: MandatePaymentApi

    private val viewModelProvider by viewModels<PaymentPageFragmentViewModelAndroid> { defaultViewModelProviderFactory }

    private val paymentPageFragmentViewModel by lazy {
        viewModelProvider.getInstance()
    }

    @Inject
    lateinit var appScope: CoroutineScope

    private var testimonialsJob: Job? = null

    private var mandateUpiApp: MandateUpiApp? = null
    private var oneTimeUpiApp: OneTimeApp? = null

    private var initiateOneTimePaymentJob: Job? = null
    private var initiateMandatePaymentJob: Job? = null

    private var balloonList: MutableList<BalloonWindow>? = mutableListOf<BalloonWindow>()

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData()))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeFlow()
    }

    private fun observeFlow() {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                paymentManger.fetchLastUsedUpiApp()
                    .zip(mandatePaymentApi.fetchLastUsedUpiApp(Constants.INSURANCE_FLOWTYPE)) { oneTimeUpiApp, mandateUpiApp ->
                        return@zip Pair(oneTimeUpiApp, mandateUpiApp)
                    }
                    .flowOn(Dispatchers.IO)
                    .collectLatest { upiAppsPair ->
                        if (upiAppsPair.first.status == RestClientResult.Status.SUCCESS && upiAppsPair.second.status == RestClientResult.Status.SUCCESS) {
                            viewModel.onTriggerEvent(
                                SelectInsuranceEvents.OnDataLoad(
                                    args.orderId,
                                    upiAppsPair.second.data,
                                    upiAppsPair.first.data
                                )
                            )
                            oneTimeUpiApp = null
                            oneTimeUpiApp = upiAppsPair.first.data
                            mandateUpiApp = upiAppsPair.second.data
                        }
                    }
            }
        }


        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.initiateOneTimePaymentWithCustomUiFlow.collect(
                    onLoading = {

                    },
                    onSuccess = {it ->
                        initiateOneTimePaymentJob?.cancel()
                        initiateOneTimePaymentJob = appScope.launch(Dispatchers.Main) {
                            if (it != null) {
                                it.screenSource = Health_Insurance
                                val paymentResponse = encodeUrl(serializer.encodeToString(it))
                                paymentManger.initiateOneTimePaymentWithCustomUI(
                                    fragmentDeepLink = "$PAYMENT_BOTTOM_SHEET_DEEPLINK/${paymentResponse}/${viewModel.insuranceIdSelected}/${null}/-1/manual/${oneTimeUpiApp?.packageName}",
                                    initiatePaymentResponse = it,
                                    customUiFragmentId = R.id.selectHealthInsurancePlanScreen,
                                    isBottomSheet = true
                                )
                                    .collectUnwrapped(onLoading = {
                                        showProgressBar()
                                    }, onSuccess = {
                                        dismissProgressBar()
                                        navigateTo(
                                            "$PAYMENT_STATUS_PAGE_DEEPLINK/${viewModel.insuranceIdSelected}",
                                            popUpTo = R.id.insuranceLandingPage,
                                            inclusive = true
                                        )
                                    }, onError = { errorMessage, _ ->
                                        dismissProgressBar()
                                        if (errorMessage.isNotEmpty()) {
                                            Toast.makeText(
                                                requireContext(), errorMessage, Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    })
                            }
                        }
                    }, onError = { errorMessage, _ ->
                        if (errorMessage.isNotEmpty()) {
                            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT)
                                .show()
                        }
                    })
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.initiatePaymentFlow.collect(onLoading = {

                }, onSuccess = {
                    initiateOneTimePaymentJob?.cancel()
                    initiateOneTimePaymentJob = appScope.launch(Dispatchers.Main) {
                        if (it != null) {
                            it.screenSource = Health_Insurance
                            paymentManger.initiatePaymentWithUpiApp(
                                initiatePaymentResponse = it,
                                upiApp = oneTimeUpiApp!!,
                                initiatePageFragmentId = R.id.selectHealthInsurancePlanScreen
                            ).collectUnwrapped(
                                onLoading = {
                                    showProgressBar()
                                },
                                onSuccess = {
                                    dismissProgressBar()
                                    navigateTo(
                                        "$PAYMENT_STATUS_PAGE_DEEPLINK/${viewModel.insuranceIdSelected}",
                                        popUpTo = R.id.insuranceLandingPage,
                                        inclusive = true
                                    )

                                }, onError = { errorMessage, _ ->
                                    dismissProgressBar()
                                    if (errorMessage.isNotEmpty()) {
                                        Toast.makeText(
                                            requireContext(), errorMessage, Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                })
                        }
                    }
                }, onError = { errorMessage, _ ->
                    if (errorMessage.isNotEmpty()) {
                        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.createProposalResponseFlow.collect(onLoading = {
                    showProgressBar()
                }, onSuccess = { createProposalResponse ->
                    dismissProgressBar()
                    val staticData = viewModel.selectPremiumResponse
                    val amount =
                        if (staticData?.main?.plans?.get(viewModel.getPlanSelected())?.premiumOptions?.get(
                                viewModel.getSubscription()
                            )?.discountPriceTxt != null
                        ) staticData.main?.plans?.get(viewModel.getPlanSelected())?.premiumOptions?.get(
                            viewModel.getSubscription()
                        )!!.discountPriceTxt else staticData?.main?.plans?.get(
                            viewModel.getPlanSelected()
                        )?.premiumOptions?.get(viewModel.getSubscription())?.originalPriceTxt

                    initiateMandatePaymentJob?.cancel()
                    initiateMandatePaymentJob = appScope.launch(Dispatchers.Main) {
                        mandatePaymentApi.initiateMandatePaymentWithUpiApp(
                            paymentPageHeaderDetails = PaymentPageHeaderDetail(
                                toolbarHeader = "Health Insurance",
                                title = "Let’s automate your monthly premium of $amount",
                                featureFlow = "INSURANCE",
                                userLifecycle = Health_Insurance,
                                savingFrequency = MandatePaymentCommonConstants.MandateStaticContentType.INSURANCE_AUTOPAY_SETUP.name,
                                mandateSavingsType = MandatePaymentCommonConstants.MandateStaticContentType.INSURANCE_AUTOPAY_SETUP,
                                toolbarIcon = R.drawable.bg_payment
                            ),

                            initiateMandatePaymentRequest = InitiateMandatePaymentRequest(
                                mandateAmount = 2000f,
                                authWorkflowType = MandateWorkflowType.TRANSACTION,
                                subscriptionType = "INSURANCE",
                                insuranceId = createProposalResponse?.insuranceId,
                            ),

                            upiApp = mandateUpiApp!!,
                            initiateMandateFragmentId = R.id.selectHealthInsurancePlanScreen
                        ).collectUnwrapped(onSuccess = {
                            createProposalResponse?.insuranceId?.let { insuranceId ->
                                navigateTo(
                                    "$PAYMENT_STATUS_PAGE_DEEPLINK/$insuranceId", popUpTo = R.id.insuranceLandingPage, inclusive = true
                                )

                            }
                        }, onError = { errorMessage, errorCode ->
                            dismissProgressBar()
                            if (errorMessage.isNotEmpty()) {
                                Toast.makeText(
                                    requireContext(), errorMessage, Toast.LENGTH_SHORT
                                ).show()
                            }
                            if (errorCode == MandateErrorCodes.BACK_PRESSES_FROM_PAYMENT_SCREEN) {
                                popBackStack(
                                    R.id.selectHealthInsurancePlanScreen, false
                                )
                            }
                        })
                    }
                }, onError = { errorMessage, errorCode ->
                    dismissProgressBar()
                    if (errorMessage.isNotEmpty()) {
                        Toast.makeText(
                            requireContext(), errorMessage, Toast.LENGTH_SHORT
                        ).show()
                    }
                    if (errorCode == MandateErrorCodes.BACK_PRESSES_FROM_PAYMENT_SCREEN) {
                        popBackStack(
                            R.id.selectHealthInsurancePlanScreen, false
                        )
                    }
                    if (errorCode == com.jar.app.feature_mandate_payment.impl.util.MandateErrorCodes.BACK_PRESSES_FROM_PAYMENT_SCREEN) {
                        analyticsHandler.postEvent(
                            HealthInsuranceEvents.Health_Insurance_Event, mapOf(
                                HealthInsuranceEvents.EVENT_NAME to HealthInsuranceEvents.MANDATE_BACK_CLICKED
                            )
                        )
                    }
                    if (errorMessage.isNotEmpty()) {
                        analyticsHandler.postEvent(
                            HealthInsuranceEvents.Health_Insurance_Event, mapOf(
                                HealthInsuranceEvents.EVENT_NAME to HealthInsuranceEvents.MANDATE_BACK_CLICKED
                            )
                        )
                    }
                })
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.initiateMandatePayment.collect(onLoading = {
                    showProgressBar()
                }, onSuccess = { createProposalResponse ->
                    dismissProgressBar()
                    val staticData = viewModel.selectPremiumResponse
                    val amount =
                        if (staticData?.main?.plans?.get(viewModel.getPlanSelected())?.premiumOptions?.get(
                                viewModel.getSubscription()
                            )?.discountPriceTxt != null
                        ) staticData.main?.plans?.get(viewModel.getPlanSelected())?.premiumOptions?.get(
                            viewModel.getSubscription()
                        )!!.discountedPrice?.toFloat() else staticData?.main?.plans?.get(
                            viewModel.getPlanSelected()
                        )?.premiumOptions?.get(viewModel.getSubscription())?.originalPrice?.toFloat()

                    initiateMandatePaymentJob?.cancel()
                    initiateMandatePaymentJob = appScope.launch(Dispatchers.Main) {
                        mandatePaymentApi.initiateMandatePaymentWithCustomUI(
                            paymentPageHeaderDetails = PaymentPageHeaderDetail(
                                toolbarHeader = "Health Insurance",
                                title = "Let’s automate your monthly premium of $amount",
                                featureFlow = "INSURANCE",
                                userLifecycle = Health_Insurance,
                                savingFrequency = MandatePaymentCommonConstants.MandateStaticContentType.INSURANCE_AUTOPAY_SETUP.name,
                                mandateSavingsType = MandatePaymentCommonConstants.MandateStaticContentType.INSURANCE_AUTOPAY_SETUP,
                                toolbarIcon = R.drawable.bg_payment
                            ),

                            initiateMandatePaymentRequest = InitiateMandatePaymentRequest(
                                mandateAmount = 2000f,
                                authWorkflowType = MandateWorkflowType.TRANSACTION,
                                subscriptionType = "INSURANCE",
                                insuranceId = createProposalResponse?.insuranceId,
                            ),

                            customMandateUiFragmentId = R.id.selectHealthInsurancePlanScreen,
                            fragmentDeepLink = "$PAYMENT_BOTTOM_SHEET_DEEPLINK/${null}/${createProposalResponse?.insuranceId}/${amount?.toInt()}/${R.id.selectHealthInsurancePlanScreen}/mandate/${mandateUpiApp?.packageName}",
                        ).collectUnwrapped(onSuccess = {
                            createProposalResponse?.insuranceId?.let { insuranceId ->
                                navigateTo(
                                    "$PAYMENT_STATUS_PAGE_DEEPLINK/$insuranceId", popUpTo = R.id.insuranceLandingPage, inclusive = true
                                )
                            }
                        }, onError = { errorMessage, errorCode ->
                            dismissProgressBar()
                            if (errorMessage.isNotEmpty()) {
                                Toast.makeText(
                                    requireContext(), errorMessage, Toast.LENGTH_SHORT
                                ).show()
                            }
                            if (errorCode == MandateErrorCodes.BACK_PRESSES_FROM_PAYMENT_SCREEN) {
                                popBackStack(
                                    R.id.selectHealthInsurancePlanScreen, false
                                )
                            }
                        })
                    }
                }, onError = { errorMessage, errorCode ->
                    dismissProgressBar()
                    if (errorMessage.isNotEmpty()) {
                        Toast.makeText(
                            requireContext(), errorMessage, Toast.LENGTH_SHORT
                        ).show()
                    }
                    if (errorCode == MandateErrorCodes.BACK_PRESSES_FROM_PAYMENT_SCREEN) {
                        popBackStack(
                            R.id.selectHealthInsurancePlanScreen, false
                        )
                    }
                    if (errorCode == com.jar.app.feature_mandate_payment.impl.util.MandateErrorCodes.BACK_PRESSES_FROM_PAYMENT_SCREEN) {
                        analyticsHandler.postEvent(
                            HealthInsuranceEvents.Health_Insurance_Event, mapOf(
                                HealthInsuranceEvents.EVENT_NAME to HealthInsuranceEvents.MANDATE_BACK_CLICKED
                            )
                        )
                    }
                    if (errorMessage.isNotEmpty()) {
                        analyticsHandler.postEvent(
                            HealthInsuranceEvents.Health_Insurance_Event, mapOf(
                                HealthInsuranceEvents.EVENT_NAME to HealthInsuranceEvents.MANDATE_BACK_CLICKED
                            )
                        )
                    }
                })
            }
        }
    }

    @Composable
    override fun RenderScreen() {
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        SelectInsuranceScreen(uiState = uiState)
    }


    @OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterialApi::class)
    @Composable
    fun SelectInsuranceScreen(
        uiState: SelectInsuranceState,
    ) {
        uiState.errorMessage?.let { errorMessage ->
            ErrorToastMessage(errorMessage = errorMessage) {
                viewModel.onTriggerEvent(SelectInsuranceEvents.OnErrorMessageDisplayed)
            }
        }

        val staticData = uiState.selectPremiumResponse
        val planSelected = viewModel.getPlanSelected()
        val subscriptionSelected = viewModel.getSubscription()

        val onAppChooserClicked: (Boolean) -> Unit = { isMandate ->
            if (isMandate) {
                viewModel.initiateMandatePaymentWithCustomUI(args.orderId)
                viewModel.onPayNowClickedEventAnalytic(
                    HealthInsuranceEvents.UPI_CLICKED,
                    HealthInsuranceEvents.PAYMENT_TYPE_MANDATE_SETUP,
                    true
                )
            } else {
                viewModel.initiateOneTimePaymentWithCustomUi(args.orderId)
                viewModel.onPayNowClickedEventAnalytic(
                    HealthInsuranceEvents.UPI_CLICKED,
                    HealthInsuranceEvents.PAYMENT_TYPE_MANUAL,
                    true
                )
            }
        }

        val onPayNowClicked: (Boolean) -> Unit = { isMandate ->
            if (isMandate) {
                viewModel.initiateMandatePayment(args.orderId)
                viewModel.onPayNowClickedEventAnalytic(
                    HealthInsuranceEvents.PAY_NOW_NEW,
                    HealthInsuranceEvents.PAYMENT_TYPE_MANDATE_SETUP,
                    true
                )
            } else {
                viewModel.initiateOneTimePayment(args.orderId)
                viewModel.onPayNowClickedEventAnalytic(
                    HealthInsuranceEvents.PAY_NOW_NEW,
                    HealthInsuranceEvents.PAYMENT_TYPE_MANUAL,
                    true
                )
            }
        }

        BackPressHandler(onBackPressed = {
            val returnScreenData = encodeUrl(serializer.encodeToString(staticData?.returnScreen))
            navigateTo(
                uri = "${BaseConstants.InternalDeepLinks.SELECT_PLAN_ABANDON_SCREEN_DEEPLINK}/${returnScreenData}"
            )
        })

        ModalBottomSheetLayout(
            sheetContent = {
            },
            scrimColor = colorResource(id = uiColor.color_9E000000),
            sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        ) {
            Scaffold(topBar = {
                RenderBaseToolBar(onBackClick = {
                    val returnScreenData = encodeUrl(serializer.encodeToString(staticData?.returnScreen))
                    navigateTo(
                        uri = "${BaseConstants.InternalDeepLinks.SELECT_PLAN_ABANDON_SCREEN_DEEPLINK}/${returnScreenData}"
                    )
                }, title = staticData?.navigationTitle.orEmpty(), RightSection = {
                    staticData?.needHelp?.let { needHelp ->
                        TopBarNeedHelpWhatsapp(modifier = Modifier
                            .padding(end = 16.dp)
                            .debounceClickable {
                                analyticsHandler.postEvent(
                                    HealthInsuranceEvents.Health_Insurance_Event, mapOf(
                                        HealthInsuranceEvents.EVENT_NAME to HealthInsuranceEvents.Insurance_Need_Help_Clicked
                                    )
                                )
                                val intitialMessage = needHelp.whatsappText.orEmpty()
                                val whatsappNumber = needHelp.whatsappNumber.orEmpty()
                                requireContext().openWhatsapp(
                                    whatsappNumber, intitialMessage
                                )
                            },
                        iconLeftText = stringResource(id = R.string.need_help)
                    )
                    }
                })
            }, bottomBar = {
                Column {
                    val planOptions = staticData?.main?.plans?.get(planSelected)?.premiumOptions
                    val paymentType =
                        planOptions?.get(viewModel.getSubscription())?.premiumTypeTxt.orEmpty()

                    if (paymentType == PaymentType.MONTHLY.value) {
                        if (mandateUpiApp != null) {
                            staticData?.let {
                                Column(
                                    modifier = Modifier.padding(
                                        top = 20.dp, start = 16.dp, end = 16.dp
                                    )
                                ) {
                                    PayNowSection(
                                        mandateUpiApp = mandateUpiApp,
                                        isMandate = true,
                                        payNowCtaText = if(!staticData.main?.plans?.get(viewModel.getPlanSelected())?.premiumOptions?.get(uiState.subscriptionSelected)?.discountPriceTxt.isNullOrEmpty())"${it.main?.ctaText} ${staticData.main?.plans?.get(viewModel.getPlanSelected())?.premiumOptions?.get(uiState.subscriptionSelected)?.discountPriceTxt}" else "${it.main?.ctaText} ${
                                            staticData.main?.plans?.get(
                                                viewModel.getPlanSelected()
                                            )?.premiumOptions?.get(uiState.subscriptionSelected)?.originalPriceTxt
                                        }",
                                        appChooserText = "PAY USING",
                                        onAppChooserClicked = onAppChooserClicked,
                                        onPayNowClicked = onPayNowClicked
                                    )

                                    staticData?.let {
                                        PartnerImage(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 6.dp, bottom = 16.dp),
                                            partnerText = it.main?.partnership?.text.orEmpty(),
                                            partnerImageUrl = it.main?.partnership?.imgUrl,
                                        )
                                    }
                                }
                            }
                        } else {
                            staticData?.main?.plans?.get(viewModel.getPlanSelected())?.premiumOptions?.get(uiState.subscriptionSelected)?.ctaText?.let {
                                JarPrimaryButton(
                                    text = it,
                                    isAllCaps = false,
                                    onClick = {
                                        viewModel.initiateMandatePaymentWithCustomUI(args.orderId)
                                        viewModel.onPayNowClickedEventAnalytic(HealthInsuranceEvents.PAY_NOW_NEW, HealthInsuranceEvents.PAYMENT_TYPE_MANDATE_SETUP, false)
                                    }, modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(
                                            start = 16.dp, end = 16.dp, top = 20.dp, bottom = 20.dp
                                        )
                                )
                            }

                                staticData?.let {
                                    PartnerImage(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 6.dp, bottom = 16.dp),
                                        partnerText = it.main?.partnership?.text.orEmpty(),
                                        partnerImageUrl = it.main?.partnership?.imgUrl,
                                    )
                                }
                        }
                    } else {
                        if (oneTimeUpiApp != null) {
                            staticData?.let {
                                Column(
                                    modifier = Modifier.padding(
                                        top = 20.dp, start = 16.dp, end = 16.dp
                                    )
                                ) {
                                    PayNowSection(
                                        oneTimeUpiApp = oneTimeUpiApp,
                                        payNowCtaText = if(!staticData.main?.plans?.get(viewModel.getPlanSelected())?.premiumOptions?.get(uiState.subscriptionSelected)?.discountPriceTxt.isNullOrEmpty())"${it.main?.ctaText} ${staticData.main?.plans?.get(viewModel.getPlanSelected())?.premiumOptions?.get(uiState.subscriptionSelected)?.discountPriceTxt}" else "${it.main?.ctaText} ${
                                            staticData.main?.plans?.get(
                                                viewModel.getPlanSelected()
                                            )?.premiumOptions?.get(uiState.subscriptionSelected)?.originalPriceTxt
                                        }",
                                        appChooserText = "PAY USING",
                                        onAppChooserClicked = onAppChooserClicked,
                                        onPayNowClicked = onPayNowClicked
                                    )

                                    staticData?.let {
                                        PartnerImage(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 6.dp, bottom = 16.dp),
                                            partnerText = it.main?.partnership?.text.orEmpty(),
                                            partnerImageUrl = it.main?.partnership?.imgUrl,
                                        )
                                    }
                                }
                            }
                        } else {
                            staticData?.main?.plans?.get(viewModel.getPlanSelected())?.premiumOptions?.get(uiState.subscriptionSelected)?.ctaText?.let {
                                JarPrimaryButton(
                                    text = it,
                                    isAllCaps = false,
                                    onClick = {
                                        viewModel.initiateMandatePaymentWithCustomUI(args.orderId)
                                        viewModel.onPayNowClickedEventAnalytic(HealthInsuranceEvents.PAY_NOW_NEW, HealthInsuranceEvents.PAYMENT_TYPE_MANDATE_SETUP, false)
                                    }, modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(
                                            start = 16.dp, end = 16.dp, top = 20.dp, bottom = 20.dp
                                        )
                                )
                            }

                            staticData?.let {
                                PartnerImage(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 6.dp, bottom = 16.dp),
                                    partnerText = it.main?.partnership?.text.orEmpty(),
                                    partnerImageUrl = it.main?.partnership?.imgUrl,
                                )
                            }

                        }
                    }
                }
            }, backgroundColor = colorResource(id = uiColor.color_141021), modifier = Modifier.pointerInteropFilter {
                when (it.action) {
                    MotionEvent.ACTION_DOWN -> {
                        viewModel.cancelTimer()
                        balloonList?.forEach {
                            it.dismiss()
                        }
                    }
                }
                false
            }) { contentPadding ->

                val builder = rememberBalloonBuilder {
                    setArrowSize(10)
                    setArrowPositionRules(ArrowPositionRules.ALIGN_ANCHOR)
                    setArrowOrientation(ArrowOrientation.TOP)
                    setWidth(BalloonSizeSpec.WRAP)
                    setHeight(BalloonSizeSpec.WRAP)
                    setPadding(8)
                    setTextSize(8f)
                    setCornerRadius(8f)
                    setBackgroundColorResource(com.jar.app.core_ui.R.color.white)
                    setMinWidth(270)
                    setDismissWhenTouchOutside(true)
                    // setShouldPassTouchEventToAnchor(true) @TODO to be debugged later by @Prasenjit
                }


                LazyColumn(
                    modifier = Modifier
                        .background(colorResource(id = uiColor.color_141021))
                        .padding(contentPadding),
                    contentPadding = PaddingValues()

                ) {
                    item {
                        staticData?.let {
                            SelectPremiumHeaderSection(
                                it, builder, uiState.shouldShowPopUp
                            )
                        }
                    }

                    item {
                        staticData?.let {
                            PlansAvailable(
                                it, planSelected, it.main?.recommendedPlanIndex.orZero()
                            )
                        }
                    }

                    item{
                        staticData?.let{
                            PlanInfoSection(
                                planSelected = planSelected,
                                plans = it.main?.plans,
                                modifier = Modifier
                                    .padding(
                                        top = 12.dp,
                                        start = 16.dp
                                    )
                            )
                        }
                    }

                    item{
                        staticData?.let{
                            BenefitsSection(
                                planSelected = planSelected,
                                plans = it.main?.plans,
                                modifier = Modifier
                                    .padding(
                                        start = 16.dp,
                                        end = 16.dp,
                                        top = 24.dp
                                    ),
                                main = it.main
                            )
                        }
                    }

                    item {
                        staticData?.let {
                            SelectSubscriptionType(
                                modifier = Modifier.padding(bottom = 16.dp),
                                staticData = it,
                                planSelected = planSelected,
                                subscriptionSelected = subscriptionSelected,
                                builder = builder,
                                timerState = uiState.shouldShowPopUp
                            )
                        }
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalGlideComposeApi::class)
    @Composable
    fun PartnerImage(
        modifier: Modifier = Modifier,
        partnerText: String,
        partnerImageUrl: String?,
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = partnerText,
                color = Color(0xFFB4B4B4),
                fontSize = 10.sp,
            )

            JarImage(
                imageUrl = partnerImageUrl,
                contentDescription = null,
                modifier = Modifier
                    .height(16.dp)
                    .padding(start = 8.dp)
            )
        }
    }

    @OptIn(ExperimentalGlideComposeApi::class)
    @SuppressLint("UnrememberedMutableState")
    @Composable
    fun SelectSubscriptionType(
        staticData: SelectPremiumResponse,
        modifier: Modifier = Modifier,
        planSelected: Int,
        subscriptionSelected: Int,
        builder: Balloon.Builder,
        timerState: Boolean,
    ) {
        Card(
            backgroundColor = Color(0xFF2E2942),
            shape = RoundedCornerShape(size = 12.dp),
            modifier = modifier.padding(start = 16.dp, end = 16.dp, top = 20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(start = 16.dp, top = 24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = staticData.main?.choosePremium?.text.orEmpty(),
                        style = JarTypography.h6.copy(color = Color.White),
                        modifier = Modifier.weight(1f)

                    )
                    Balloon(builder = builder, balloonContent = {
                        val balloonText =
                            "Cost that you will pay in exchange for the insurance coverage provided by a policy."
                        androidx.compose.material3.Text(
                            modifier = Modifier.fillMaxWidth(), text = balloonText ?: ""
                        )
                    }) { balloonWindow ->
                        balloonList?.add(balloonWindow)
                        if (timerState) {
                            balloonWindow.showAlignBottom(yOff = 10)
                        } else {
                            balloonWindow.dismiss()
                        }
                        IconButton(
                            onClick = {
                                balloonWindow.showAlignBottom(yOff = 10)
                            },
                            modifier = Modifier
                                .padding(end = 12.dp)
                                .size(16.dp)
                                .align(Alignment.CenterVertically)
                        ) {
                            JarImage(
                                modifier = Modifier
                                    .size(16.dp)
                                    .padding(top = 2.dp)
                                    .clickable {
                                        balloonWindow.showAlignBottom(yOff = 10)
                                    },
                                imageUrl = staticData.main?.choosePremium?.infoImgUrl,
                                contentDescription = "infoImage"
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min)
                        .padding(top = 20.dp, start = 16.dp, end = 16.dp),
                ) {

                    val planOptions = staticData.main?.plans?.get(planSelected)?.premiumOptions

                    val onSelection: (Int) -> Unit = { index ->
                        viewModel.onTriggerEvent(SelectInsuranceEvents.OnSubscriptionSelected(index))
                        val paymentType = planOptions?.get(index)?.premiumTypeTxt.orEmpty()
                        analyticsHandler.postEvent(
                            HealthInsuranceEvents.Health_Insurance_Event, mapOf(
                                HealthInsuranceEvents.Payment_type to paymentType,
                                HealthInsuranceEvents.EVENT_NAME to Payment_TypeClicked
                            )
                        )
                    }

                    planOptions?.forEachIndexed { index, option ->
                        option?.let {
                            Spacer(modifier = Modifier.width(if (index == planOptions.size.minus(1)) 12.dp else 0.dp))
                            SubscriptionTypeCard(
                                isSelected = index == subscriptionSelected,
                                onSelection,
                                index = index,
                                option,
                                Modifier
                                    .weight(1f, false)
                                    .fillMaxHeight()
                            )
                        }
                    }
                }

                staticData?.testimonials?.let { testimonials ->
                    TestimonialsCarousal(
                        testimonials = testimonials,
                        modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
                    )
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onAvailableAppsEvent(availableAppEvent: AvailableAppEvent) {
        viewModel.setUpiApps(availableAppEvent.upiApps)
    }


    @Composable
    fun PlansAvailable(
        staticData: SelectPremiumResponse, planSelected: Int, recommendedIndex: Int
    ) {
        val onSelection: (Int) -> Unit = { index ->
            val plan = staticData.main?.plans?.get(index)?.coverageTxt
            plan?.let {
                analyticsHandler.postEvent(
                    HealthInsuranceEvents.Health_Insurance_Event, mapOf(
                        HealthInsuranceEvents.Plan to it,
                        HealthInsuranceEvents.EVENT_NAME to Insurance_PlanClicked
                    )
                )
            }
            val premiumOptionList = staticData.main?.plans?.get(index)?.premiumOptions
            premiumOptionList?.let {
                viewModel.onTriggerEvent(SelectInsuranceEvents.OnPlanSelection(index, it))
            }
        }

        LazyRow(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .padding(top = 24.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.Bottom
        ) {
            if (staticData.main != null) {
                itemsIndexed(staticData.main!!.plans) { index, plan ->
                    if (plan != null) {
                        InsurancePlanCard(
                            plan = plan,
                            isSelected = index == planSelected,
                            isRecommended = index == recommendedIndex,
                            defaultCardText = staticData.main!!.defaultPlanTxt.orEmpty(),
                            onSelection,
                            index
                        )
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalGlideComposeApi::class)
    @Composable
    fun SubscriptionTypeCard(
        isSelected: Boolean,
        onSelection: (Int) -> Unit,
        index: Int,
        premiumOption: PremiumOption,
        modifier: Modifier = Modifier,
    ) {
        Box(modifier = modifier) {
            Card(backgroundColor = if (isSelected) Color(0xFF474063) else Color(0xFF2E2942),
                shape = RoundedCornerShape(12.dp),
                border = if (isSelected) BorderStroke(
                    1.dp, Color(0xFFC5B0FF)
                ) else BorderStroke(1.dp, Color(0xFF8C80B6)),
                modifier = Modifier
                    .fillMaxHeight()
                    .align(Alignment.TopStart)
                    .padding(top = 10.dp)
                    .clickable {
                        onSelection(index)
                    }) {
                Column(
                    modifier = Modifier.heightIn(min = 136.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        premiumOption.premiumTypeTxt?.let {
                            Text(
                                text = it, style = JarTypography.body2.copy(
                                    color = Color.White,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                ), modifier = Modifier
                                    .padding(start = 12.dp)
                                    .weight(1f)
                            )

                            Image(
                                modifier = Modifier.padding(end = 12.dp),
                                painter = painterResource(id = if (isSelected) R.drawable.ic_radio_button_selected else R.drawable.ic_radio_button_unselected),
                                contentDescription = null,
                            )
                        }
                    }

                    Divider(
                        modifier = Modifier.height(1.dp),
                        color = if (isSelected) Color(0xFFC5B0FF) else Color(0xFF8C80B6)
                    )

                    premiumOption.originalPriceTxt?.let {
                        Text(
                            text = it,
                            fontSize = if (premiumOption.discountPriceTxt != null) 12.sp else 16.sp,
                            color = Color.White,
                            style = if (premiumOption.discountPriceTxt != null) TextStyle(
                                textDecoration = TextDecoration.LineThrough
                            ) else TextStyle(),
                            modifier = Modifier.padding(start = 16.dp, top = 16.dp),
                            fontWeight = when {
                                isSelected -> FontWeight.Bold
                                premiumOption.discountPriceTxt != null -> FontWeight.Normal
                                else -> FontWeight.Normal
                            }
                        )
                    }

                    premiumOption.discountPriceTxt?.let {
                        Text(
                            text = it, style = JarTypography.h6.copy(
                                color = Color.White,
                            ), modifier = Modifier.padding(start = 16.dp, top = 6.dp)
                        )
                    }

                    premiumOption.yearlyCalcTxt?.let {
                        Text(
                            text = it,
                            fontSize = 12.sp,
                            color = Color.White,
                            fontWeight = FontWeight(300),
                            modifier = Modifier.padding(start = 16.dp, top = 6.dp)
                        )
                    }

                    premiumOption.gstTxt?.let {
                        Text(
                            text = it,
                            fontSize = 10.sp,
                            color = Color(0xFFACA1D3),
                            modifier = Modifier.padding(start = 16.dp, top = 6.dp)
                        )
                    }

                    premiumOption.newSavingTxt?.let {
                        Text(
                            text = convertToAnnotatedString(it),
                            fontSize = 10.sp,
                            color = Color(0xFF58DDC8),
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    premiumOption.renewalDetails?.let{

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                        ){
                            JarImage(
                                imageUrl = if(isSelected) it.activeIcon else it.inActiveIcon, contentDescription = null, modifier = Modifier
                                .size(24.dp)
                                .padding(start = 12.dp))

                            Spacer(
                                modifier = Modifier.width(6.dp)
                            )

                            Text(
                                text = it.text,
                                style = JarTypography.overline.copy(
                                    color = if (isSelected) Color.White else colorResource(
                                        id = uiColor.color_ACA1D3
                                    )
                                ),
                                modifier = Modifier
                                    .padding(end = 8.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                }
            }

            premiumOption.discountPercTxt?.let {
                Card(
                    shape = RoundedCornerShape(
                        topStart = 6.dp, topEnd = 6.dp, bottomStart = 0.dp, bottomEnd = 6.dp
                    ),
                    modifier = Modifier
                        .heightIn(20.dp)
                        .widthIn(60.dp),
                    backgroundColor = Color(0xFFEBB46A),
                ) {
                    Text(
                        text = it,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        fontSize = 10.sp,
                        modifier = Modifier
                            .background(Color(0xFFEBB46A))
                            .align(Alignment.Center)
                            .padding(top = 3.dp, start = 10.dp, end = 10.dp)
                    )
                }
            }
        }
    }

    @Composable
    fun InsurancePlanCard(
        plan: com.jar.app.feature_health_insurance.shared.data.models.select_premium.Plan,
        isSelected: Boolean,
        isRecommended: Boolean,
        defaultCardText: String,
        onSelection: (Int) -> Unit,
        index: Int,
    ) {

        Card(shape = RoundedCornerShape(size = 6.dp),
            border = if (!isSelected) BorderStroke(1.dp, Color(0xFF3C3357)) else BorderStroke(
                0.dp, Color.Transparent
            ),
            backgroundColor = if (isSelected) Color(0xFF7745FF) else Color(0xFF141021),
            modifier = Modifier
                .widthIn(max = if (isSelected) 130.dp else 100.dp)
                .heightIn(min = 60.dp)
                .clickable {
                    onSelection(index)
                }) {
            Column {
                if (isRecommended) {
                    Text(
                        text = defaultCardText,
                        color = Color(0xFF272239),
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        fontSize = 8.sp,
                        modifier = Modifier
                            .background(Color(0xFFEBB46A))
                            .padding(top = 6.dp, bottom = 6.dp, start = 16.dp, end = 16.dp)
                            .fillMaxWidth()
                    )
                }

                Row {
                    if (isSelected) {
                        Image(
                            painter = painterResource(id = com.jar.app.core_ui.R.drawable.core_ui_ic_green_tick),
                            contentDescription = "",
                            modifier = Modifier
                                .padding(start = 16.dp, top = 4.dp)
                                .align(Alignment.CenterVertically)
                        )
                    }

                    Column(
                        modifier = Modifier.padding(
                            start = if (isSelected) 8.dp else 16.dp,
                            end = 16.dp,
                            top = if (isRecommended) 5.dp else 11.dp,
                            bottom = if (isRecommended) 8.dp else 0.dp
                        )
                    ) {
                        Text(
                            text = plan.name ?: "",
                            color = Color.White,
                            fontSize = 12.sp,
                        )
                        Text(
                            text = plan.coverageTxt ?: "",
                            color = Color.White,
                            fontSize = 16.sp,
                        )
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun TestimonialsCarousal(
        modifier: Modifier = Modifier, testimonials: List<Testimonial>
    ) {
        val listState = rememberLazyListState(Int.MAX_VALUE / 2)

        LazyRow(
            state = listState,
            modifier = modifier,
            flingBehavior = rememberSnapFlingBehavior(lazyListState = listState),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            userScrollEnabled = false,
        ) {

            items(Int.MAX_VALUE, itemContent = {
                val index = it % 3
                Testimonials(testimonialsData = testimonials[index])    // item composable
            })
        }

        LaunchedEffect(key1 = listState.firstVisibleItemIndex) {
            delay(5000)
            listState.animateScrollToItem(
                index = listState.firstVisibleItemIndex + 1
            )
        }
    }

    @OptIn(ExperimentalGlideComposeApi::class)
    @Composable
    fun Testimonials(
        modifier: Modifier = Modifier, testimonialsData: Testimonial
    ) {
        val screenWidth = LocalConfiguration.current.screenWidthDp.dp
        val alignment = remember { mutableStateOf(Alignment.CenterVertically) }

        Row(
            verticalAlignment = alignment.value,
            modifier = modifier
                .widthIn(max = (screenWidth - 64.dp))
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xff423C5B))
        ) {
            JarImage(
                imageUrl = testimonialsData.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .padding(start = 12.dp, top = 12.dp, bottom = 12.dp)
                    .size(height = 30.dp, width = 30.dp)
            )

            Text(text = convertToAnnotatedString(testimonialsData.text),
                style = JarTypography.body2.copy(fontSize = 10.sp, color = colorResource(id = uiColor.color_EEEAFF)),
                modifier = Modifier.padding(start = 8.dp, top = 12.dp, end = 14.dp, bottom = 12.dp),
                onTextLayout = { it ->
                    if (it.lineCount >= 3) {
                        alignment.value = Alignment.Top
                    }
                })
        }
    }

    @OptIn(ExperimentalGlideComposeApi::class)
    @Composable
    fun PlanInfoSection(
        plans: List<Plan?>?,
        planSelected: Int,
        modifier: Modifier = Modifier
    ){
        plans?.get(planSelected)?.infoText?.let{
            Row(
                modifier = modifier
            ){
                plans.getOrNull(planSelected)?.infoTextIcon?.let{
                    JarImage(
                        imageUrl = it,
                        contentDescription = null,
                        modifier = Modifier
                            .size(16.dp)
                            .padding(end = 6.dp)
                    )
                }
                plans.getOrNull(planSelected)?.infoText?.let{
                    Text(
                        text =  com.jar.app.core_compose_ui.utils.convertToAnnotatedString(it),
                    )
                }
            }
        }
    }

    @OptIn(ExperimentalGlideComposeApi::class)
    @Composable
    fun BenefitsSection(
        modifier: Modifier = Modifier,
        plans: List<Plan?>?,
        planSelected: Int,
        main: Main?
    ){
        val benefits = plans?.get(planSelected)?.planBenefits

        Column(
            modifier = modifier
                .background(
                    colorResource(id = uiColor.color_262139),
                    shape = RoundedCornerShape(12.dp)
                )
                .clickable {
                    analyticsHandler.postEvent(
                        HealthInsuranceEvents.Health_Insurance_Event, mapOf(
                            HealthInsuranceEvents.EVENT_NAME to HealthInsuranceEvents.Plan_Benefits_Clicked
                        )
                    )
                    main?.providerId?.let {
                        navigateTo(
                            SelectHealthInsurancePlanScreenDirections.actionSelectHealthInsurancePlanScreenToInsurancePlanComparison(
                                it
                            )
                        )
                    }

                }
        ){
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 18.dp),
                verticalAlignment = Alignment.CenterVertically
            ){
                benefits?.headerText?.let{
                    Text(
                        text = it,
                        style = JarTypography.body1.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        ),
                        modifier = Modifier
                            .padding(start = 12.dp)
                    )
                }
                Spacer(modifier.weight(1f))

                benefits?.benefitsCTAText?.let {  benefitsCTAText ->
                    benefits?.benefitsCTAIcon?.let { benefitsCTAIcon ->
                        Row(
                            modifier = Modifier
                                .padding(end = 12.dp)
                                .drawBehind {
                                    drawLine(
                                        start = Offset(x = 0f, y = size.height),
                                        end = Offset(x = size.width, size.height),
                                        color = Color.White,
                                        strokeWidth = 1.dp.toPx()
                                    )
                                }
                        ) {
                            Text(
                                text = benefitsCTAText,
                                style = JarTypography.overline.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                ),
                                modifier = Modifier.padding(end = 5.dp)
                            )

                            JarImage(
                                imageUrl = benefitsCTAIcon,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(10.dp)
                            )
                        }
                    }
                }

            }

            Spacer(modifier = Modifier.height(24.dp))

            benefits?.benefitsDetailsList?.let{ benefitsList ->
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 12.dp,
                            end = 12.dp,
                            bottom = 18.dp
                        ),
                    horizontalArrangement = Arrangement.SpaceAround
                ){
                    items(benefitsList.size){ index ->
                        BenefitsListView(benefitsDetails = benefitsList[index], modifier = Modifier
                            .fillMaxHeight())
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalGlideComposeApi::class)
    @Composable
    fun BenefitsListView(
        benefitsDetails: BenefitsDetailsV2,
        modifier: Modifier = Modifier
    ){
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            JarImage(
                imageUrl = benefitsDetails?.icon.orEmpty(),
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = convertToAnnotatedString(benefitsDetails.text),
                style = JarTypography.overline.copy(color = Color.White, textAlign = TextAlign.Center)
            )
        }
    }


    @OptIn(ExperimentalGlideComposeApi::class)
    @Composable
    fun SelectPremiumHeaderSection(
        staticData: SelectPremiumResponse, builder1: Balloon.Builder, timeState: Boolean
    ) {

        Box {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp, start = 20.dp)
            ) {

                HtmlText(
                    text = staticData.header?.text1 ?: "",
                    textSize = 32f,
                )
                Row(
                    modifier = Modifier.height(IntrinsicSize.Min),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    HtmlText(
                        text = staticData.header?.text2 ?: "", textSize = 32f, modifier = Modifier
                    )
                    staticData.header?.infoImgUrl?.let { infoImgUrl ->
                        Balloon(builder = builder1, balloonContent = {
                            val balloonText =
                                "Amount of money that the insurance policy will pay out in the event of a covered loss or claim"
                            androidx.compose.material3.Text(
                                modifier = Modifier.fillMaxWidth(), text = balloonText ?: ""
                            )
                        }) { balloonWindow ->
                            balloonList?.add(balloonWindow)
                            if (timeState) {
                                balloonWindow.showAlignBottom(yOff = 10)
                            } else {
                                balloonWindow.dismiss()
                            }
                            JarImage(
                                modifier = Modifier
                                    .size(16.dp)
                                    .padding(top = 2.dp)
                                    .clickable {
                                        balloonWindow.showAlignBottom(yOff = 10)
                                    }, imageUrl = infoImgUrl, contentDescription = "infoImage"
                            )
                        }
                    }

                }
            }
        }
    }

    override fun setup(savedInstanceState: Bundle?) {
        setStatusBarColor(com.jar.app.core_ui.R.color.color_141021)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        balloonList = null
        testimonialsJob?.cancel()
    }

    override fun onDestroy() {
        initiateOneTimePaymentJob?.cancel()
        initiateMandatePaymentJob?.cancel()
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    fun convertToAnnotatedString(
        textList: List<TextData>, separator: String = " "
    ): AnnotatedString {
        return buildAnnotatedString {
            if (textList.isEmpty()) return@buildAnnotatedString
            textList.forEach {
                val text = it.text

                var textStyle = SpanStyle(fontFamily = jarFontFamily)

                if (it.fontType?.contains(FontType.BOLD.name).orFalse()) {
                    textStyle = textStyle.copy(fontWeight = FontWeight.Bold)
                }
                if (it.fontType?.contains(FontType.UNDERLINE.name).orFalse()) {
                    textStyle = textStyle.copy(textDecoration = TextDecoration.Underline)
                }
                textStyle = textStyle.copy(
                    fontSize = it.textSize.sp,
                    color = Color(android.graphics.Color.parseColor(it.textColor))
                )
                withStyle(textStyle) {
                    append(text + separator)
                }
            }
        }
    }
}
