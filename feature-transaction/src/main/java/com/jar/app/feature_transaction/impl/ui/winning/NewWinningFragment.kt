package com.jar.app.feature_transaction.impl.ui.winning

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.appbar.AppBarLayout
import com.jar.app.base.data.event.HandleDeepLinkEvent
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.*
import com.jar.app.core_analytics.EventKey
import com.jar.app.core_base.domain.model.IconBackgroundTextComponent
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.orZero
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.core_ui.extension.getHtmlTextValue
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.core_ui.item_decoration.SpaceItemDecoration
import com.jar.app.feature_buy_gold_v2.api.BuyGoldV2Api
import com.jar.app.feature_kyc.api.KycApi
import com.jar.app.feature_spin.api.SpinApi
import com.jar.app.feature_transaction.R
import com.jar.app.feature_transaction.databinding.FeatureTransactionFragmentNewWinningBinding
import com.jar.app.feature_transaction.impl.domain.event.ShowTransactionOverLayEvent
import com.jar.app.feature_transaction.impl.ui.TransactionFragmentViewModelAndroid
import com.jar.app.feature_transaction.impl.ui.winning.adapter.WinningListAdapter
import com.jar.app.feature_transaction.impl.ui.winning.ui.WinningTransactionViewModelAndroid
import com.jar.app.feature_transaction.shared.util.TransactionConstants
import com.jar.app.feature_user_api.domain.model.UserKycStatus
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject
import kotlin.math.abs

@AndroidEntryPoint
class NewWinningFragment : BaseFragment<FeatureTransactionFragmentNewWinningBinding>() {

    @Inject
    lateinit var remoteConfigManager: RemoteConfigApi

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    @Inject
    lateinit var serializer: Serializer

    @Inject
    lateinit var kycApi: KycApi

    @Inject
    lateinit var buyGoldApi: BuyGoldV2Api

    @Inject
    lateinit var spinApi: SpinApi

    private var userKycStatus: UserKycStatus? = null

    private val transactionViewModelProvider by activityViewModels<TransactionFragmentViewModelAndroid> { defaultViewModelProviderFactory }

    private val transactionViewModel by lazy {
        transactionViewModelProvider.getInstance()
    }

    private val viewModelProvider by viewModels<WinningTransactionViewModelAndroid> { defaultViewModelProviderFactory }

    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }

    private var winningListAdapter: WinningListAdapter? = null
    private var spaceItemDecoration = SpaceItemDecoration(16.dp, 8.dp)
    private var isFirstApiCall = true

    private var mAppBarLastOffset = 0

    private val offsetChangedListener =
        AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            binding.swipeRefresh.isEnabled = verticalOffset == 0
            mAppBarLastOffset = verticalOffset
            val proportion =
                (abs(verticalOffset.toFloat()) / appBarLayout.totalScrollRange.toFloat())
            updateToolbar(proportion)
        }

    companion object {
        fun newInstance() = NewWinningFragment()
    }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureTransactionFragmentNewWinningBinding
        get() = FeatureTransactionFragmentNewWinningBinding::inflate

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
        binding.btnConvertToGold.isEnabled = false
        binding.tvConvertToGold.alpha = 0.5f
        winningListAdapter = WinningListAdapter {
            analyticsHandler.postEvent(
                EventKey.TransactionsV2.Clicked_WinningsCard_WinningsScreen,
                mapOf(
                    EventKey.TransactionType to (it.txnType ?: ""),
                    EventKey.TransactionId to (it.orderId ?: ""),
                    EventKey.Winnings_Status to it.status.orEmpty()
                )
            )
            val encoded = encodeUrl(serializer.encodeToString(it))
            findNavController().navigate(
                Uri.parse(
                    if (it.getTransactionType() == com.jar.app.feature_transaction.shared.domain.model.WinningTxnType.DEBIT)
                        "android-app://com.jar.app/winningsDebitedDialog/$encoded"
                    else
                        "android-app://com.jar.app/winningsCreditedDialog/$encoded"
                )
            )
        }
        binding.rvWinnings.addItemDecorationIfNoneAdded(spaceItemDecoration)
        binding.rvWinnings.adapter = winningListAdapter
    }

    private fun setupListeners() {
        binding.tvTotalWinningsValue.setDebounceClickListener {
            analyticsHandler
                .postEvent(
                    EventKey.TransactionsV2.Clicked_TotalWinningsReceived_WinningsScreen
                )
            navigateTo("android-app://com.jar.app/userWinningAmountBreakDown/${BaseConstants.BreakDownType.INVESTED}")
        }
        binding.llDropDownArrow.setOnClickListener {
            if (binding.txnGroupTwo.isVisible) {
                binding.ivExpand.rotateAntiClockWise()
            } else {
                binding.ivExpand.rotateClockWise()
            }
            binding.txnGroupTwo.isVisible = !binding.txnGroupTwo.isVisible
            binding.ivExpand.rotation = if (binding.txnGroupTwo.isVisible) 0f else 180f
            analyticsHandler.postEvent(
                EventKey.TransactionsV2.Clicked_Locker_WinningsScreen
            )
        }
        binding.swipeRefresh.setOnRefreshListener {
            getData()
        }

        binding.btnContactUs.setDebounceClickListener {
            requireContext().openWhatsapp(
                remoteConfigManager.getWhatsappNumber(),
                getString(R.string.feature_transaction_hey_need_help_with_transactions)
            )
        }

        binding.rvWinnings.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                binding.swipeRefresh.isEnabled = recyclerView.canScrollVertically(-1).not()
            }
        })

        viewLifecycleOwner.lifecycleScope.launch {
            winningListAdapter?.loadStateFlow?.collectLatest { loadState ->
                val isListEmpty =
                    loadState.refresh is LoadState.NotLoading && winningListAdapter?.itemCount == 0

                if (isListEmpty)
                    setEmptyLayout()
                else {
                    binding.emptyLayout.root.isVisible = false
                    binding.rvWinnings.isVisible = true
                }
                when (loadState.refresh is LoadState.Loading) {
                    true -> {
                        binding.rvWinnings.isVisible = false
                        binding.shimmerPlaceholder.isVisible = true
                        binding.shimmerPlaceholder.startShimmer()
                    }

                    false -> {
                        if (isFirstApiCall && winningListAdapter?.itemCount.orZero() > 2) {
                            EventBus.getDefault().post(ShowTransactionOverLayEvent())
                        }
                        binding.swipeRefresh.isRefreshing = false
                        binding.shimmerPlaceholder.stopShimmer()
                        binding.shimmerPlaceholder.isVisible = false
                    }
                }
            }
        }
    }

    private fun observeLiveData() {

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.userWinningFlow.collect(
                    onSuccess = {
                        setWinningData(it)
                        setActionButtonText(it)
                        it.winningsExpiryDesc?.let { winningsExpiryDesc ->
                            postShownEvent()
                            setJarWinningsFooterData(winningsExpiryDesc)
                        } ?: kotlin.run {
                            postShownEvent()
                        }
                    },
                    onError = { errorMessage, errorCode ->
                        postShownEvent()
                        errorMessage.snackBar(binding.root)
                    }
                )
            }
        }


        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                transactionViewModel.userKycStatusLiveData.collect(
                    onSuccess = {
                        userKycStatus = it
                    },
                    onError = { errorMessage, _ ->
                        errorMessage.snackBar(binding.root)
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.winningsListFlow.collectLatest {
                    it?.let {
                        winningListAdapter?.submitData(it)
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

    private fun getData() {
        viewModel.fetchUserWinningDetails()
        viewModel.fetchWinnings()
        transactionViewModel.fetchUserKycStatus()
    }

    private fun setJarWinningsFooterData(winningsExpiryDesc: IconBackgroundTextComponent) {
        transactionViewModel.setWinningsTabAlert(winningsExpiryDesc.text.isNullOrEmpty().not())
        binding.llJarWinningsFooter.isVisible = winningsExpiryDesc.text.isNullOrEmpty().not()
        winningsExpiryDesc.bgColor?.takeIf { it.isNotEmpty() }?.let { bgColor ->
            val bgDrawable = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 4f.dp
                setColor(Color.parseColor(bgColor))
            }
            binding.llJarWinningsFooter.background = bgDrawable
        }
        binding.tvJarWinningsFooter.setHtmlText(winningsExpiryDesc.text.orEmpty())
        binding.ivJarWinningsFooter.isVisible = winningsExpiryDesc.iconUrl.isNullOrEmpty().not()
        winningsExpiryDesc.iconUrl?.takeIf { it.isNotEmpty() }?.let { iconUrl ->
            Glide.with(requireContext()).load(iconUrl).into(binding.ivJarWinningsFooter)
        }
    }

    private fun postShownEvent(){
        if (isBindingInitialized() && lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
            analyticsHandler.postEvent(
                EventKey.TransactionsV2.Shown_WinningsScreen,
                mapOf(
                    TransactionConstants.AnalyticsKeys.Parameters.Winnings_Status to viewModel.userWinningFlow.value.data?.data?.winningsExpiryDesc?.text.orEmpty()
                        .getHtmlTextValue().toString()
                )
            )
        }
    }

    private fun setActionButtonText(userWinningDetailsRes: com.jar.app.feature_transaction.shared.domain.model.UserWinningDetailsRes? = null) {
        binding.tvDisabled.isVisible = false
        binding.btnConvertToGold.isEnabled = true
        binding.tvConvertToGold.alpha = 1f
        binding.tvConvertToGold.text = userWinningDetailsRes?.winningsTabActionCTA?.ctaText
        Glide.with(binding.btnIcon)
            .load(userWinningDetailsRes?.winningsTabActionCTA?.iconLink)
            .into(binding.btnIcon)
        binding.btnConvertToGold.setDebounceClickListener {
            userWinningDetailsRes?.winningsTabActionCTA?.ctaText?.let {
                analyticsHandler
                    .postEvent(
                        EventKey.TransactionsV2.Clicked_ActionButton_WinningsScreen,
                        mapOf(
                            EventKey.TransactionsV2.Button to it
                        )
                    )
            }
            userWinningDetailsRes?.winningsTabActionCTA?.deepLink?.let { it1 ->
                EventBus.getDefault().post(HandleDeepLinkEvent(it1))
            }
        }
    }

    private fun setWinningData(data: com.jar.app.feature_transaction.shared.domain.model.UserWinningDetailsRes) {
        binding.tvMyWinnings.text =
            getString(R.string.feature_transaction_rs_value, data.myWinningsAmount.orZero())
        binding.tvTotalWinningsValue.text =
            getString(
                R.string.feature_transaction_rs_value,
                data.totalWinningsReceivedAmount.orZero()
            )
        binding.tvWinningsUsedValue.text =
            getString(R.string.feature_transaction_rs_value, data.totalWinningsUsedAmount.orZero())
        binding.tvTotalWinningsValue.paint?.isUnderlineText = true
        binding.llDropDownArrow.visibility = if (data.showChevron == false)
            View.INVISIBLE
        else
            View.VISIBLE
    }

    private fun setEmptyLayout() {
        binding.rvWinnings.isVisible = false
        binding.emptyLayout.let {
            it.root.isVisible = true
            it.tvEmpty.text = getString(R.string.feature_transaction_no_winning_yet)
            it.tvSeeAllTxn.text =
                getString(R.string.feature_transaction_you_can_see_all_winnings_here)
        }
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
            binding.rvWinnings.smoothScrollToPosition(0)
            binding.appBarLayout.setExpanded(true, true)
        }
    }
}