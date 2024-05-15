package com.jar.app.feature.transaction_new.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.jar.app.R
import com.jar.app.base.data.event.NavItemReselectedEvent
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarHome
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_ui.pull_to_refresh_overlay.PullToRefreshListener
import com.jar.app.core_utils.data.NetworkFlow
import com.jar.app.databinding.FragmentNewTransactionBinding
import com.jar.app.feature_transaction.api.TransactionApi
import com.jar.app.feature_transaction.impl.domain.event.ShowTransactionOverLayEvent
import com.jar.app.feature_transaction.impl.ui.TransactionFragmentViewModelAndroid
import com.jar.app.feature_transaction.shared.ui.UiEvent
import com.jar.app.feature_transaction.shared.util.TransactionConstants
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

@AndroidEntryPoint
class NewTransactionFragment : BaseFragment<FragmentNewTransactionBinding>() {

    companion object {
        private const val EXTRA_TRANSACTION_TYPE = "EXTRA_TRANSACTION_TYPE"
        private const val EXTRA_FROM_SCREEN = "EXTRA_FROM_SCREEN"
        fun newInstance(
            transactionType: com.jar.app.feature_transaction.shared.domain.model.TransactionType? = null,
            fromScreen: String? = null
        ): NewTransactionFragment {
            val fragment = NewTransactionFragment()
            val bundle = Bundle()
            bundle.putParcelable(EXTRA_TRANSACTION_TYPE, transactionType)
            bundle.putString(EXTRA_FROM_SCREEN, fromScreen)
            fragment.arguments = bundle
            return fragment
        }
    }

    @Inject
    lateinit var transactionApi: TransactionApi

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    @Inject
    lateinit var prefs: PrefsApi

    @Inject
    lateinit var networkFlow: NetworkFlow

    private var adapter: NewTransactionAdapter? = null

    private val transactionType by lazy {
        requireArguments().getParcelable<com.jar.app.feature_transaction.shared.domain.model.TransactionType>(
            EXTRA_TRANSACTION_TYPE
        )
    }

    private val fromScreen by lazy {
        requireArguments().getString(EXTRA_FROM_SCREEN)
    }

    private var handler: Handler? = null

    private val viewModelProvider by activityViewModels<TransactionFragmentViewModelAndroid> { defaultViewModelProviderFactory }

    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }

    //Don't redirect after coming back to this screen from TransactionDetails
    private var isRedirectionHandled = false

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentNewTransactionBinding
        get() = FragmentNewTransactionBinding::inflate

    override fun setupAppBar() {

    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
        setupListeners()
        observeData()
        handleRedirection()
    }

    private fun setupUI() {
        adapter =
            NewTransactionAdapter(
                childFragmentManager,
                viewLifecycleOwner.lifecycle,
                transactionApi
            )
        binding.transactionViewPager.adapter = adapter
        TabLayoutMediator(binding.tabLayout, binding.transactionViewPager) { tab, position ->
            tab.setCustomView(com.jar.app.feature_transaction.R.layout.feature_transaction_layout_custom_tab)
            tab.customView?.findViewById<AppCompatTextView>(com.jar.app.feature_transaction.R.id.tvTabText)?.text = when (position) {
                0 -> getString(R.string.gold)
                1 -> getString(R.string.winnings)
                else -> throw Exception("Invalid tab position in Transactions")
            }
        }.attach()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun showTransactionOverlay(data: ShowTransactionOverLayEvent) {
        if (prefs.shouldShowTransactionOverLay()) {
            binding.pullToRefreshOverlay.isVisible = true
            binding.pullToRefreshOverlay.setPullListener(object : PullToRefreshListener {
                override fun onPulledToRefresh() {
                    binding.pullToRefreshOverlay.isVisible = false
                    prefs.setShowTransactionOverLay(false)
                    viewModel.sendPullToRefreshEvent()
                }

                override fun onClickedSomewhereElse() {
                    binding.pullToRefreshOverlay.isVisible = false
                    prefs.setShowTransactionOverLay(false)
                }
            })
        }
    }

    override fun onDestroyView() {
        adapter = null
        super.onDestroyView()
    }

    override fun onResume() {
        super.onResume()
        EventBus.getDefault()
            .post(UpdateAppBarEvent(AppBarData(ToolbarHome(showStoryIcon = false, showNotification = true))))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handler = Handler(Looper.getMainLooper())
        EventBus.getDefault().register(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler?.removeCallbacksAndMessages(null)
        handler = null
        EventBus.getDefault().unregister(this)
    }

    private fun setupListeners() {
        binding.transactionViewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (isResumed) {
                    if (position == 0)
                        analyticsHandler.postEvent(TransactionConstants.AnalyticsKeys.Clicked_GoldTab_WinningsScreen)
                    else
                        analyticsHandler.postEvent(TransactionConstants.AnalyticsKeys.Clicked_WinningsTab_GoldTransactionScreen)
                }
            }
        })
    }

    private fun observeData() {
        //Intentionally done in this way, to avoid memory leak.

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.uiAppBarEventLiveData.collectLatest { event ->
                    when (event) {
                        is UiEvent.AppBarBackgroundColor -> {
                            binding.tabLayout.setBackgroundColor(event.color)
                        }

                        else -> {   /*Ignore*/
                        }
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.uiSetWinningsTabAlert.collectLatest { event ->
                    if (event is UiEvent.WinningTabAlert) {
                        binding.tabLayout.getTabAt(1)
                            ?.customView
                            ?.findViewById<AppCompatImageView>(com.jar.app.feature_transaction.R.id.ivTabIcon)
                            ?.isVisible = event.shouldShow
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                networkFlow.networkStatus.collectLatest {
                    binding.networkView.toggleNetworkLayout(it, uiScope)
                }
            }
        }
    }

    private fun handleRedirection() {
        if (isRedirectionHandled.not()) {
            val position = when (transactionType) {
                com.jar.app.feature_transaction.shared.domain.model.TransactionType.INVESTMENTS,
                com.jar.app.feature_transaction.shared.domain.model.TransactionType.GOLD_GIFT,
                com.jar.app.feature_transaction.shared.domain.model.TransactionType.WITHDRAWALS,
                com.jar.app.feature_transaction.shared.domain.model.TransactionType.NONE, com.jar.app.feature_transaction.shared.domain.model.TransactionType.GOLD -> BaseConstants.TransactionAdapterPosition.GOLD

                com.jar.app.feature_transaction.shared.domain.model.TransactionType.WINNINGS,
                com.jar.app.feature_transaction.shared.domain.model.TransactionType.PARTNERSHIPS -> BaseConstants.TransactionAdapterPosition.WINNING

                else -> BaseConstants.TransactionAdapterPosition.GOLD
            }

            binding.transactionViewPager.postDelayed({
                if (lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED))
                    binding.transactionViewPager.setCurrentItem(position, true)
            }, 200)
            isRedirectionHandled = true
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onTransactionFragmentPositionChangedEvent(transactionFragmentPositionChangedEvent: com.jar.app.feature_transaction.impl.domain.event.TransactionFragmentPositionChangedEvent) {
        if (lifecycle.currentState.isAtLeast(Lifecycle.State.CREATED))
            binding.transactionViewPager.setCurrentItem(
                transactionFragmentPositionChangedEvent.position,
                true

            )
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onNavItemReselectedEvent(navItemReselectedEvent: NavItemReselectedEvent) {
        if (navItemReselectedEvent.itemId == R.id.transactionFragment &&
            lifecycle.currentState.isAtLeast(Lifecycle.State.CREATED)
        ) {
            adapter?.scrollToTop(binding.transactionViewPager.currentItem)
        }
    }
}