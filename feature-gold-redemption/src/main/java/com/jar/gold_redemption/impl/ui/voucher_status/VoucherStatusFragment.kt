package com.jar.gold_redemption.impl.ui.voucher_status

import android.os.Bundle
import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.feature_gold_redemption.R
import com.jar.app.core_compose_ui.base.BaseComposeFragment
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.util.openWhatsapp
import com.jar.app.core_compose_ui.theme.JarTypography
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.core_ui.extension.toast
import com.jar.app.feature_gold_redemption.shared.domain.model.getGoldRedemptionStatusForAnalytics
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.ORDER_ID
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.Redemption_ContactSupportClicked
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.Redemption_PaymentReceivedStatusScreenShown
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.Redemption_VoucherPurchaseStatusScreenLaunched
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.SCREEN_STATUS
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.SOURCE_SCREEN
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.VOUCHER_AMOUNT
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.VOUCHER_BONUS
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.VOUCHER_TITLE
import com.jar.gold_redemption.impl.ui.cart_complete_payment.RenderOrderDetails
import com.jar.gold_redemption.impl.ui.cart_complete_payment.RenderTransactionStatus
import com.jar.gold_redemption.impl.ui.voucher_success.RenderVoucherPurchaseSummaryBottomSheet
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

@AndroidEntryPoint
internal class VoucherStatusFragment : BaseComposeFragment() {

    private val args by navArgs<VoucherStatusFragmentArgs>()
    private val viewModel by viewModels<VoucherStatusViewModel> { defaultViewModelProviderFactory }
    private var pollingJob: Job? = null

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    @Inject
    lateinit var remoteConfigManager: RemoteConfigApi

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    @Preview
    override fun RenderScreen() {
        val transactionDetails = viewModel.transactionDetails.observeAsState()
        val addedRow = viewModel.addedRow.observeAsState()
        val timelineViewList = viewModel.timelineViewList.observeAsState()
        val statusBottomText = viewModel.statusBottomText.observeAsState()
        val refundDetails = viewModel.refundDetails.observeAsState()
        val orderDetails = viewModel.orderDetails.observeAsState()
        val showOrderId = viewModel.showOrderId.observeAsState()
        val headingText = viewModel.headingText.observeAsState()
        val isExpanded = remember { mutableStateOf<Boolean>(true) }
        val rememberModalBottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
        val coroutineScope = rememberCoroutineScope()
        val isForBonus = remember { derivedStateOf { args.enumType == VOUCHER_BONUS }}

        ModalBottomSheetLayout(
            sheetShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            sheetState = rememberModalBottomSheetState,
            sheetBackgroundColor = colorResource(id = com.jar.app.core_ui.R.color.color_3C3357),
            sheetContent = {
                RenderVoucherPurchaseSummaryBottomSheet(transactionDetails.value?.voucherList, rememberModalBottomSheetState.isVisible) {
                    coroutineScope.launch {
                        rememberModalBottomSheetState.hide()
                    }
                }
            }
        ) {
            Column(Modifier.fillMaxSize()) {
                RenderToolBar(
                    headingText,
                    transactionDetails?.value?.headerDateString ?: transactionDetails.value?.dateString.orEmpty(),
                    forBonus = isForBonus.value
                ) {
                    analyticsHandler.postEvent(
                        GoldRedemptionAnalyticsKeys.Redemption_BackClicked,
                        GoldRedemptionAnalyticsKeys.BACK_BUTTON, "VOUCHER_STATUS")
                    findNavController().navigateUp()
                }
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(colorResource(id = com.jar.app.core_ui.R.color.color_272239))
                ) {
                    item {
                        Divider(color = colorResource(id = R.color.color_484D5C14))
                        VoucherRow(transactionDetails.value, forBonus = isForBonus.value)
                    }
                    item {
                        transactionDetails.value?.paidForVouchersString?.let {
                            PaidForRow(it) {
                                coroutineScope.launch {
                                    rememberModalBottomSheetState.show()
                                }
                            }
                        }
                    }
                    addedRow?.value?.let {
                        item {
                            AddedForRow(
                                it
                            )
                        }
                    }
                    item {
                        timelineViewList.value?.let {
                            RenderTransactionStatus(
                                Modifier.padding(bottom = 12.dp),
                                it,
                                horizontalMargin = 0.dp,
                                backgroundColor = colorResource(id = com.jar.app.core_ui.R.color.color_272239),
                                cardBackgroundColor = colorResource(id = com.jar.app.core_ui.R.color.color_2E2942),
                                statusBottomText.value,
                                refreshButtonPressed = {
                                    uiScope.launch {
                                        showProgressBar()
                                    }
                                    viewModel.fetchTransactionDetails(
                                        args.orderId,
                                        args.enumType,
                                        WeakReference(context),
                                        { false }
                                    )
                                }
                            )
                        }
                    }

                    item {
                        orderDetails.value?.takeIf { it.isNotEmpty() }?.let {
                            RenderOrderDetails(title = com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_order_details, it, isExpanded)
                        }
                    }
                    item {
                        refundDetails.value?.takeIf { it.isNotEmpty() }?.let {
                            RenderOrderDetails(title = com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_refund_details, it, isExpanded)
                        }
                    }
                    item {
                        (transactionDetails.value?.paymentOrderId ?: transactionDetails.value?.paymentOrderDetails?.paymentOrderId)?.takeIf { showOrderId.value == true }?.let {
                            OrderIdRow(
                                it
                            )
                        }
                    }
                    item {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .background(colorResource(id = com.jar.app.core_ui.R.color.color_272239))
                                .padding(horizontal = 16.dp, vertical = 4.dp)
                        ) {
                            Spacer(Modifier.weight(1f))
                            Card(
                                backgroundColor = colorResource(id = com.jar.app.core_ui.R.color.color_3C3357),
                                elevation = 0.dp,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.padding(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .padding(10.dp)
                                        .clickable {
                                            analyticsHandler.postEvent(
                                                Redemption_ContactSupportClicked,
                                            )
                                            navigateToContactSupport()
                                        },
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        modifier = Modifier.padding(end = 10.dp),
                                        painter = painterResource(id = R.drawable.feature_gold_redemption_whatsapp_greyscale),
                                        tint = Color.Unspecified,
                                        contentDescription = ""
                                    )
                                    Text(
                                        text = stringResource(id = com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_contact_us),
                                        modifier = Modifier,
                                        style = JarTypography.body1,
                                        color = colorResource(id = com.jar.app.core_ui.R.color.color_EEEAFF),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }

                        }
                    }

                }
            }

        }
    }

    private fun navigateToContactSupport() {
        val number = remoteConfigManager.getWhatsappNumber()
        requireContext().openWhatsapp(
            number,
            getString(com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_hey_i_m_having_trouble_in_gold_redemption)
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
        viewModel.showLoading.observe(viewLifecycleOwner) {
            if (it) showProgressBar() else dismissProgressBar()
        }
        viewModel.showToast.observe(viewLifecycleOwner) { string ->
            view?.takeIf { !string.isNullOrEmpty() }?.let {
                string.toast(it)
            }
        }
        viewModel.finalStatus.observe(viewLifecycleOwner) { status ->
            if (status != null)
                analyticsHandler.postEvent(Redemption_PaymentReceivedStatusScreenShown, buildAnalyticsMap().apply {
                    put(SCREEN_STATUS, getGoldRedemptionStatusForAnalytics(status).name)
                })
        }
    }

    private fun getData() {
        viewModel.fetchTransactionDetails(args.orderId, args.enumType, WeakReference(context), { false })
        if (!args.isSuccessful) {
            viewModel.startPolling(args.orderId, args.enumType, WeakReference(context))
        }
    }

    private fun setupUI() {
        viewModel.voucherType = args.enumType
        analyticsHandler.postEvent(Redemption_VoucherPurchaseStatusScreenLaunched, buildAnalyticsMap())
    }

    private fun buildAnalyticsMap(): MutableMap<String, String> {
        val map = mutableMapOf<String, String>(
            SOURCE_SCREEN to args.sourceScreen.orEmpty(),
            SCREEN_STATUS to viewModel.curateLoadingStatus?.name.orEmpty(),
            VOUCHER_TITLE to viewModel.transactionDetails?.value?.brandName.orEmpty(),
            VOUCHER_AMOUNT to viewModel.transactionDetails?.value?.amount.orEmpty(),
            ORDER_ID to args.orderId.orEmpty(),
        )
        return map
    }
}