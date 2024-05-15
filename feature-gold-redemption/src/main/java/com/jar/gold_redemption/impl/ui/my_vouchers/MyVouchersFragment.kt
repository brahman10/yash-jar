package com.jar.gold_redemption.impl.ui.my_vouchers

import android.os.Bundle
import android.view.View
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.core_base.util.orZero
import com.jar.app.core_compose_ui.views.RenderBaseToolBar
import com.jar.app.core_compose_ui.views.RenderHorizontalFilterList
import com.jar.app.core_ui.extension.toast
import com.jar.app.feature_gold_redemption.R
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.ACTIVE_VOUCHERS_COUNT
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.EXPIRED_VOUCHERS_COUNT
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.MINIMUM_VOUCHER_AMOUNT
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.MY_ORDERS_TAB
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.ORDER_ID
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.Redemption_MyOrdersScreenPmtHistoryClicked
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.Redemption_MyOrdersScreenShown
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.Redemption_MyOrdersTabClicked
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.Redemption_MyOrdersVoucherClicked
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.Redemption_MyOrdersVoucherPinCopied
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.SCREEN_STATUS
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.TRANSACTION_CARD_TYPE
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.VOUCHER_AMOUNT
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.VOUCHER_STATUS
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.VOUCHER_TITLE
import com.jar.app.core_compose_ui.base.BaseComposeFragment
import com.jar.app.core_compose_ui.component.JarPrimaryButton
import com.jar.gold_redemption.impl.ui.my_vouchers.bottom_sheet.RenderFAQsButton
import com.jar.gold_redemption.impl.ui.my_vouchers.bottom_sheet.RenderVoucherDetailItem
import com.jar.app.feature_gold_redemption.shared.data.network.model.UserVoucher
import com.jar.app.feature_gold_redemption.shared.data.network.model.request.FetchVoucherType
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
internal class MyVouchersFragment : BaseComposeFragment() {

    private val viewModel by hiltNavGraphViewModels<MyVouchersViewModel>(R.id.feature_redemption_navigation)
    private val args by navArgs<MyVouchersFragmentArgs>()
    private val SHEET_PEEK_HEIGHT = 100.dp

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    private fun navigateToVoucherStatus(it: String, enum: String) {
        navigateTo(MyVouchersFragmentDirections.actionMyVouchersFragmentToVoucherStatusFragment(it, enum, "MY_VOUCHERS_SCREEN"))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (args.tabType.equals(FetchVoucherType.ALL.name)) {
            viewModel.setActive(com.jar.app.feature_gold_redemption.shared.data.network.model.request.FetchVoucherType.ALL)
        } else {
            viewModel.setActive(com.jar.app.feature_gold_redemption.shared.data.network.model.request.FetchVoucherType.ACTIVE)
        }
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    override fun RenderScreen() {
        val voucherList = viewModel.voucherList.observeAsState(listOf())
        val paymentHistoryList = viewModel.paymentHistoryList.observeAsState(listOf())
        val selectedIndex = viewModel.selectedIndex.observeAsState()
        val selectedIndexType = remember { derivedStateOf { selectedIndex.value?.ordinal } }
        val horizontalFilterList = viewModel.horizontalFilterList
        val coroutineScope = rememberCoroutineScope()
        val bottomSheetState = rememberBottomSheetScaffoldState()


        BottomSheetScaffold(
            scaffoldState = bottomSheetState,
            sheetPeekHeight = if (paymentHistoryList.value.isNullOrEmpty()) 0.dp else SHEET_PEEK_HEIGHT,
            sheetShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            sheetBackgroundColor = colorResource(id = com.jar.app.core_ui.R.color.color_3C3357),
            sheetContent = {
                PurchaseHistoryBottomsheet(paymentHistoryList, {
                    coroutineScope.launch {
                        bottomSheetState.bottomSheetState.collapse()
                    }
                }) { obj ->
                    analyticsHandler.postEvent(
                        Redemption_MyOrdersScreenPmtHistoryClicked, mapOf(
                            TRANSACTION_CARD_TYPE to obj.bottomDrawerObjectType.orEmpty(),
                            SCREEN_STATUS to obj.txnStatus.orEmpty(),
                            VOUCHER_TITLE to obj.title.orEmpty(),
                            MINIMUM_VOUCHER_AMOUNT to obj.amount.orEmpty(),
                            VOUCHER_AMOUNT to obj.amount.orEmpty(),
                            ORDER_ID to obj.voucherId.orEmpty(),
                        )
                    )
                    navigateToVoucherStatus(obj.voucherId.orEmpty(), obj.bottomDrawerObjectType.orEmpty())
                }
            }) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colorResource(id = com.jar.app.core_ui.R.color.color_2E2942))
            ) {
                RenderBaseToolBar(
                    modifier = Modifier.background(colorResource(id = com.jar.app.core_ui.R.color.color_2e2942)),
                    onBackClick = { findNavController().navigateUp() },
                    title = stringResource(id = com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_my_orders),
                    RightSection = {
                        RenderFAQsButton() {
                            navigateTo(MyVouchersFragmentDirections.actionMyVouchersFragmentToFaqsScreen())
                        }
                    }
                )
                if (!horizontalFilterList.isNullOrEmpty()) {
                    RenderHorizontalFilterList(
                        list = horizontalFilterList, defaultIndex = FetchVoucherType.ACTIVE.ordinal, function = {
                            analyticsHandler.postEvent(
                                Redemption_MyOrdersTabClicked,
                                mapOf(
                                    ACTIVE_VOUCHERS_COUNT to viewModel.horizontalFilterList?.getOrNull(0).orEmpty(),
                                    EXPIRED_VOUCHERS_COUNT to viewModel.horizontalFilterList?.getOrNull(1).orEmpty(),
                                )
                            )
                            viewModel.setActive(FetchVoucherType.values()[it])
                            viewModel.fetchAllMyVouchers() // if active is not selected, then we don't need to pass any filter
                        },
                        bgColor = com.jar.app.core_ui.R.color.color_2e2942,
                        selectedIndexPass = selectedIndexType
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
                RenderMainContent(voucherList, !paymentHistoryList.value.isNullOrEmpty())
            }
            }
        }


    @Composable
    fun RenderMainContent(voucherList: State<List<UserVoucher?>>, addSpacingAtBottom: Boolean) {
        if (voucherList.value.isNullOrEmpty()) {
            RenderEmptyScreen()
        } else {
            RenderListScreen(voucherList, addSpacingAtBottom)
        }
    }

    @Composable
    fun RenderListScreen(voucherList: State<List<UserVoucher?>>, addSpacingAtBottom: Boolean) {
        val value = voucherList.value
        Column(modifier = Modifier
            .fillMaxWidth()
            .background(color = colorResource(id = com.jar.app.core_ui.R.color.color_272239))) {
            LazyColumn(modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = if (addSpacingAtBottom) SHEET_PEEK_HEIGHT else 0.dp, top = 16.dp)
            ) {
                items(value) {
                    RenderVoucherDetailItem(
                        modifier = Modifier.padding(PaddingValues(horizontal = 16.dp)),
                        it,
                        showCardNoHideBtn = false,
                        showCopyClipboardBtn = false,
                        isCardNoHidden = false,
                        copyClipboardAnalytics = {
                            analyticsHandler.postEvent(Redemption_MyOrdersVoucherPinCopied, buildAnalytics(it))
                        },
                        viewRef = WeakReference(view),
                    ) {
                        navigateToVoucherDetail(it)
                    }
                    Divider(
                        color = colorResource(id = com.jar.app.core_ui.R.color.color_3c3357),
                        thickness = 1.dp
                    )
                }
            }
        }
    }

    private fun buildAnalytics(it: UserVoucher?): Map<String, String> {
        return mapOf(
            ACTIVE_VOUCHERS_COUNT to viewModel.horizontalFilterList?.getOrNull(0).orEmpty(),
            EXPIRED_VOUCHERS_COUNT to viewModel.horizontalFilterList?.getOrNull(1).orEmpty(),
            VOUCHER_STATUS to it?.myVouchersType.orEmpty(),
            MY_ORDERS_TAB to viewModel.horizontalFilterList.getOrNull(viewModel.selectedIndex.value?.ordinal.orZero())
                .orEmpty()
        )
    }
    private fun navigateToVoucherDetail(it: UserVoucher?) {
       analyticsHandler.postEvent(Redemption_MyOrdersVoucherClicked, buildAnalytics(it))
        if (it?.voucherId.isNullOrEmpty() && it?.orderId.isNullOrEmpty()) {
            view?.let { it1 ->
                getString(com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_something_went_wrong).toast(
                    it1
                )
            }
            return
        }
        navigateTo(
            MyVouchersFragmentDirections.actionMyVouchersFragmentToVoucherDetail(
                it?.voucherId.orEmpty(), it?.orderId.orEmpty()
            )
        )
    }

    @OptIn(ExperimentalGlideComposeApi::class)
    @Preview
    @Composable
    fun RenderEmptyScreen() {
        Column(modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = com.jar.app.core_ui.R.color.color_272239)),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(painter = painterResource(id = com.jar.app.feature_gold_redemption.R.drawable.feature_gold_redemption_empty_voucher), contentDescription = "")
            Text(
                stringResource(id = com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_empty_voucher),
                modifier = Modifier.padding(top = 16.dp, bottom = 32.dp),
                color = Color.White,
                textAlign = TextAlign.Center
            )
            JarPrimaryButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 60.dp),
                text = stringResource(id = com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_buy_vouchers),
                onClick = {
                    navigateToBuyVouchers()
                }
            )
        }
    }

    private fun navigateToBuyVouchers() {
        navigateTo(MyVouchersFragmentDirections.actionMyVouchersFragmentToBrandCatalougeFragment("MY_VOUCHERS"))
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
        viewModel.showLoading.observe(viewLifecycleOwner) {
            if (it) showProgressBar() else dismissProgressBar()
        }
        viewModel.showToast.observe(viewLifecycleOwner) { string ->
            view?.takeIf { !string.isNullOrEmpty() }?.let {
                string.toast(it)
            }
        }
    }

    private fun getData() {
        viewModel.fetchAllMyVouchers()
        viewModel.fetchPurchaseHistory()
        viewModel.fetchActiveCount(WeakReference(context))
    }

    private fun setupUI() {
        analyticsHandler.postEvent(Redemption_MyOrdersScreenShown)
        setStatusBarColor(com.jar.app.core_ui.R.color.color_2e2942)
    }
}