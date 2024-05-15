package com.jar.app.feature_transaction.impl.ui.gold

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.appbar.AppBarLayout
import com.jar.app.base.data.event.HandleDeepLinkEvent
import com.jar.app.base.data.event.OpenGoldLeaseFlow
import com.jar.app.base.data.event.OpenUserGoldBreakdownScreenEvent
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.*
import com.jar.app.core_analytics.EventKey
import com.jar.app.core_base.domain.model.GoldBalance
import com.jar.app.core_base.domain.model.GoldBalanceViewType
import com.jar.app.core_base.domain.model.IconBackgroundTextComponent
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.orZero
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.core_ui.extension.getHtmlTextValue
import com.jar.app.core_ui.extension.runLayoutAnimation
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.core_ui.item_decoration.SpaceItemDecoration
import com.jar.app.core_ui.view_holder.LoadStateAdapter
import com.jar.app.feature_transaction.R
import com.jar.app.feature_transaction.databinding.FeatureTransactionFragmentNewGoldBinding
import com.jar.app.feature_transaction.impl.domain.event.ShowTransactionOverLayEvent
import com.jar.app.feature_transaction.impl.ui.TransactionFragmentViewModelAndroid
import com.jar.app.feature_transaction.impl.ui.filter.adapter.SelectedOptionAdapter
import com.jar.app.feature_transaction.impl.ui.gold.adapter.TransactionListAdapter
import com.jar.app.feature_transaction.shared.ui.UiEvent
import com.jar.app.feature_transaction.shared.util.TransactionConstants
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import kotlin.math.abs

@AndroidEntryPoint
class NewGoldFragment : BaseFragment<FeatureTransactionFragmentNewGoldBinding>() {

    companion object {
        private const val MG = "mg"
        fun newInstance() = NewGoldFragment()
    }

    @Inject
    lateinit var remoteConfigApi: RemoteConfigApi

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    @Inject
    lateinit var prefsApi: PrefsApi

    private val transactionViewModelProvider by activityViewModels<TransactionFragmentViewModelAndroid> { defaultViewModelProviderFactory }

    private val transactionViewModel by lazy {
        transactionViewModelProvider.getInstance()
    }

    private val viewModelProvider by viewModels<GoldTransactionViewModelAndroid> { defaultViewModelProviderFactory }

    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }

    private var glide: RequestManager? = null
    private var target = object : CustomTarget<Drawable>() {
        override fun onResourceReady(
            resource: Drawable,
            transition: Transition<in Drawable>?
        ) {
            uiScope.launch {
                binding.tvPrimaryCta.setCompoundDrawablesWithIntrinsicBounds(
                    resource,
                    null,
                    null,
                    null
                )
            }
        }

        override fun onLoadCleared(placeholder: Drawable?) {
        }

    }
    private var mAppBarLastOffset = 0

    private val offsetChangedListener =
        AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            binding.swipeRefresh.isEnabled = verticalOffset == 0
            mAppBarLastOffset = verticalOffset
            val proportion =
                (abs(verticalOffset.toFloat()) / appBarLayout.totalScrollRange.toFloat())
            updateToolbar(proportion)
        }

    private var spaceItemDecoration = SpaceItemDecoration(16.dp, 8.dp)
    private var spaceItemDecorationFilter = SpaceItemDecoration(4.dp, 4.dp)
    private var selectedAdapter: SelectedOptionAdapter? = null
    private var transactionListAdapter: TransactionListAdapter? = null

    private val hasAnimatedOnce = AtomicBoolean(false)

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureTransactionFragmentNewGoldBinding
        get() = FeatureTransactionFragmentNewGoldBinding::inflate

    override fun setupAppBar() {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getData()
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
        setupListeners()
        observeLiveData()
    }

    override fun onResume() {
        super.onResume()
        binding.appBarLayout.addOnOffsetChangedListener(offsetChangedListener)
        val proportion =
            (abs(mAppBarLastOffset.toFloat()) / binding.appBarLayout.totalScrollRange.toFloat())
        updateToolbar(proportion)
    }

    override fun onPause() {
        super.onPause()
        binding.appBarLayout.removeOnOffsetChangedListener(offsetChangedListener)
    }

    private fun setupUI() {
        (binding.clDetails as ViewGroup).layoutTransition.setAnimateParentHierarchy(false)

        transactionListAdapter = TransactionListAdapter {
            analyticsHandler.postEvent(
                TransactionConstants.AnalyticsKeys.Clicked_TransactionCard_GoldTransactionScreen,
                mapOf(
                    EventKey.TransactionType to it.sourceType!!,
                    EventKey.TransactionId to it.orderId!!,
                    EventKey.TransactionStatus to (it.statusEnum ?: "")
                )
            )
            /**
             * If transaction list details type is V5 use V5 API for Transaction Details and
             * open New Transaction Details Screen.
             * This check may be removed once the new transaction details screen is finalized
             * for other flows, Only to be used in Gold Lease V2 for now
             * **/
            navigateTo("${it.getTransactionListDetailsType().transactionScreenDeepLink}/${it.orderId}/${it.assetTransactionId}/${it.sourceType}")
        }
        binding.rvTransactions.addItemDecorationIfNoneAdded(spaceItemDecoration)
        binding.rvTransactions.adapter = transactionListAdapter?.withLoadStateFooter(
            footer = LoadStateAdapter {
                transactionListAdapter?.retry()
            }
        )

        selectedAdapter = SelectedOptionAdapter {
            analyticsHandler.postEvent(
                TransactionConstants.AnalyticsKeys.Clicked_RemoveFilterValue_FilterResultScreen,
                mapOf(
                    EventKey.PROP_VALUE to it.displayName
                )
            )
            transactionViewModel.removeFilterSelection(it)
        }
        binding.rvSelectedFilters.addItemDecorationIfNoneAdded(spaceItemDecorationFilter)
        binding.rvSelectedFilters.adapter = selectedAdapter
    }

    private fun setupListeners() {

        binding.llDropDownArrow.setOnClickListener {
            if (binding.txnGroup.isVisible) {
                binding.ivExpand.rotateClockWise()
            } else {
                binding.ivExpand.rotateAntiClockWise()
            }
            binding.txnGroup.isVisible = !binding.txnGroup.isVisible
            if (viewModel.userGoldLiveData.value?.data?.data?.showCurrentValue == false) {
                binding.tvCurrentValue.isVisible = false
                binding.tvCurrentValueTitle.isVisible = false
            }
            if (viewModel.userGoldLiveData.value?.data?.data?.showInvestedValue == false) {
                binding.tvInvestedValue.isVisible = false
                binding.tvInvestedValueTitle.isVisible = false
            }
            analyticsHandler.postEvent(
                TransactionConstants.AnalyticsKeys.Clicked_Locker_GoldTransactionScreen,
                EventKey.TransactionsV2.Chevron
            )
        }

        binding.swipeRefresh.setOnRefreshListener {
            getData()
        }

        binding.btnContactUs.setDebounceClickListener {
            analyticsHandler.postEvent(TransactionConstants.AnalyticsKeys.Clicked_Help_GoldTransactionScreen)
            requireContext().openWhatsapp(
                remoteConfigApi.getWhatsappNumber(),
                getString(R.string.feature_transaction_hey_need_help_with_transactions)
            )
        }

        binding.btnFilter.setDebounceClickListener {
            analyticsHandler.postEvent(
                if (binding.btnFilter.text.toString()
                        .equals(getString(R.string.feature_transaction_filter), true)
                )
                    TransactionConstants.AnalyticsKeys.Clicked_Filter_GoldTransactionScreen
                else
                    TransactionConstants.AnalyticsKeys.Clicked_FilterApplied_FilterResultScreen
            )
            navigateTo("android-app://com.jar.app/transactionFilterFragment")
        }

        binding.tvInvestedValue.setDebounceClickListener {
            analyticsHandler.postEvent(
                TransactionConstants.AnalyticsKeys.Clicked_InvestedValue_GoldTransactionScreen,
                mapOf(EventKey.AMOUNT to binding.tvInvestedValue.text)
            )
        }

        binding.rvTransactions.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                binding.swipeRefresh.isEnabled = recyclerView.canScrollVertically(-1).not()
            }
        })

        viewLifecycleOwner.lifecycleScope.launch {
            transactionListAdapter?.loadStateFlow?.collectLatest { loadState ->
                val isListEmpty =
                    loadState.refresh is LoadState.NotLoading && transactionListAdapter?.itemCount == 0

                if (isListEmpty)
                    setEmptyLayout()
                else {
                    binding.emptyLayout.root.isVisible = false
                    binding.rvTransactions.isVisible = true
                }
                when (loadState.refresh is LoadState.Loading) {
                    true -> {
                        binding.rvTransactions.isVisible = false
                        binding.shimmerPlaceholder.startShimmer()
                        binding.shimmerPlaceholder.isVisible = true
                    }

                    false -> {
                        if (isFirstApiCall && transactionListAdapter?.itemCount.orZero() > 2) {
                            EventBus.getDefault().post(ShowTransactionOverLayEvent())
                        }
                        binding.swipeRefresh.isRefreshing = false
                        binding.shimmerPlaceholder.stopShimmer()
                        binding.shimmerPlaceholder.isVisible = false

                        if (hasAnimatedOnce.getAndSet(true).not()) {
                            binding.rvTransactions.runLayoutAnimation(R.anim.feature_transaction_layout_animation_fall_down)
                        }
                    }
                }
            }
        }

        binding.goldLeaseTopBar.setDebounceClickListener {
            analyticsHandler.postEvent(EventKey.TransactionsV2.Clicked_Locker_ManageLease)
            EventBus.getDefault()
                .post(
                    OpenGoldLeaseFlow(
                        BaseConstants.GoldLeaseFlowType.NEW_GOLD_FRAGMENT,
                        BaseConstants.GoldLeaseTabPosition.TAB_MY_ORDERS
                    )
                )
        }
    }

    private fun observeLiveData() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                transactionViewModel.uiPullRefreshEventLiveData.collectLatest {
                    object : Observer<UiEvent?> {
                        override fun onChanged(event: UiEvent?) {
                            event ?: return
                            when (event) {
                                is UiEvent.PulledToRefresh -> {
                                    binding.swipeRefresh.isRefreshing = true
                                }

                                else -> {   /*Ignore*/
                                }
                            }
                        }
                    }
                }
            }
        }


        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.userGoldLiveData.collect(
                    onSuccess = {
                        it?.let {
                            setGoldData(it)
                            setupCTAData(it.cta)
                            it.jarWinningsFooter?.let { jarWinningsFooter ->
                                postShownEvent()
                                setJarWinningsFooterData(jarWinningsFooter)
                            } ?: kotlin.run {
                                postShownEvent()
                            }
                        }
                    },
                    onError = { errorMessage, _ ->
                        postShownEvent()
                        errorMessage.snackBar(binding.root)
                    }
                )
            }
        }


        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                transactionViewModel.selectedFiltersLiveData.collectLatest {
                    binding.btnFilter.text =
                        if (it.isNullOrEmpty()) getText(R.string.feature_transaction_filter) else resources.getQuantityString(
                            R.plurals.feature_transaction_x_filters_applied,
                            it.size,
                            it.size,
                        )
                    if (it.isNullOrEmpty()) {
                        binding.btnContactUs.text =
                            getString(com.jar.app.core_ui.R.string.contact_us)
                        binding.btnContactUs.compoundDrawablePadding = 8.dp
                    } else {
                        binding.btnContactUs.text = ""
                        binding.btnContactUs.compoundDrawablePadding = 0
                    }
                    selectedAdapter?.submitList(it)
                    viewModel.fetchTransactions(
                        transactionViewModel.selectedFiltersLiveData.value,
                        transactionViewModel.selectedDates
                    )
                }
            }
        }

        viewModel.goldBalanceLiveData.asLiveData().observe(viewLifecycleOwner) { data ->
            if (data.status == RestClientResult.Status.SUCCESS) {
                data.data?.data?.let { _goldBalance ->
                    setupGoldLeaseData(_goldBalance)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.goldTransactionFlow.collectLatest {
                    it?.let {
                        transactionListAdapter?.submitData(it)
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                postShownEvent()
            }
        }
    }

    private fun postShownEvent() {
        if (isBindingInitialized() && lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
            analyticsHandler.postEvent(
                TransactionConstants.AnalyticsKeys.Shown_GoldTransactionScreen,
                mapOf(
                    TransactionConstants.AnalyticsKeys.Parameters.Winnings_Status to viewModel.userGoldLiveData.value.data?.data?.jarWinningsFooter?.text.orEmpty()
                        .getHtmlTextValue().toString()
                )
            )
        }
    }

    private fun setJarWinningsFooterData(jarWinningsFooter: IconBackgroundTextComponent) {
        transactionViewModel.setWinningsTabAlert(jarWinningsFooter.text.isNullOrEmpty().not())
        binding.llJarWinningsFooter.isVisible = jarWinningsFooter.text.isNullOrEmpty().not()
        jarWinningsFooter.bgColor?.takeIf { it.isNotEmpty() }?.let { bgColor ->
            val bgDrawable = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 4f.dp
                setColor(Color.parseColor(bgColor))
            }
            binding.llJarWinningsFooter.background = bgDrawable
        }
        binding.tvJarWinningsFooter.setHtmlText(jarWinningsFooter.text.orEmpty())
        binding.ivJarWinningsFooter.isVisible = jarWinningsFooter.iconUrl.isNullOrEmpty().not()
        jarWinningsFooter.iconUrl?.takeIf { it.isNotEmpty() }?.let { iconUrl ->
            Glide.with(requireContext()).load(iconUrl).into(binding.ivJarWinningsFooter)
        }
    }

    private fun setupGoldLeaseData(goldBalance: GoldBalance) {
        goldBalance.goldLeaseBreakupObject?.volumeLeased?.let { goldLeased ->
            binding.goldLeaseTopBar.isVisible = goldBalance.showLeaseBanner.orFalse()
            binding.tvGoldLeaseAmount.text = getString(R.string.gold_leased, goldLeased)
        } ?: run {
            binding.goldLeaseTopBar.isVisible = false
        }
    }

    private fun getData() {
        viewModel.fetchUserGoldDetails()
        viewModel.fetchUserGoldBalance()
        viewModel.fetchTransactions(
            transactionViewModel.selectedFiltersLiveData.value,
            transactionViewModel.selectedDates
        )
    }

    private var isFirstApiCall = true

    private fun setGoldData(data: com.jar.app.feature_transaction.shared.domain.model.UserGoldDetailsRes) {
        val goldInLocker = requireContext().getFormattedTextForOneStringValue(
            if (data.currentValue != null && data.currentValue.orZero() > 0f) {
                if (data.unitPreference == MG)
                    R.string.feature_transaction_n_mg_underline
                else
                    R.string.feature_transaction_n_gm_underline
            } else {
                if (data.unitPreference == MG)
                    R.string.feature_transaction_n_mg_non_underline
                else
                    R.string.feature_transaction_n_gm_non_underline
            },
            if (data.unitPreference == MG)
                data.volumeInMg.orZero().volumeToString(1)
            else
                data.volume.orZero().volumeToString()
        )
        binding.tvGoldInLocker.text = goldInLocker
        binding.tvCurrentValue.text =
            getString(R.string.feature_transaction_rs_value, data.currentValue.orZero())
        binding.tvInvestedValue.text =
            getString(R.string.feature_transaction_rs_value, data.investedValue.orZero())
        val investedGold = data.investedExtraGold.orZero().toString().run {
            indexOf('.').let {
                if (it != -1) {
                    substring(0, it)
                } else {
                    this
                }
            }
        }
        val includeExtraGoldString =
            getString(R.string.feature_transaction_include_extra_gold) + investedGold
        binding.includeGoldLabel.text = includeExtraGoldString.bold(
            includeExtraGoldString,
            includeExtraGoldString.indexOf(getString(R.string.rupee_sign)),
            includeExtraGoldString.length
        )

        binding.tvGoldInLocker.setDebounceClickListener {
            data.let { userGoldDetailsRes ->
                userGoldDetailsRes.currentValue?.let {
                    if (it > 0f) {
                        analyticsHandler.postEvent(
                            EventKey.TransactionsV2.Clicked_Locker_GoldTransactionScreen,
                            EventKey.TransactionsV2.GoldInLocker
                        )
                        userGoldDetailsRes.balanceView?.let {
                            EventBus.getDefault().post(OpenUserGoldBreakdownScreenEvent(it))
                        } ?: kotlin.run {
                            EventBus.getDefault()
                                .post(OpenUserGoldBreakdownScreenEvent(GoldBalanceViewType.ONLY_GM))
                        }
                    }
                }
            }
        }
    }

    private fun setEmptyLayout() {
        binding.rvTransactions.isVisible = false
        binding.emptyLayout.let {
            it.root.isVisible = true
            it.tvEmpty.text = getString(R.string.feature_transaction_no_transaction_yet)
            it.tvSeeAllTxn.text =
                getString(R.string.feature_transaction_you_can_see_all_your_transactions_here)
        }
    }

    override fun onDestroy() {
        transactionViewModel.tearDownData()
        super.onDestroy()
    }

    override fun onDestroyView() {
        glide?.clear(target)
        super.onDestroyView()
    }

    private fun updateToolbar(proportion: Float) {
        val blendedColor = ColorUtils.blendARGB(
            ContextCompat.getColor(requireContext(), com.jar.app.core_ui.R.color.transparent),
            ContextCompat.getColor(requireContext(), com.jar.app.core_ui.R.color.bgColor),
            when {
                proportion < 0 -> 0f
                proportion > 1 -> 1f
                else -> proportion
            }
        )
        transactionViewModel.sendAppBarOffsetChangeEvent(blendedColor)
    }

    fun scrollToTop() {
        if (lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
            binding.rvTransactions.smoothScrollToPosition(0)
            binding.appBarLayout.setExpanded(true, true)
        }
    }

    private fun setupCTAData(transactionPageCTA: com.jar.app.feature_transaction.shared.domain.model.TransactionPageCTA) {
        binding.tvPrimaryCta.text = transactionPageCTA.text
        transactionPageCTA.icon?.let { iconLink ->
            glide = Glide.with(requireContext())
            glide?.load(iconLink)
                ?.override(24.dp)
                ?.into(target)
        }

        binding.btnPrimaryCta.setDebounceClickListener {
            prefsApi.setUserLifeCycleForMandate(EventKey.UserLifecycles.TransactionsTab)
            EventBus.getDefault().post(
                HandleDeepLinkEvent(
                    transactionPageCTA.deeplink,
                    EventKey.UserLifecycles.TransactionsTab
                )
            )
            analyticsHandler.postEvent(
                TransactionConstants.AnalyticsKeys.GoldTransactionScreen_PrimaryCtaClicked,
                transactionPageCTA.deeplink
            )
        }
    }
}