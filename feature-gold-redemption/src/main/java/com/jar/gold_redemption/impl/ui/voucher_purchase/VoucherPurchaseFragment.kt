package com.jar.gold_redemption.impl.ui.voucher_purchase

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.feature_gold_redemption.R
import com.jar.app.core_compose_ui.base.BaseComposeFragment
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.util.DispatcherProvider
import com.jar.app.base.util.encodeUrl
import com.jar.app.base.util.shareAsText
import com.jar.app.core_base.util.orZero
import com.jar.app.core_compose_ui.theme.JarTypography
import com.jar.app.core_compose_ui.utils.rememberModalBottomSheetStateCustom
import com.jar.app.core_compose_ui.views.GradientSeperator
import com.jar.app.core_compose_ui.views.renderExpandableFaqList
import com.jar.internal.library.jar_core_network.api.util.collectUnwrapped
import com.jar.app.core_ui.extension.toast
import com.jar.app.feature_payment.api.PaymentManager
import com.jar.app.feature_one_time_payments_common.shared.FetchManualPaymentStatusResponse
import com.jar.app.feature_one_time_payments.shared.data.model.base.InitiatePaymentResponse
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.CHANGE_FROM
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.CHANGE_TO
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.CHANGE_TYPE
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.DECREASE
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.GOLD_BENEFIT
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.GOLD_BENEFIT_AMOUNT
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.GOLD_BENEFIT_PERCENTAGE
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.INCREASE
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.MINIMUM_VOUCHER_AMOUNT
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.MOREINFO_TYPE
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.PAID_PLATFORM
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.Purchase_Screen
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.REDEMPTION_AVAILABILITY
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.Redemption_MaxLimitMsgShown
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.Redemption_MoreInfoClicked
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.Redemption_PurchaseScreenProceedClicked
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.Redemption_ShareClicked
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.Redemption_VAmountChangeClicked
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.Redemption_VPurchaseScreenLaunched
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.Redemption_VoucherQuantityChanged
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.LAUNCH_SOURCE
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.SOURCE
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.VOUCHER_AMOUNT
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.VOUCHER_PURCHASE
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.VOUCHER_QUANTITY
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.VOUCHER_TITLE
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.VOUCHER_TYPE
import com.jar.gold_redemption.impl.ui.common_ui.AboutJewellerContainer
import com.jar.gold_redemption.impl.ui.common_ui.GreenBannerGold
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys
import com.jar.app.feature_gold_redemption.shared.data.network.model.request.GoldRedemptionInitiateCreateOrderRequest
import com.jar.app.feature_one_time_payments.shared.domain.model.UpiApp
import com.jar.gold_redemption.impl.ui.my_vouchers.bottom_sheet.RenderVoucherDetailCard
import com.jar.gold_redemption.impl.ui.search_store.RenderMainSearchStoreBottomSheet
import com.jar.gold_redemption.impl.ui.search_store.SearchStoreViewModel
import com.jar.internal.library.jar_core_network.api.util.Serializer
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

@AndroidEntryPoint
internal class VoucherPurchaseFragment : BaseComposeFragment() {

    private val args by navArgs<VoucherPurchaseFragmentArgs>()
    private val viewModel by viewModels<VoucherPurchaseViewModel> { defaultViewModelProviderFactory }
    private val searchStoreViewModel by viewModels<SearchStoreViewModel> { defaultViewModelProviderFactory }

    @Inject
    lateinit var serializer: Serializer

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    @Preview
    override fun RenderScreen() {
        val configuration = LocalConfiguration.current
        val screenHeight = configuration.screenHeightDp.dp
        val context = LocalContext.current
        val selectedAmount = viewModel.totalAmount.observeAsState(-1f)
        val whichBottomSheet = viewModel.whichBottomSheet.observeAsState()
        val coroutineScope = rememberCoroutineScope()
        val rememberModalBottomSheetState =
            rememberModalBottomSheetStateCustom(
                ModalBottomSheetValue.Hidden,
                skipHalfExpanded = true
            )
        val isSearchBottomSheetOpen =
            remember { derivedStateOf { rememberModalBottomSheetState.isVisible && whichBottomSheet.value == WhichBottomSheet.STATE_DETAIL } }
        ModalBottomSheetLayout(
            modifier = Modifier.navigationBarsPadding(),
            sheetContent = {
                when (whichBottomSheet.value) {
                    null, WhichBottomSheet.AMOUNT -> {
                        RenderAmountBottomSheet(analyticsHandler, viewModel) {
                            coroutineScope.launch {
                                rememberModalBottomSheetState.hide()
                            }
                        }
                    }

                    WhichBottomSheet.STATE_DETAIL -> {
                        RenderMainSearchStoreBottomSheet(
                            searchStoreViewModel,
                            isSearchBottomSheetOpen,
                            { event, map ->
                                val it = viewModel.voucherPurchase.value
                                analyticsHandler.postEvent(
                                    event, mutableMapOf<String, String>(
                                        VOUCHER_TITLE to it?.title.orEmpty(),
                                        VOUCHER_TYPE to it?.type.orEmpty(),
                                        GOLD_BENEFIT_PERCENTAGE to it?.discountPercentage?.orZero()
                                            ?.toString().orEmpty(),
                                        MINIMUM_VOUCHER_AMOUNT to it?.amountList?.getOrNull(0)
                                            .orZero().toString(),
                                        GOLD_BENEFIT to (it?.amountList?.getOrNull(0).orZero()
                                            .times(it?.discountPercentage.orZero())).orZero()
                                            .toString(),
                                        REDEMPTION_AVAILABILITY to (computeAvailability(it)),
                                        VOUCHER_QUANTITY to viewModel.quantity.value.orZero()
                                            .toString(),
                                    ).apply {
                                        this.putAll(map)
                                    })
                            }) {
                            coroutineScope.launch {
                                rememberModalBottomSheetState.hide()
                            }
                        }
                    }
                }
            },
            sheetShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            sheetState = rememberModalBottomSheetState
        ) {
            Box(Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(colorResource(id = com.jar.app.core_ui.R.color.color_121127))
                ) {
                    RenderToolBar({
                        shareText(context, viewModel.brandName.value.orEmpty(), args.voucherId)
                        analyticsHandler.postEvent(
                            Redemption_ShareClicked,
                            LAUNCH_SOURCE,
                            Purchase_Screen
                        )
                    }) {
                        analyticsHandler.postEvent(
                            GoldRedemptionAnalyticsKeys.Redemption_BackClicked,
                            GoldRedemptionAnalyticsKeys.BACK_BUTTON, VOUCHER_PURCHASE
                        )
                        findNavController().navigateUp()
                    }
                    RenderList {
                        coroutineScope.launch {
                            rememberModalBottomSheetState.show()
                        }
                    }
                }
                RenderCartContainer(this, selectedAmount.value) { initiatePlaceOrderRequest() }
            }
        }
    }

    private fun shareText(context: Context, brandName: String, voucherId: String) {
        val deeplink = "dl.myjar.app/voucherPurchase/${voucherId}/"
        val stringResource =
            context.getString(
                com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_share_text,
                brandName,
                deeplink
            )
        activity?.shareAsText(
            getString(com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_share_this_gold_voucher),
            stringResource
        )
    }

    private fun initiatePlaceOrderRequest() {
//        navigateToPaymentScreen(
//            FetchManualPaymentStatusResponse(
//                null,
//                null,
//                null,
//                null,
//                null,
//                null,
//                null,
//                null,
//                null,
//                null,
//                null,
//                null,
//                null,
//                null,
//                null,
//                null,
//                null,
//                null,
//                null,
//                null,
//                null,
//                null,
//                null,
//                null
//            ), "6463231877ca727c270a826f", GoldRedemptionInitiateCreateOrderRequest(
//                1000.0f, "EGCGBFK001",1,"PAYTM"
//            )
//        )

        val it = viewModel.voucherPurchase.value
        analyticsHandler.postEvent(
            Redemption_PurchaseScreenProceedClicked, mapOf<String, String>(
                VOUCHER_TITLE to it?.title.orEmpty(),
                VOUCHER_TYPE to it?.type.orEmpty(),
                GOLD_BENEFIT_PERCENTAGE to it?.discountPercentage?.orZero()?.toString().orEmpty(),
                MINIMUM_VOUCHER_AMOUNT to it?.amountList?.getOrNull(0).orZero().toString(),
                GOLD_BENEFIT to (it?.amountList?.getOrNull(0).orZero()
                    .times(it?.discountPercentage.orZero())).orZero().toString(),
                REDEMPTION_AVAILABILITY to (computeAvailability(it)),
                VOUCHER_QUANTITY to viewModel.quantity.value.orZero().toString()
            )
        )
        val goldRedemptionInitiateCreateOrderRequest = GoldRedemptionInitiateCreateOrderRequest(
            viewModel.selectedAmount.value.orZero(),
            args.voucherId,
            viewModel.quantity.value.orZero(),
        )
        viewModel.initiatePlaceOrderRequest(goldRedemptionInitiateCreateOrderRequest) // todo
    }


    @Inject
    lateinit var paymentManager: PaymentManager

    @Inject
    lateinit var dispatcherProvider: DispatcherProvider

    @Inject
    lateinit var appScope: CoroutineScope

    private var voucherPaymentJob: Job? = null

    private fun initiatePayment(
        initiatePaymentResponse: InitiatePaymentResponse,
        second: GoldRedemptionInitiateCreateOrderRequest
    ) {
        voucherPaymentJob?.cancel()
        voucherPaymentJob = appScope.launch(dispatcherProvider.main) {
            paymentManager.initiateOneTimePayment(initiatePaymentResponse)
                .collectUnwrapped(
                    onSuccess = {
                        appScope.launch(dispatcherProvider.main) {
                            uiScope.launch {
                                dismissProgressBar()
                            }
                            viewModel.setLoading(false)
                            navigateToPaymentScreen(it, initiatePaymentResponse.orderId, second)
                        }
                    },
                    onError = { message, errorCode ->
                        viewModel.setLoading(false)
                        appScope.launch(dispatcherProvider.main) {
                            dismissProgressBar()
                            view?.let { message.toast(it) }
                        }
                    }
                )
        }

    }

    override fun onResume() {
        super.onResume()
        viewModel.goldGreenBannerString.value?.takeIf { !it.isNullOrBlank() }?.let {
            uiScope.launch {
                delay(100)
                viewModel.setGreenGold(it + " ")
            }
        }
    }

    private fun navigateToPaymentScreen(
        it: FetchManualPaymentStatusResponse,
        orderId: String,
        second: GoldRedemptionInitiateCreateOrderRequest
    ) {
        val fetchResponse =
            encodeUrl(serializer.encodeToString(it))
        val placeOrderRequestString =
            encodeUrl(serializer.encodeToString(second))

        navigateTo(
            "android-app://com.jar.app/voucherCompletePaymentFrag/${orderId}/${fetchResponse}/${placeOrderRequestString}",
            popUpTo = R.id.brandCatalougeFragment,
            inclusive = true
        )
    }

    @Composable
    fun RenderList(showBottomSheet: () -> Unit) {
        val voucherPurchase = viewModel.voucherPurchase.observeAsState()
        val voucherCardType = viewModel.voucherCardType.observeAsState()
        val selectedAmount = viewModel.selectedAmount.observeAsState()
        val faqSelectedIndex = remember { mutableStateOf<Int>(-1) }
        val faqList = viewModel.faqListLiveData.observeAsState()
        val isMinusEnabled = viewModel.isMinusEnabled.observeAsState(false)
        val isPlusEnabled = viewModel.isPlusEnabled.observeAsState(false)
        val quantity = viewModel.quantity.observeAsState(1)
        val errorText = viewModel.errorText.observeAsState(null)
        val goldGreenBannerString = viewModel.goldGreenBannerString.observeAsState()

        LaunchedEffect(key1 = errorText.value, block = {
            if (!errorText.value.isNullOrEmpty()) {
                val it = viewModel.voucherPurchase.value
                analyticsHandler.postEvent(
                    Redemption_MaxLimitMsgShown, mapOf<String, String>(
                        VOUCHER_TITLE to it?.title.orEmpty(),
                        VOUCHER_TYPE to it?.type.orEmpty(),
                        GOLD_BENEFIT_PERCENTAGE to it?.discountPercentage?.orZero()?.toString()
                            .orEmpty(),
                        MINIMUM_VOUCHER_AMOUNT to it?.amountList?.getOrNull(0).orZero().toString(),
                        GOLD_BENEFIT to (it?.amountList?.getOrNull(0).orZero()
                            .times(it?.discountPercentage.orZero())).orZero().toString(),
                        REDEMPTION_AVAILABILITY to (computeAvailability(it)),
                        VOUCHER_QUANTITY to quantity.value.orZero().toString(),
                        GOLD_BENEFIT_AMOUNT to (viewModel.totalAmount.value.orZero() * viewModel.discountPercentage / 100).orZero()
                            .toString()
                    )
                )
            }
        })
        LazyColumn(
            Modifier
                .background(colorResource(id = com.jar.app.core_ui.R.color.color_2E2942))
                .fillMaxSize(),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            item {
                VoucherHeader(
                    voucherPurchase.value?.voucherHeaderString.orEmpty(),
                    voucherPurchase.value?.imageUrl.orEmpty(),
                    voucherPurchase.value?.discountText.orEmpty()
                )
            }
            item {
                GradientSeperator(modifier = Modifier.padding(vertical = 24.dp))
            }
            item {
                AmountQuantityContainer(
                    selectedAmount,
                    isMinusEnabled.value,
                    isPlusEnabled.value,
                    quantity.value,
                    {
                        quantityChangePostEvent(DECREASE)
                        viewModel.onMinusClick(WeakReference(context))
                    },
                    {
                        quantityChangePostEvent(INCREASE)
                        viewModel.onAddClick(WeakReference(context))
                    },
                    hiddenClick = {
                        viewModel.setFromHidden()
                    }
                ) {
                    viewModel.setWhichBottomSheet(WhichBottomSheet.AMOUNT)
                    showBottomSheet()

                    val it = voucherPurchase.value
                    analyticsHandler.postEvent(
                        Redemption_VAmountChangeClicked, mapOf<String, String>(
                            VOUCHER_TITLE to it?.title.orEmpty(),
                            VOUCHER_TYPE to it?.type.orEmpty(),
                            GOLD_BENEFIT_PERCENTAGE to it?.discountPercentage?.orZero()?.toString()
                                .orEmpty(),
                            MINIMUM_VOUCHER_AMOUNT to it?.amountList?.getOrNull(0).orZero()
                                .toString(),
                            GOLD_BENEFIT to (it?.amountList?.getOrNull(0).orZero()
                                .times(it?.discountPercentage.orZero())).orZero().toString(),
                            REDEMPTION_AVAILABILITY to (computeAvailability(it)),
                            VOUCHER_QUANTITY to quantity.value.orZero().toString()
                        )
                    )
                }
            }
            errorText?.value?.takeIf { !it.isNullOrBlank() }?.let {
                item {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Text(
                            text = it,
                            color = colorResource(id = com.jar.app.core_ui.R.color.color_EB6A6E),
                            style = JarTypography.h6,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp, bottom = 8.dp)
                        )
                    }
                }
            }
            item {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    RenderVoucherDetailCard(
                        amount = selectedAmount.value.orZero(),
                        voucher = null,
                        bgColor = colorResource(id = com.jar.app.core_ui.R.color.color_2E2942),
                        showCardContainer = false,
                        quantity = quantity.value.toString(),
                        showCardNoHideBtn = false,
                        voucherCardType = voucherCardType.value,
                        viewRef = WeakReference(view),
                        showQuantityLabel = true
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    goldGreenBannerString.value?.let {
                        GreenBannerGold(
                            text = it,
                            shape = RoundedCornerShape(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
            item {
                ImageInfoContainer(voucherPurchase.value?.voucherStaticContentList)
            }
            item {
                AboutJewellerContainer(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    inStoreRedemptionText = voucherPurchase.value?.inStoreRedemptionText,
                    onlineRedemptionText = voucherPurchase.value?.onlineRedemptionText?.replace(
                        "\\",
                        ""
                    ),
                    showStatesDropdown = true,
                    analyticsFunction = { it ->
                        analyticsHandler.postEvent(
                            it, mapOf<String, String>(
                                SOURCE to VOUCHER_PURCHASE,
                                MINIMUM_VOUCHER_AMOUNT to "",
                                VOUCHER_QUANTITY to "",
                                PAID_PLATFORM to "",
                                REDEMPTION_AVAILABILITY to "",
                                VOUCHER_TITLE to viewModel.voucherPurchase?.value?.title.orEmpty(),
                                VOUCHER_TYPE to viewModel.voucherPurchase?.value?.type.orEmpty(),
                                GOLD_BENEFIT_PERCENTAGE to viewModel.voucherData?.value?.goldBonus.orEmpty(),
                                VOUCHER_AMOUNT to viewModel.selectedAmount.value.orZero()
                                    .toString(),
                            )
                        )
                    }
                ) {
                    viewModel.setWhichBottomSheet(WhichBottomSheet.STATE_DETAIL)
                    showBottomSheet()
                }
            }
            faqList.value?.let {
                item {
                    Text(
                        text = stringResource(id = com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_more_info),
                        color = colorResource(id = com.jar.app.core_ui.R.color.white),
                        style = JarTypography.h6,
                        fontSize = 20.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                colorResource(id = com.jar.app.core_ui.R.color.color_121127)
                            )
                            .padding(top = 24.dp, start = 16.dp, bottom = 12.dp)
                    )
                }
                renderExpandableFaqList(
                    this,
                    it,
                    faqSelectedIndex,
                    com.jar.app.core_ui.R.color.color_121127,
                    com.jar.app.core_ui.R.color.color_121127,
                    addSeperator = true,
                    paddedSeparator = true,
                    onClick = { index ->
                        val orNull = it.getOrNull(index)
                        analyticsHandler.postEvent(
                            Redemption_MoreInfoClicked, mapOf<String, String>(
                                VOUCHER_TITLE to viewModel.voucherPurchase?.value?.title.orEmpty(),
                                MOREINFO_TYPE to orNull?.faqHeaderText.orEmpty()
                            )
                        )
                    }
                )
                item {
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(30.dp)
                            .background(
                                colorResource(id = com.jar.app.core_ui.R.color.color_121127)
                            )
                    )
                }
            }
        }
    }

    private fun quantityChangePostEvent(s: String) {
        val voucherPurchase = viewModel.voucherPurchase
        val quantity = viewModel.quantity.value
        analyticsHandler.postEvent(
            Redemption_VoucherQuantityChanged, mapOf<String, String>(
                CHANGE_TYPE to s,
                CHANGE_FROM to (quantity.orZero()).toString(),
                CHANGE_TO to if (s == DECREASE) quantity?.minus(1).orZero()
                    .toString() else quantity?.plus(1).orZero().toString(),
                VOUCHER_AMOUNT to viewModel.selectedAmount.value.orZero().toString(),
                VOUCHER_TITLE to voucherPurchase.value?.title.orEmpty(),
                VOUCHER_TYPE to voucherPurchase.value?.type.orEmpty(),
                GOLD_BENEFIT_PERCENTAGE to voucherPurchase.value?.discountPercentage?.orZero()
                    ?.toString().orEmpty(),
                MINIMUM_VOUCHER_AMOUNT to voucherPurchase.value?.amountList?.getOrNull(0).orZero()
                    .toString(),
                GOLD_BENEFIT to (voucherPurchase.value?.amountList?.getOrNull(0).orZero()
                    .times(voucherPurchase.value?.discountPercentage.orZero())).orZero().toString(),
                REDEMPTION_AVAILABILITY to (computeAvailability(voucherPurchase.value)),
                VOUCHER_QUANTITY to quantity.orZero().toString(),
            )
        )
    }


    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
        setupListeners()
        observeLiveData()
        getData()
    }

    private fun setupListeners() {

    }

    private fun observeLiveData() {
        val viewRef: WeakReference<View> = WeakReference(this.view)
        viewModel.placeOrderAPILiveData.observe(viewLifecycleOwner) {
            initiatePayment(it.first, it.second)
        }
        viewModel.showLoading.observe(viewLifecycleOwner) {
            if (it) showProgressBar() else dismissProgressBar()
        }
        viewModel.showToast.observe(viewLifecycleOwner) {
            if (!it.isNullOrBlank()) {
                this.view?.let { it1 -> it.toast(it1) }
            }
        }
        viewModel.voucherPurchase.observe(viewLifecycleOwner) {
            it?.amountList?.getOrNull(0).orZero()
            analyticsHandler.postEvent(
                Redemption_VPurchaseScreenLaunched, mapOf<String, String>(
                    VOUCHER_TITLE to it?.title.orEmpty(),
                    VOUCHER_TYPE to it?.type.orEmpty(),
                    GOLD_BENEFIT_PERCENTAGE to it?.discountPercentage?.orZero()?.toString()
                        .orEmpty(),
                    MINIMUM_VOUCHER_AMOUNT to it?.amountList?.getOrNull(0).orZero().toString(),
                    GOLD_BENEFIT to (it?.amountList?.getOrNull(0).orZero()
                        .times(it?.discountPercentage.orZero())).orZero().toString(),
                    REDEMPTION_AVAILABILITY to (computeAvailability(it)),
                )
            )
        }
        viewModel.brandName.observe(viewLifecycleOwner) {
            searchStoreViewModel.setBrandName(it)
        }
        searchStoreViewModel.showLoading.observe(viewLifecycleOwner) {
            if (it) showProgressBar() else dismissProgressBar()
        }
    }

    private fun getData() {
        viewModel.fetchVoucherPurchase(args.voucherId, WeakReference(context))
//        viewModel.fetchVoucherPurchase2()
//        viewModel.fetchFaqs()

    }

    private fun setupUI() {
        setStatusBarColor(com.jar.app.core_ui.R.color.color_2e2942)
    }

    override fun onDestroy() {
        voucherPaymentJob?.cancel()
        super.onDestroy()
    }
}