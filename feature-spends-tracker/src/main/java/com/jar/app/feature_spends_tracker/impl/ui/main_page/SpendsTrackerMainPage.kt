package com.jar.app.feature_spends_tracker.impl.ui.main_page

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jar.app.base.data.event.GoToHomeEvent
import com.jar.app.base.data.event.HandleDeepLinkEvent
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.feature_spends_tracker.shared.MR
import com.jar.app.feature_spends_tracker.databinding.FragmentSpendsTrackerMainPageBinding
import com.jar.app.feature_spends_tracker.shared.domain.events.SpendsTrackerEvent
import com.jar.app.feature_spends_tracker.shared.domain.model.spendsDetailsData.SpendsData
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
internal class SpendsTrackerMainPage : BaseFragment<FragmentSpendsTrackerMainPageBinding>(),
    RvSummaryClickListener {

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    private var lastVisibleSpendPosition = -1

    companion object {
        private const val SpendsTrackerMainPage = "SpendsTrackerMainPage"
    }

    private val viewModel by viewModels<SpendsTrackerMainViewModel> {
        defaultViewModelProviderFactory
    }

    private var spendsTransactionsAdapter: SpendsTransactionsAdapter? = null
    private var spendsSummaryAdapter: SpendsSummaryAdapter? = null
    private var concatAdapter: ConcatAdapter? = null

    private var backPressCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            EventBus.getDefault().post(
                GoToHomeEvent(
                    SpendsTrackerMainPage, BaseConstants.HomeBottomNavigationScreen.HOME
                )
            )
        }
    }
    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentSpendsTrackerMainPageBinding
        get() = FragmentSpendsTrackerMainPageBinding::inflate

    override fun setupAppBar() {

    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
        observeLiveData()
        setupListeners()
        registerBackPressDispatcher()

    }

    private fun registerBackPressDispatcher() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner, backPressCallback
        )
    }

    private fun setupListeners() {
        binding.ivBack.setDebounceClickListener {
            analyticsHandler.postEvent(
                SpendsTrackerEvent.ST_BackbuttonClicked,
            )
            EventBus.getDefault().post(GoToHomeEvent("SPEND_TRACKER"))
        }

        binding.spendsTrackerSwipeToRefresh.setOnRefreshListener {
            viewModel.fetchSpendsData()
        }
    }

    private fun observeLiveData() {
        viewModel.spendsDataLiveData.observeNetworkResponse(viewLifecycleOwner,
            WeakReference(binding.root),
            onLoading = {
                binding.root.isVisible = false
                showProgressBar()
            },
            onSuccess = { spendsData ->
                binding.spendsTrackerSwipeToRefresh.isRefreshing = false
                binding.root.isVisible = true
                dismissProgressBar()
                setupSpendsTrackerUI(spendsData)
            },
            onError = {
                binding.spendsTrackerSwipeToRefresh.isRefreshing = false
                binding.root.isVisible = true
                dismissProgressBar()
            })

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.fetchSpendsTransactionData().collectLatest {
                    spendsTransactionsAdapter?.submitData(it)
                }
            }
        }

    }

    private fun setupSpendsTrackerUI(spendsData: SpendsData) {
        spendsSummaryAdapter?.submitList(listOf(spendsData))
        analyticsHandler.postEvent(
            SpendsTrackerEvent.ST_LandingpageShown, mapOf(
                SpendsTrackerEvent.Balance to spendsData.spendsTrackerResponseSummary.balanceText,
                SpendsTrackerEvent.Spends to spendsData.spendsTrackerResponseSummary.spendsText,
            ), shouldPushOncePerSession = true
        )
        binding.rvSpends.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (lastVisibleSpendPosition > 0) {
                        analyticsHandler.postEvent(
                            SpendsTrackerEvent.ST_SpendstransactionlistScrolled, mapOf(
                                SpendsTrackerEvent.transactions_viewed to lastVisibleSpendPosition + 1
                            )
                        )
                    }
                }
            }
        })
        binding.tvHeaderText.text = getCustomString(MR.strings.spent_tracker)
    }

    private fun setupUI() {
        setupTransactionsRecyclerView()
        viewModel.fetchSpendsData()
    }

    private fun setupTransactionsRecyclerView() {
        spendsTransactionsAdapter = SpendsTransactionsAdapter(
            onReportClicked = {
                analyticsHandler.postEvent(
                    SpendsTrackerEvent.ST_ReportClicked, mapOf(
                        SpendsTrackerEvent.Transaction_amount to it.amount,
                        SpendsTrackerEvent.Date to it.txnDate,
                    )
                )
                navigateTo(
                    SpendsTrackerMainPageDirections.actionSpendsTrackerMainPageToReportTransactionBottomSheet(
                        it
                    )
                )

            },
            onTransactionClicked = {
                analyticsHandler.postEvent(
                    SpendsTrackerEvent.ST_transactionlistclicked,
                )
            },
            onItemBind = { pos ->
                if (pos > lastVisibleSpendPosition)
                    lastVisibleSpendPosition = pos
            }
        )

        spendsSummaryAdapter = SpendsSummaryAdapter(this)
        concatAdapter = ConcatAdapter(spendsSummaryAdapter, spendsTransactionsAdapter)

        binding.rvSpends.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = concatAdapter
        }
    }

    override fun balanceViewClickListener() {
        analyticsHandler.postEvent(
            SpendsTrackerEvent.ST_balancetabclicked,
        )
    }

    override fun spendsViewClickListener() {
        analyticsHandler.postEvent(
            SpendsTrackerEvent.ST_spendstabclicked,
        )
    }

    override fun promptViewClickListener() {
        analyticsHandler.postEvent(
            SpendsTrackerEvent.ST_insightpromptclicked,
        )
    }

    override fun graphViewClickListener() {
        analyticsHandler.postEvent(
            SpendsTrackerEvent.ST_graphclicked,
        )
    }

    override fun btnSaveGoldClickListener(spendsData: SpendsData) {

        val deepLinkData = spendsData.spendsFlowRedirectionDetails.buttonLink.split("/")
        val isDsSetup = if (deepLinkData.isNotEmpty()) when (deepLinkData[1]) {
            BaseConstants.ExternalDeepLinks.BUY_GOLD -> "Y"
            BaseConstants.ExternalDeepLinks.SETUP_DAILY_INVESTMENT -> "N"
            else -> "N"
        } else "N"
        analyticsHandler.postEvent(
            SpendsTrackerEvent.ST_SaveingoldClicked, mapOf(
                SpendsTrackerEvent.Balance to spendsData.spendsTrackerResponseSummary.balanceText,
                SpendsTrackerEvent.Spends to spendsData.spendsTrackerResponseSummary.spendsText,
                SpendsTrackerEvent.DS_setup to isDsSetup,
            )
        )
        EventBus.getDefault()
            .post(HandleDeepLinkEvent(spendsData.spendsFlowRedirectionDetails.buttonLink))

    }


}