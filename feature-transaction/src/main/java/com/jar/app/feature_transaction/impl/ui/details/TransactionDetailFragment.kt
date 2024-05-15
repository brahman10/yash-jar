package com.jar.app.feature_transaction.impl.ui.details

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.*
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import com.jar.app.core_ui.BaseEdgeEffectFactory
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.item_decoration.SpaceItemDecoration
import com.jar.app.core_ui.pull_to_refresh_overlay.PullToRefreshListener
import com.jar.app.base.data.event.OpenPaymentTransactionBreakupScreenEvent
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.core_analytics.EventKey
import com.jar.app.core_base.util.orZero
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.core_web_pdf_viewer.api.WebPdfViewerApi
import com.jar.app.feature_gifting.api.GiftingApi
import com.jar.app.feature_sell_gold.api.SellGoldApi
import com.jar.app.feature_transaction.R
import com.jar.app.feature_transaction.databinding.FeatureTransactionFragmentTransactionDetailsBinding
import com.jar.app.feature_transaction.impl.ui.details.adapter.TxnStatusAdapter
import com.jar.app.feature_transaction.impl.ui.details.adapter.gold_lease.GoldLeaseTxnAdapter
import com.jar.app.feature_transaction.impl.ui.details.adapter.*
import com.jar.app.feature_transaction.impl.ui.details.adapter.savings.SavingsTxnAdapter
import com.jar.internal.library.jar_core_network.api.util.collect
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
class TransactionDetailFragment :
    BaseFragment<FeatureTransactionFragmentTransactionDetailsBinding>() {

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

    private val viewModelProvider by viewModels<TransactionDetailViewModelAndroid> { defaultViewModelProviderFactory }

    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }

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

    private val couponCodeAdapter = CouponCodeAdapter()
    private val winningsUsedAdapter = WinningsUsedAdapter()
    private val weeklyChallengeAdapter = WeeklyChallengeAdapter()
    private val goldGiftingAdapter = GoldGiftingAdapter()
    private val goldLeaseTxnAdapter = GoldLeaseTxnAdapter()
    private val savingsTxnAdapter = SavingsTxnAdapter()
    private val productDetailsAdapter = ProductDetailsAdapter()
    private val args by navArgs<TransactionDetailFragmentArgs>()
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
    private val txnTrackingAdapter = TxnTrackingAdapter { url ->
        analyticsHandler.postEvent(EventKey.TransactionsV2.Clicked_TrackingLinkCard_TransactionDetailsScreen)
        requireContext().startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }

    private val adapterDelegates = listOf(
        contactUsAdapter,
        winningsUsedAdapter,
        couponCodeAdapter,
        goldGiftingAdapter,
        goldLeaseTxnAdapter,
        savingsTxnAdapter,
        productDetailsAdapter,
        roundOffAdapter,
        txnDetailsCardAdapter,
        txnRoutineCardAdapter,
        weeklyChallengeAdapter,
        txnStatusAdapter,
        txnTrackingAdapter
    )
    private var baseEdgeEffectFactory: BaseEdgeEffectFactory? = null
    private var layoutManager: LinearLayoutManager? = null
    private val spaceItemDecoration = SpaceItemDecoration(16.dp, 8.dp)
    private var screenAdapter: TxnDetailsScreenAdapter? = null

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureTransactionFragmentTransactionDetailsBinding
        get() = FeatureTransactionFragmentTransactionDetailsBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
        setupListeners()
        observeLiveData()
        getData()
    }

    private fun setupUI() {
        if (prefs.shouldShowTransactionDetailOverLay()) {
            binding.pullToRefreshOverlay.isVisible = true
            binding.swipeRefresh.isEnabled = false
        }

        layoutManager = LinearLayoutManager(requireContext())
        layoutManager?.isItemPrefetchEnabled = true
        layoutManager?.initialPrefetchItemCount = 10
        binding.rvTransactionDetails.layoutManager = layoutManager
        binding.rvTransactionDetails.addItemDecorationIfNoneAdded(spaceItemDecoration)
        baseEdgeEffectFactory = BaseEdgeEffectFactory()
        binding.rvTransactionDetails.edgeEffectFactory = baseEdgeEffectFactory!!
        screenAdapter = TxnDetailsScreenAdapter(adapterDelegates)
        binding.rvTransactionDetails.adapter = screenAdapter
        screenAdapter?.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
    }

    private fun setupListeners() {
        binding.pullToRefreshOverlay.setPullListener(object : PullToRefreshListener {
            override fun onPulledToRefresh() {
                binding.swipeRefresh.isEnabled = true
                binding.pullToRefreshOverlay.isVisible = false
                prefs.setShowTransactionDetailOverLay(false)
                binding.swipeRefresh.isRefreshing = true
                getData()
            }

            override fun onClickedSomewhereElse() {
                binding.swipeRefresh.isEnabled = true
                binding.pullToRefreshOverlay.isVisible = false
                prefs.setShowTransactionDetailOverLay(false)
            }
        })

        binding.rvTransactionDetails.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                binding.swipeRefresh.isEnabled =
                    binding.pullToRefreshOverlay.isVisible.not() && recyclerView.canScrollVertically(
                        -1
                    ).not()
            }
        })

        binding.swipeRefresh.setOnRefreshListener {
            getData()
        }

        binding.ivBack.setDebounceClickListener {
            popBackStack()
        }
    }

    private fun observeLiveData() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.cardsLiveData.collectLatest {
                    if (it.isNotEmpty()) {
                        binding.shimmerLayout.stopShimmer()
                        binding.shimmerLayout.isVisible = false
                        binding.rvTransactionDetails.isVisible = true
                        binding.swipeRefresh.isRefreshing = false
                    }
                    screenAdapter?.items = it
                    logAnalyticsData()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.transactionDetailFlow.collect(
                    onSuccess = {
                        it.headers?.let {
                            setHeaderData(it)
                        }
                    },
                    onError = { errorMessage, _ ->
                        errorMessage.snackBar(binding.root)
                    }
                )
            }
        }
    }

    private fun logAnalyticsData() {
        val map = mutableMapOf<String, Any>()
        if (viewModel.weeklyMagicShown) {
            map[EventKey.TransactionsV2.paramters.cardIncluded] =
                EventKey.TransactionsV2.values.WeeklyMagicCard
        }
        analyticsHandler.postEvent(EventKey.TransactionsV2.Shown_TransactionDetailsScreen, map)
    }

    private fun getData() {
        val orderId = args.orderId
        val sourceType = args.sourceType
        val txnId = args.txnId

        viewModel.fetchTransactionDetails(
            orderId,
            sourceType,
            txnId
        )
    }

    private fun setHeaderData(header: com.jar.app.feature_transaction.shared.domain.model.TransactionHeaders) {
        binding.tvDate.text = header.date
        binding.tvStatus.text = header.currentStatus
    }

    private fun shareGiftImageMsg() {
        giftingApi.shareGift(
            uiScope,
            WeakReference(requireContext()),
            false // Send Reminder is only visible for users who are not a jar user already
        )
        analyticsHandler.postEvent(
            com.jar.app.feature_gifting.shared.util.EventKey.Clicked_Button_GiftGoldFlow,
            mapOf(com.jar.app.feature_gifting.shared.util.EventKey.buttonType to com.jar.app.feature_gifting.shared.util.EventKey.sendReminderTxnScreen)
        )
    }


    override fun onDestroyView() {
        screenAdapter = null
        layoutManager = null
        baseEdgeEffectFactory = null
        super.onDestroyView()
    }
}