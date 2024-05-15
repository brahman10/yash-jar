package com.jar.gold_redemption.impl.ui.cart_complete_payment


import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.base.util.DispatcherProvider
import com.jar.app.base.util.decodeUrl
import com.jar.app.base.util.encodeUrl
import com.jar.app.base.util.openWhatsapp
import com.jar.app.core_base.util.orZero
import com.jar.app.core_compose_ui.component.JarPrimaryButton
import com.jar.app.core_compose_ui.component.JarSecondaryButton
import com.jar.app.core_compose_ui.theme.JarTypography
import com.jar.app.core_compose_ui.views.HeaderCircleIconComponent
import com.jar.app.core_compose_ui.views.LabelAndValueCompose
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.internal.library.jar_core_network.api.util.collectUnwrapped
import com.jar.app.core_ui.extension.toast
import com.jar.app.feature_gold_redemption.R
import com.jar.app.feature_payment.api.PaymentManager
import com.jar.app.feature_one_time_payments_common.shared.FetchManualPaymentStatusResponse
import com.jar.app.feature_one_time_payments.shared.data.model.base.InitiatePaymentResponse
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.CTA_BUTTON
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.GOLD_BENEFIT_AMOUNT
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.GOLD_BENEFIT_PERCENTAGE
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.GO_TO_CONTINUE_SHOPPING
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.GO_TO_MY_ORDERS
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.MINIMUM_VOUCHER_AMOUNT
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.ORDER_ID
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.PAID_PLATFORM
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.REDEMPTION_AVAILABILITY
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.Redemption_VoucherPurchaseStatusScreenCTAClicked
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.Redemption_PaymentReceivedStatusScreenLaunched
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.SCREEN_STATUS
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.VOUCHER_AMOUNT
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.VOUCHER_QUANTITY
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.VOUCHER_TITLE
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.VOUCHER_TYPE
import com.jar.app.core_compose_ui.base.BaseComposeFragment
import com.jar.app.core_compose_ui.views.payments.TimelineViewData
import com.jar.gold_redemption.impl.ui.common_ui.GreenBannerGold
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.Redemption_ContactSupportClicked
import com.jar.app.feature_gold_redemption.shared.data.network.model.request.GoldRedemptionInitiateCreateOrderRequest
import com.jar.app.feature_gold_redemption.shared.domain.model.GoldRedemptionManualPaymentStatus
import com.jar.app.feature_gold_redemption.shared.data.network.model.UserVoucher
import com.jar.app.feature_gold_redemption.shared.domain.model.getGoldRedemptionStatusForAnalytics
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
internal class CompletePaymentFragment : BaseComposeFragment() {

    private val args by navArgs<CompletePaymentFragmentArgs>()
    private val viewModel by viewModels<CompletePaymentFragmentViewModel> { defaultViewModelProviderFactory }

    @Inject
    lateinit var paymentManager: PaymentManager

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    @Inject
    lateinit var remoteConfigManager: RemoteConfigApi

    @Inject
    lateinit var serializer: Serializer
    companion object {
        //After 30 seconds of no API response UI should switch to pending state
        const val TIMEOUT_IN_MILLIS = 30000L
        const val POLLING_ATTEMPTS = 10
        const val POLLING_INTERVAL = 5 * 1000L
    }

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    @Composable
    internal fun RenderCelebratationLottie() {
        val composition by rememberLottieComposition(LottieCompositionSpec.Url(BaseConstants.LottieUrls.CONFETTI_FROM_TOP))
        val progress by animateLottieCompositionAsState(composition, iterations = 1)

        LottieAnimation(
            modifier = Modifier.fillMaxSize(),
            composition = composition,
            progress = { progress },
            contentScale = ContentScale.FillWidth,
        )
    }

    @Preview
    @Composable
    override fun RenderScreen() {
        val voucherDetailsList = viewModel.voucherDetailsList.observeAsState()
        val voucherCardList = viewModel.voucherCardList.observeAsState()
        val refundList = viewModel.refundList.observeAsState()
        val showMyOrdersButton = viewModel.showMyOrdersButton.observeAsState()
        val showContinueShoppingButton = viewModel.showContinueShoppingButton.observeAsState()
        val paymentDetailsList = viewModel.paymentDetailsList.observeAsState()
        val orderStatusLiveData = viewModel.orderStatusLiveData.observeAsState()
        val statusBottomText = viewModel.statusBottomText.observeAsState()
        val title = viewModel.title.observeAsState()
        val bonusGoldText = viewModel.bonusGoldText.observeAsState()
        val finalStatus = viewModel.finalStatus.observeAsState()

        val renderCelebratationLottie = remember { mutableStateOf(false) }
        LaunchedEffect(key1 = finalStatus.value, block = {
            if (finalStatus.value in setOf(GoldRedemptionManualPaymentStatus.SUCCESS, GoldRedemptionManualPaymentStatus.COMPLETED) && !renderCelebratationLottie.value) {
                renderCelebratationLottie.value = true
            }
        })

        Box(Modifier.fillMaxSize()) {
            RenderMainScreen(
                voucherDetailsList,
                voucherCardList,
                paymentDetailsList,
                orderStatusLiveData,
                statusBottomText,
                title,
                bonusGoldText,
                finalStatus,
                refundList,
                showMyOrdersButton,
                showContinueShoppingButton
            )
            if (renderCelebratationLottie.value)
                RenderCelebratationLottie()
        }
    }

    @Composable
    private fun RenderMainScreen(
        voucherDetailsList: State<List<LabelAndValueCompose>?>,
        voucherCardList: State<List<UserVoucher>?>,
        paymentDetailsList: State<List<LabelAndValueCompose>?>,
        orderStatusLiveData: State<List<TimelineViewData>?>,
        statusBottomText: State<String?>,
        title: State<String?>,
        bonusGoldText: State<String?>,
        finalStatus: State<GoldRedemptionManualPaymentStatus?>,
        refundList: State<List<LabelAndValueCompose>?>,
        showMyOrdersButton: State<Boolean?>,
        showContinueShoppingButton: State<ContinueShoppingButtonPlacement?>
    ) {
        val drawableIcon = remember {
            derivedStateOf {
                getGoldRedemptionHeaderIconForStatus(
                    finalStatus.value ?: GoldRedemptionManualPaymentStatus.PENDING
                )
            }
        }
        val cardBg = colorResource(id = com.jar.app.core_ui.R.color.color_3c3357)
        val isExpanded = remember { mutableStateOf<Boolean>(true) }
        val defaultModifier: Modifier =
            Modifier
                .padding(
                    horizontal = 12.dp,
                )
                .background(cardBg)
        LazyColumn(
            Modifier
                .background(colorResource(id = com.jar.app.core_ui.R.color.color_272239))
                .fillMaxSize()
        ) {
            item {
                HeaderCircleIconComponent(
                    defaultModifier,
                    drawableIcon.value ?: R.drawable.feature_gold_redemption_hourglass,
                    topPadding = 20.dp
                ) {
                    Spacer(
                        Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                    )
                }
            }
            item {
                Text(
                    text = title.value.orEmpty(),
                    style = JarTypography.h6,
                    modifier = defaultModifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 10.dp),
                    color = Color.White,
                    fontSize = 24.sp,
                    textAlign = TextAlign.Center
                )
            }
            item {
                voucherCardList?.value?.takeIf { !it.isNullOrEmpty() }?.let {
                    VoucherSuccessComponents(
                        defaultModifier,
                        list = it,
                        modifier = defaultModifier,
                        showCardNoHideBtn = true,
                        alpha = (
                                if (finalStatus.value in setOf(
                                        GoldRedemptionManualPaymentStatus.PENDING,
                                        GoldRedemptionManualPaymentStatus.FAILURE,
                                        GoldRedemptionManualPaymentStatus.PROCESSING,
                                        GoldRedemptionManualPaymentStatus.FAILED,
                                    )
                                )
                                    0.5f else 1f
                                ),
                        viewRef = WeakReference(view)
                    )
                }
            }
            item {
                orderStatusLiveData.value?.takeIf { finalStatus.value != GoldRedemptionManualPaymentStatus.SUCCESS && !it.isNullOrEmpty() }
                    ?.let {
                        RenderTransactionStatus(
                            Modifier,
                            it,
                            cardBackgroundColor = colorResource(id = com.jar.app.core_ui.R.color.color_2E2942),
                            statusBottomText = statusBottomText.value,
                            retryButtonPressed = {
                                retryPaymentFlow()
                            },
                            refreshButtonPressed = {
                                uiScope.launch {
                                    showProgressBar()
                                }
                                viewModel.fetchOrderStatus(args.orderId, paymentManager.getCurrentPaymentGateway().name, WeakReference(context))
                            }
                        )
                    }
            }
            showMyOrdersButton?.value?.takeIf { it == true }?.let {
                item {
                    JarPrimaryButton(
                        modifier = defaultModifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp, horizontal = 20.dp),
                        text = stringResource(id = com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_go_to_my_orders),
                        onClick = {
                            navigateToMyOrders()
                            analyticsHandler.postEvent(
                                Redemption_VoucherPurchaseStatusScreenCTAClicked,
                                buildAnalyticsForPayment(GO_TO_MY_ORDERS)
                            )
                        },
                        isAllCaps = false,
                        fontSize = 16.sp
                    )
                }
            }
            showContinueShoppingButton?.value?.takeIf { it == ContinueShoppingButtonPlacement.UP }?.let {
                item {
                    JarSecondaryButton(
                        text = stringResource(id = com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_continue_shopping),
                        onClick = {
                            navigateToContinueShopping()
                            analyticsHandler.postEvent(
                                Redemption_VoucherPurchaseStatusScreenCTAClicked,
                                buildAnalyticsForPayment(GO_TO_CONTINUE_SHOPPING)
                            )
                        },
                        modifier = defaultModifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp, horizontal = 20.dp)
                    )
                }
            }

            item {
                bonusGoldText.value?.takeIf { it.isNotBlank() }?.let {
                    GreenBannerGold(text = it, modifier = defaultModifier.padding(top = 14.dp))
                }
            }
            item {
                RenderRowWithDashedLine(
                    defaultModifier
                        .background(cardBg)
                        .padding(top = 10.dp, bottom = 16.dp)
                )
            }
            item {
                Text(
                    text = stringResource(id = com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_voucher_details).uppercase(),
                    modifier = defaultModifier
                        .fillMaxWidth()
                        .padding(top = 20.dp, bottom = 16.dp),
                    style = JarTypography.h6,
                    color = colorResource(id = com.jar.app.core_ui.R.color.color_ACA1D3),
                    textAlign = TextAlign.Center
                )
            }
            item {
                RenderValueAdapterCard(defaultModifier, voucherDetailsList.value ?: listOf())
            }
            item {
                Text(
                    text = stringResource(id = com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_order_details).uppercase(),
                    modifier = defaultModifier
                        .fillMaxWidth()
                        .padding(top = 20.dp, bottom = 16.dp),
                    style = JarTypography.h6,
                    color = colorResource(id = com.jar.app.core_ui.R.color.color_ACA1D3),
                    textAlign = TextAlign.Center
                )
            }
            item {
                RenderValueAdapterCard(defaultModifier, paymentDetailsList.value ?: listOf())
            }
            refundList?.value?.takeIf { !it.isNullOrEmpty() }?.let {
                item {
                    Text(
                        text = stringResource(id = com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_refund_details).uppercase(),
                        modifier = defaultModifier
                            .fillMaxWidth()
                            .padding(top = 20.dp),
                        style = JarTypography.h6,
                        color = colorResource(id = com.jar.app.core_ui.R.color.color_ACA1D3),
                        textAlign = TextAlign.Center
                    )
                }
                item {
                    RenderRefundDetails(defaultModifier, it ?: listOf(), isExpanded)
                }
            }
            item {
                RenderBottomCard()
            }
            item {
                Spacer(
                    Modifier
                        .fillMaxWidth()
                        .height(30.dp)
                )
            }
            showContinueShoppingButton?.value?.takeIf { it == ContinueShoppingButtonPlacement.DOWN }?.let {
                item {
                    JarSecondaryButton(
                        text = stringResource(id = com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_continue_shopping),
                        onClick = {
                            navigateToContinueShopping()
                            analyticsHandler.postEvent(
                                Redemption_VoucherPurchaseStatusScreenCTAClicked,
                                buildAnalyticsForPayment(GO_TO_CONTINUE_SHOPPING)
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp, horizontal = 20.dp)
                    )
                }
            }
            if (finalStatus.value !in setOf(GoldRedemptionManualPaymentStatus.SUCCESS, GoldRedemptionManualPaymentStatus.COMPLETED)) {
                item {
                    RenderHelpSupportSection {
                        analyticsHandler.postEvent(
                            Redemption_ContactSupportClicked,
                        )
                        navigateToContactSupport()
                    }
                }
            }
        }
    }
    private fun navigateToContinueShopping() {
        navigateTo(
            CompletePaymentFragmentDirections.actionCompletePaymentToBrandCatalougeFragment("PAYMENT_FLOW"),
            popUpTo = R.id.completePayment,
            inclusive = true
        )
    }

    private fun retryPaymentFlow() {
        initOrderRequest?.let { viewModel.retryPaymentFlow(it) } ?: run {
            view?.let {
                uiScope.launch {
                    getString(com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_something_went_wrong).toast(it)
                }
            }
        }
    }

    private fun navigateToContactSupport() {
        val number = remoteConfigManager.getWhatsappNumber()
        requireContext().openWhatsapp(
            number,
            getString(
                com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_hey_i_m_having_trouble_in_gold_redemption_with_order_id,
                args.orderId
            )
        )
    }
    private fun buildAnalyticsForPayment(buttonPressed: String? = null): MutableMap<String, String> {
        val map = mutableMapOf<String, String>(
            CTA_BUTTON to buttonPressed.orEmpty(),
            SCREEN_STATUS to viewModel.finalStatus.value?.name.orEmpty(),
            VOUCHER_TITLE to viewModel.voucherCardList.value?.getOrNull(0)?.voucherName.orEmpty(),
            VOUCHER_TYPE to viewModel.voucherCardList.value?.getOrNull(0)?.currentState.orEmpty(),
            GOLD_BENEFIT_PERCENTAGE to viewModel.bonusGoldText.value.orEmpty(),
            MINIMUM_VOUCHER_AMOUNT to viewModel.voucherCardList.value?.getOrNull(0)?.amount.orZero()
                .toString(),
            VOUCHER_QUANTITY to viewModel.voucherCardList.value?.size.orZero().toString(),
            VOUCHER_AMOUNT to viewModel.voucherCardList.value?.getOrNull(0)?.amount.orZero()
                .times(viewModel.voucherCardList.value?.size.orZero()).toString(),
            GOLD_BENEFIT_AMOUNT to "",
            PAID_PLATFORM to paymentManager.getCurrentPaymentGateway().name,
            ORDER_ID to args.orderId,
            REDEMPTION_AVAILABILITY to "",
        )
        return map
    }

    private fun navigateToMyOrders() {
        navigateTo(
            CompletePaymentFragmentDirections.actionCompletePaymentToMyVouchersFragment(),
            popUpTo = R.id.introScreenFragment,
            inclusive = true
        )
    }

    @Inject
    lateinit var dispatcherProvider: DispatcherProvider

    @Inject
    lateinit var appScope: CoroutineScope

    private fun initiatePayment(initiatePaymentResponse: InitiatePaymentResponse) {
        appScope.launch(dispatcherProvider.main) {
            paymentManager.initiateOneTimePayment(initiatePaymentResponse)
                .collectUnwrapped(
                    onSuccessWithNullData = {
                    },
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        navigateToPaymentScreen(it, initiatePaymentResponse.orderId)
                    },
                    onError = { message, _ ->
                        dismissProgressBar()
                        view?.let {
                            uiScope.launch {
                                message.toast(it)
                            }
                        }
                    },
                )
        }
    }

    private fun navigateToPaymentScreen(
        it: FetchManualPaymentStatusResponse,
        orderId: String
    ) {
        val fetchResponse = encodeUrl(serializer.encodeToString(it))
        val placeOrderRequestString = args.initOrderRequest

        navigateTo("android-app://com.jar.app/voucherCompletePaymentFrag/${orderId}/${fetchResponse}/${placeOrderRequestString}", popUpTo = R.id.brandCatalougeFragment, inclusive = true)
    }

    override fun setup(savedInstanceState: Bundle?) {
        analyticsHandler.postEvent(Redemption_PaymentReceivedStatusScreenLaunched)
        registerBackPressDispatcher()
        setupUI()
        setupListeners()
        observeLiveData()
        getData()
    }

    var statusApiData: FetchManualPaymentStatusResponse? = null
    var initOrderRequest: GoldRedemptionInitiateCreateOrderRequest? = null

    private fun setupUI() {
        args.statusApiData?.let {
            statusApiData = serializer.decodeFromString(decodeUrl(it))
        }
        args.initOrderRequest?.let {
            initOrderRequest = serializer.decodeFromString(decodeUrl(it))
        }
    }

    private fun setupListeners() {
    }

    private fun observeLiveData() {
        val viewRef: WeakReference<View> = WeakReference(this.view)
        viewModel.placeOrderAPILiveData.observe(viewLifecycleOwner
        ) {
            initiatePayment(it)
        }

        viewModel.showLoading.observe(viewLifecycleOwner) {
            if (it) showProgressBar() else dismissProgressBar()
        }
        viewModel.finalStatus.observe(viewLifecycleOwner) { status ->
            if (status != null)
                analyticsHandler.postEvent(
                    GoldRedemptionAnalyticsKeys.Redemption_VoucherPurchaseStatusScreenShown,
                    buildAnalyticsForPayment().apply {
                        put(SCREEN_STATUS, getGoldRedemptionStatusForAnalytics(status).name)
                    })
        }
    }

    private fun getData() {
        val weakReference = WeakReference(context)
        statusApiData?.let {
            val completeStatusDone = viewModel.processStatusResponse(it, weakReference)
            if (!completeStatusDone) {
                fetchOrderStatus(args.orderId)
            }
        }
    }

    private fun registerBackPressDispatcher() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            backPressCallback
        )
    }

    private var backPressCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navigateTo(
                    CompletePaymentFragmentDirections.actionCompletePaymentToIntroScreenFragment("PAYMENT_FLOW"),
                    popUpTo = R.id.introScreenFragment,
                    inclusive = false
                )
            }
        }

    private fun fetchOrderStatus(orderId: String) {
        viewModel.startPolling(
            orderId,
            paymentManager.getCurrentPaymentGateway().name,
            WeakReference(context)
        )
    }
}
