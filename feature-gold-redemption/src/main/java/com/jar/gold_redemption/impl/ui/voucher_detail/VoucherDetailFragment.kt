package com.jar.gold_redemption.impl.ui.voucher_detail

import android.os.Bundle
import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.core_compose_ui.base.BaseComposeFragment
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.util.shareAsText
import com.jar.app.core_base.domain.model.User
import com.jar.app.core_base.util.orZero
import com.jar.app.core_compose_ui.theme.JarTypography
import com.jar.app.core_compose_ui.utils.rememberModalBottomSheetStateCustom
import com.jar.app.core_compose_ui.views.ExpandableCardModel
import com.jar.app.core_compose_ui.views.GradientSeperator
import com.jar.app.core_compose_ui.views.LabelAndValueCompose
import com.jar.app.core_compose_ui.views.LabelValueComposeView
import com.jar.app.core_compose_ui.views.renderExpandableFaqList
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_ui.extension.toast
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.GOLD_BENEFIT_AMOUNT
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.GOLD_BENEFIT_PERCENTAGE
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.MY_VOUCHER
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.MyVScreen
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.Redemption_ShareClicked
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.LAUNCH_SOURCE
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.SOURCE
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.VOUCHER_AMOUNT
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.VOUCHER_DETAIL
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.VOUCHER_PURCHASE
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.VOUCHER_TITLE
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.VOUCHER_TYPE
import com.jar.gold_redemption.impl.ui.common_ui.AboutJewellerContainer
import com.jar.gold_redemption.impl.ui.common_ui.GreenBannerGold
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys
import com.jar.app.feature_gold_redemption.shared.data.network.model.ViewVoucherDetailsAPIResponse
import com.jar.gold_redemption.impl.ui.my_vouchers.bottom_sheet.RenderVoucherDetailCard
import com.jar.app.feature_gold_redemption.shared.domain.model.CardStatus
import com.jar.app.feature_gold_redemption.shared.data.network.model.UserVoucher
import com.jar.gold_redemption.impl.ui.search_store.RenderMainSearchStoreBottomSheet
import com.jar.gold_redemption.impl.ui.search_store.SearchStoreViewModel
import com.jar.app.feature_gold_redemption.shared.data.network.model.RefundDetails
import com.jar.internal.library.jar_core_network.api.util.Serializer
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

@AndroidEntryPoint
internal class VoucherDetailFragment : BaseComposeFragment() {

    private val args by navArgs<VoucherDetailFragmentArgs>()
    private val viewModel by viewModels<VoucherDetailViewModel> { defaultViewModelProviderFactory }
    private val searchStoreViewModel by viewModels<SearchStoreViewModel> { defaultViewModelProviderFactory }

    @Inject
    lateinit var prefs: PrefsApi

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    @Inject
    lateinit var serializer: Serializer

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    @Preview(showSystemUi = true, showBackground = true)
    override fun RenderScreen() {
        val voucherData = viewModel.voucherData.observeAsState()
        val refundDetails = viewModel.refundDetails.observeAsState()
        val faqList = viewModel.faqListLiveData.observeAsState()
        val userVoucher = viewModel.userVoucher.observeAsState()
        val rememberModalBottomSheetState =
            rememberModalBottomSheetStateCustom(
                ModalBottomSheetValue.Hidden,
                skipHalfExpanded = true
            )
        val coroutineScope = rememberCoroutineScope()
        val isSheetOpened = remember { mutableStateOf<Boolean>(false) }
        ModalBottomSheetLayout(
            modifier = Modifier.navigationBarsPadding(),
            sheetContent = {
                RenderMainSearchStoreBottomSheet(searchStoreViewModel, isSheetOpened, { it, map ->
                }) {
                    coroutineScope.launch {
                        rememberModalBottomSheetState.hide()
                    }
                }
            },
            sheetShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            sheetState = rememberModalBottomSheetState
        ) {
            RenderMainScreen(
                voucherData,
                userVoucher,
                faqList,
                refundDetails
            ) {
                coroutineScope.launch {
                    rememberModalBottomSheetState.show()
                }
            }
        }
    }

    @Composable
    fun RenderMainScreen(
        voucherData: State<ViewVoucherDetailsAPIResponse?>,
        userVoucher: State<UserVoucher?>,
        faqList: State<List<ExpandableCardModel>?>,
        refundDetails: State<RefundDetails?>,
        openBottomSheet: () -> Unit,
    ) {
        val context = LocalContext.current
        val faqSelectedIndex = remember { mutableStateOf<Int>(2) }
        val bgColor = colorResource(id = com.jar.app.core_ui.R.color.color_272239)
        LaunchedEffect(key1 = voucherData.value, block = {
            if (voucherData.value?.getVoucherStatusEnum() == CardStatus.FAILED) {
                faqSelectedIndex.value = 0
            } else {
                faqSelectedIndex.value = 2
            }
        })
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colorResource(id = com.jar.app.core_ui.R.color.color_121127))
        ) {
            val shouldShowShareBtn =
                voucherData.value?.getVoucherStatusEnum() == CardStatus.ACTIVE && voucherData?.value?.refundDetails == null
            RenderToolBar("", showShowShareButton = shouldShowShareBtn, {
                // Share button
                val deeplink = "dl.myjar.app/goldCoinStore/${args.voucherId}/"
                val strings = constructShareText(
                    voucherData?.value?.code,
                    voucherData?.value?.pin,
                    voucherData?.value?.brandTitle
                )
                activity?.shareAsText(
                    context.getString(com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_share_this_gold_coin_2),
                    strings
                )
                analyticsHandler.postEvent(Redemption_ShareClicked, LAUNCH_SOURCE, MyVScreen)
            }) {
                analyticsHandler.postEvent(
                    GoldRedemptionAnalyticsKeys.Redemption_BackClicked,
                    GoldRedemptionAnalyticsKeys.BACK_BUTTON, "VOUCHER_DETAIL"
                )
                findNavController().navigateUp()
            }
            RenderTopBar(
                voucherData.value?.validTillText ?: voucherData.value?.expiredAtText,
                voucherData.value?.getVoucherStatusEnum() ?: CardStatus.ACTIVE,
                refundDetails = refundDetails.value
            ) {
                val x = if (args.voucherId.isEmpty()) args.orderId else args.voucherId
                navigateToSeeDetails(x, voucherData.value?.bottomDrawerObjectType)
            }
            LazyColumn(
                Modifier
                    .background(colorResource(id = com.jar.app.core_ui.R.color.color_2E2942))
                    .fillMaxSize()
            ) {
                item {
                    TitleWithImage(
                        voucherData.value?.imageUrl,
                        voucherData.value?.voucherName,
                    )
                }
                item {
                    GradientSeperator(modifier = Modifier.padding(top = 8.dp, bottom = 12.dp))
                }
                item {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .background(colorResource(id = com.jar.app.core_ui.R.color.color_2E2942))
                            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                    ) {
                        RenderVoucherDetailCard(
                            Modifier.padding(bottom = 8.dp),
                            voucher = userVoucher.value,
                            bgColor = colorResource(id = com.jar.app.core_ui.R.color.color_2E2942),
                            clickListener = {},
                            copyClipboardAnalytics = {
                                analyticsHandler.postEvent(GoldRedemptionAnalyticsKeys.Redemption_MyOrdersVoucherPinCopied)
                            },
                            showCardNoHideBtn = false,
                            showCopyClipboardBtn = (!userVoucher?.value?.code.isNullOrEmpty() && voucherData?.value?.getVoucherStatusEnum() == CardStatus.ACTIVE),
                            isCardNoHidden = false,
                            alpha = getAlphaForVoucherCard(voucherData?.value?.getVoucherStatusEnum()),
                            viewRef = WeakReference(view),
                            showQuantityLabel = false
                        )
                    }
                }
                item {
                    voucherData?.value?.pin?.let {
                        VoucherPinContainer(it)
                    }
                }
                voucherData?.value?.goldBonusText?.let {
                    item {
                        GradientSeperator(modifier = Modifier.padding(top = 16.dp, bottom = 10.dp))
                        GreenBannerGold(
                            text = it,
                            bgColor = Color.Transparent,
                            modifier = Modifier.padding(bottom = 12.dp),
                            fontSize = 16.sp
                        )
                    }
                }
                item {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                            .background(colorResource(id = com.jar.app.core_ui.R.color.color_121127))
                    ) {
// no-op
                    }
                }
                item {
                    AboutJewellerContainer(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        voucherData?.value?.offlineStoreListTextAns,
                        voucherData?.value?.onlineRedemptionTextAns,
                        voucherData?.value?.offlineStoreListText,
                        voucherData?.value?.onlineRedemptionText,
                        true,
                        stringResource(id = com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_redeem_your_voucher),
                        { it ->
                            analyticsHandler.postEvent(
                                it, mapOf<String, String>(
                                    SOURCE to MY_VOUCHER,
                                    VOUCHER_TITLE to viewModel.userVoucher?.value?.voucherName.orEmpty(),
                                    VOUCHER_TYPE to viewModel.userVoucher?.value?.currentState.orEmpty(),
                                    GOLD_BENEFIT_PERCENTAGE to viewModel.voucherData?.value?.goldBonusText.orEmpty(),
                                    VOUCHER_AMOUNT to viewModel.voucherData?.value?.amount.orZero()
                                        .toString(),
                                    GOLD_BENEFIT_AMOUNT to viewModel.voucherData?.value?.goldBonusText.orEmpty(),
                                )
                            )
                        }
                    ) {
                        openBottomSheet()
                    }
                }
                faqList.value?.takeIf { it.isNotEmpty() }?.let {
                    item {
                        Spacer(
                            modifier = Modifier
                                .height(8.dp)
                                .fillMaxWidth()
                                .background(
                                    colorResource(
                                        id = com.jar.app.core_ui.R.color.color_262238
                                    )
                                )
                        )
                    }
                    item {
                        Text(
                            text = stringResource(id = com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_more_info),
                            color = colorResource(id = com.jar.app.core_ui.R.color.white),
                            style = JarTypography.h6,
                            fontSize = 20.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    colorResource(id = com.jar.app.core_ui.R.color.color_272239)
                                )
                                .padding(top = 24.dp, start = 16.dp, bottom = 12.dp)
                        )
                    }
                    renderExpandableFaqList(
                        this,
                        it,
                        faqSelectedIndex,
                        com.jar.app.core_ui.R.color.color_272239,
                        com.jar.app.core_ui.R.color.color_272239,
                        customExpandableContent = {
                            (it as? List<LabelAndValueCompose>?)?.let {
                                RenderOrderDetailsCard(it)
                            }
                        },
                        elevation = 0.dp,
                        addSeperator = true,
                        paddedSeparator = true,
                        columnWrapper = Modifier.then(
                            if (refundDetails.value != null && it.size.orZero() == 1)
                                Modifier
                                    .heightIn(min = 200.dp)
                                    .background(bgColor) else Modifier
                        )
                    )
                }
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(colorResource(id = com.jar.app.core_ui.R.color.color_272239))
                    ) {
                    }
                }
            }
        }
    }

    private fun constructShareText(code: String?, pin: String?, brandTitle: String?): String {
        val userString = prefs.getUserStringSync()
        val name = serializer.decodeFromString<User?>(userString!!)?.firstName.orEmpty()
        return if (pin == null) {
            getString(
                com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_active_voucher_share_text,
                name,
                brandTitle.orEmpty(),
                brandTitle.orEmpty(),
                code.orEmpty()
            )
        } else {
            getString(
                com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_active_voucher_share_text_pin,
                name,
                brandTitle.orEmpty(),
                brandTitle.orEmpty(),
                code.orEmpty(),
                pin
            )
        }
    }

    private fun getAlphaForVoucherCard(voucherCardStatusEnum: CardStatus?): Float {
        return when (voucherCardStatusEnum) {
            CardStatus.PROCESSING -> 0.5f
            CardStatus.EXPIRED -> 0.5f
            CardStatus.ACTIVE -> 1f
            null -> 0.5f
            CardStatus.FAILED -> 0.5f
        }
    }

    private fun navigateToSeeDetails(voucherId: String?, bottomDrawerObjectType: String?) {
        navigateTo(
            VoucherDetailFragmentDirections.actionVoucherDetailToVoucherStatusFragment(
                voucherId,
                if (viewModel.refundDetails.value != null) "VOUCHER_REFUND" else VOUCHER_PURCHASE,
                VOUCHER_DETAIL
            )
        )
    }

    @OptIn(ExperimentalGlideComposeApi::class)
    @Composable
    fun RenderOrderDetailsCard(labelAndValueComposes: List<LabelAndValueCompose>) {
        Card(
            Modifier,
            shape = RoundedCornerShape(12.dp),
            backgroundColor = colorResource(id = com.jar.app.core_ui.R.color.color_2E2942)
        ) {
            LabelValueComposeView(
                Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                labelAndValueComposes
            )
        }
    }

    override fun setup(savedInstanceState: Bundle?) {
        observeLiveData()
        setupUI()
        setupListeners()
        getData()
    }

    private fun setupListeners() {

    }

    private fun observeLiveData() {
        val viewRef: WeakReference<View> = WeakReference(this.view)
        viewModel.showLoading.observe(viewLifecycleOwner) {
            if (it) showProgressBar() else dismissProgressBar()
        }
        viewModel.brandName.observe(viewLifecycleOwner) {
            searchStoreViewModel.setBrandName(it)
        }
        viewModel.showToast.observe(viewLifecycleOwner) { string ->
            view?.takeIf { !string.isNullOrEmpty() }?.let {
                string.toast(it)
            }
        }
    }

    private fun getData() {
        viewModel.fetchVoucherDetail(args.voucherId, args.orderId, WeakReference(context))
//        viewModel.fetchVoucherDetail2()
//        viewModel.fetchFaqs()


    }

    private fun setupUI() {
    }
}