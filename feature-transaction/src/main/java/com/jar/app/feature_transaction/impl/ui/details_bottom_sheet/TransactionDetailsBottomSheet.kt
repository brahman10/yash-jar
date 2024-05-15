package com.jar.app.feature_transaction.impl.ui.details_bottom_sheet

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jar.app.base.data.event.InitiatePaymentForFailedTransactionsEvent
import com.jar.app.base.data.event.OpenPaymentTransactionBreakupScreenEvent
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.app.base.util.*
import com.jar.app.core_analytics.EventKey
import com.jar.app.core_base.util.orZero
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.core_ui.BaseEdgeEffectFactory
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.item_decoration.SpaceItemDecoration
import com.jar.app.core_web_pdf_viewer.api.WebPdfViewerApi
import com.jar.app.feature_gifting.api.GiftingApi
import com.jar.app.feature_sell_gold.api.SellGoldApi
import com.jar.app.feature_transaction.R
import com.jar.app.feature_transaction.databinding.FeatureTransactionDetailBottomSheetBinding
import com.jar.app.feature_transaction.impl.ui.details.adapter.*
import com.jar.app.feature_transaction.impl.ui.details.adapter.gold_lease.GoldLeaseTxnAdapter
import com.jar.app.feature_transaction.impl.ui.details.adapter.savings.SavingsTxnAdapter
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
class TransactionDetailsBottomSheet :
    BaseBottomSheetDialogFragment<FeatureTransactionDetailBottomSheetBinding>() {

    @Inject
    lateinit var remoteConfigManager: RemoteConfigApi

    @Inject
    lateinit var prefs: PrefsApi

    @Inject
    lateinit var sellGoldApi: SellGoldApi

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    @Inject
    lateinit var giftingApi: GiftingApi

    @Inject
    lateinit var webPdfViewerApi: WebPdfViewerApi

    private val viewModel: TransactionDetailBottomSheetViewModel by viewModels()

    private val args: TransactionDetailsBottomSheetArgs by navArgs()

    private val couponCodeAdapter = CouponCodeAdapter()
    private val winningsUsedAdapter = WinningsUsedAdapter()
    private val weeklyChallengeAdapter = WeeklyChallengeAdapter()
    private val goldGiftingAdapter = GoldGiftingAdapter()
    private val goldLeaseTxnAdapter = GoldLeaseTxnAdapter()
    private val savingsTxnAdapter = SavingsTxnAdapter()
    private val pauseTxnDetailAdapter = PauseTxnDetailAdapter()
    private val productDetailsAdapter = ProductDetailsAdapter()

    private val contactUsAdapter = ContactUsAdapter {
        viewModel.commonData?.let {
            val message = getString(
                R.string.feature_transaction_transaction_support_message,
                it.title,
                it.orderId,
                it.amount?.amountToString(),
                it.date
            )
            val number = remoteConfigManager.getWhatsappNumber()
            requireContext().openWhatsapp(number, message)
            analyticsHandler.postEvent(EventKey.TransactionsV2.Clicked_ContactUs_TransactionDetailsScreen)
        }
    }

    private val roundOffAdapter = RoundOffAdapter {
        analyticsHandler.postEvent(EventKey.TransactionsV2.RoundOffCard_TransactionDetailsScreen)
        EventBus.getDefault().post(
            OpenPaymentTransactionBreakupScreenEvent(
                orderId = it.orderId,
                title = "",
                description = requireContext().resources.getQuantityString(
                    R.plurals.feature_transaction_round_off_transactions,
                    it.roundoffCount.orZero(),
                    it.roundoffCount.orZero(),
                )
            )
        )
    }

    private val txnDetailsCardAdapter = TxnDetailsCardAdapter({
        requireContext().copyToClipboard(it, getString(com.jar.app.core_ui.R.string.copied))
        analyticsHandler.postEvent(EventKey.TransactionsV2.Clicked_CopyTransactionId_TransactionDetailsScreen)
    }, {
        analyticsHandler.postEvent(
            EventKey.TransactionsV2.Clicked_TransactionChevron_TransactionDetailsScreen,
            mapOf(EventKey.FinalState to it)
        )
    })

    private val txnRoutineCardAdapter = TxnRoutineCardAdapter(
        onViewInvoiceClick = { url, title, showToolbar ->
            webPdfViewerApi.openPdf(url)
            analyticsHandler.postEvent(
                EventKey.TransactionsV2.Clicked_CTA_TransactionDetailsScreen,
                mapOf(EventKey.PROP_VALUE to url)
            )
        }, {
            uiScope.launch {
                viewModel.commonData?.orderId?.let {
                    sellGoldApi.openVpaSelectionFragment(
                        isRetryFlow = true,
                        withdrawalPrice = viewModel.commonData?.amount?.amountToString(),
                        orderId = viewModel.commonData?.orderId
                    )
                }
            }
        },
        onSendGiftReminderClick = {
            shareGiftImageMsg()
        }
    )

    private val txnStatusAdapter = TxnStatusAdapter()
    private val txnTrackingAdapter = TxnTrackingAdapter() { url ->
        analyticsHandler.postEvent(EventKey.TransactionsV2.Clicked_TrackingLinkCard_TransactionDetailsScreen)
        requireContext().startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }

    private var baseEdgeEffectFactory: BaseEdgeEffectFactory? = null
    private var layoutManager: LinearLayoutManager? = null
    private val spaceItemDecoration = SpaceItemDecoration(16.dp, 8.dp)
    private val screenAdapter by lazy {
        TxnDetailsScreenAdapter(
            listOf(
                contactUsAdapter,
                winningsUsedAdapter,
                couponCodeAdapter,
                goldGiftingAdapter,
                goldLeaseTxnAdapter,
                savingsTxnAdapter,
                pauseTxnDetailAdapter,
                productDetailsAdapter,
                roundOffAdapter,
                txnDetailsCardAdapter,
                txnRoutineCardAdapter,
                weeklyChallengeAdapter,
                txnStatusAdapter,
                txnTrackingAdapter
            )
        )
    }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureTransactionDetailBottomSheetBinding
        get() = FeatureTransactionDetailBottomSheetBinding::inflate


    override val bottomSheetConfig: BottomSheetConfig
        get() = BottomSheetConfig()

    override fun setup() {
        setupUI()
        setupListeners()
        observeLiveData()
        getData()
    }

    private fun getData() {
        viewModel.fetchTransactionDetails(args.id)
    }

    private fun setupUI() {
        layoutManager = LinearLayoutManager(requireContext())
        layoutManager?.isItemPrefetchEnabled = true
        layoutManager?.initialPrefetchItemCount = 10
        binding.rvTransactionDetails.layoutManager = layoutManager
        binding.rvTransactionDetails.addItemDecorationIfNoneAdded(spaceItemDecoration)
        baseEdgeEffectFactory = BaseEdgeEffectFactory()
        binding.rvTransactionDetails.edgeEffectFactory = baseEdgeEffectFactory!!
        binding.rvTransactionDetails.adapter = screenAdapter
        screenAdapter.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
    }

    private fun setupListeners() {
        binding.ivCross.setDebounceClickListener {
            dismiss()
        }

        binding.btnSaveNow.setDebounceClickListener {
            EventBus.getDefault().post(
                InitiatePaymentForFailedTransactionsEvent(
                    viewModel.commonData?.amount.orZero(),
                    listOf(args.id)
                )
            )
            dismiss()
        }
    }

    private fun observeLiveData() {
        viewModel.cardsLiveData.observe(viewLifecycleOwner) { t ->
            if (!t.isNullOrEmpty()) {
                binding.shimmerLayout.stopShimmer()
                binding.shimmerLayout.isVisible = false
                binding.rvTransactionDetails.isVisible = true
            }
            screenAdapter.items = t
            if (viewModel.fetchIsFailedTransaction())
                renderFailedStatusView()
            logAnalyticsData()
        }
    }

    private fun logAnalyticsData() {
        val map = mutableMapOf<String, Any>()
        if (viewModel.weeklyMagicShown) {
            map[EventKey.TransactionsV2.paramters.cardIncluded] =
                EventKey.TransactionsV2.values.WeeklyMagicCard
        }
        analyticsHandler.postEvent(EventKey.TransactionsV2.Shown_TransactionDetailsBottomSheet, map)
    }

    private fun renderFailedStatusView() {
        binding.clPaymentDetailsContainer.isVisible = viewModel.fetchIsFailedTransaction()
        viewModel.commonData?.amount?.let {
            binding.tvPayableAmount.text =
                getString(com.jar.app.core_ui.R.string.core_ui_rs_x_float, it)
        }
    }

    private fun shareGiftImageMsg() {
        giftingApi.shareGift(
            uiScope,
            WeakReference(requireContext()),
            false // Send Reminder is only visible for users who are not a jar user already
        )
    }


    override fun onDestroyView() {
        layoutManager = null
        baseEdgeEffectFactory = null
        super.onDestroyView()
    }

}