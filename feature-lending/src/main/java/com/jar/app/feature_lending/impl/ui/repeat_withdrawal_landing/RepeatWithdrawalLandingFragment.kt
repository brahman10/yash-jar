package com.jar.app.feature_lending.impl.ui.repeat_withdrawal_landing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.*
import com.jar.app.core_base.util.orZero
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.core_ui.item_decoration.SpaceItemDecoration
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.feature_lending.R
import com.jar.app.feature_lending.databinding.FeatureLendingFragmentRepeatWithdrawalLandingBinding
import com.jar.app.feature_lending.impl.domain.event.LendingNavigateToRepaymentFlow
import com.jar.app.feature_lending.impl.domain.event.ReadyCashNavigationEvent
import com.jar.app.feature_lending.shared.domain.model.experiment.ReadyCashScreenArgs
import com.jar.app.feature_lending.shared.domain.model.v2.LoanApplicationStatusV2
import com.jar.app.feature_lending.shared.domain.model.v2.PreApprovedData
import com.jar.app.feature_lending.impl.ui.educational_intro.LendingEducationalIntroFragmentArgs
import com.jar.app.feature_lending.shared.domain.LendingEventKeyV2
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import javax.inject.Inject
import com.jar.app.feature_lending.shared.MR
import com.jar.internal.library.jar_core_network.api.util.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
internal class RepeatWithdrawalLandingFragment :
    BaseFragment<FeatureLendingFragmentRepeatWithdrawalLandingBinding>() {

    @Inject
    lateinit var prefs: PrefsApi

    @Inject
    lateinit var serializer: Serializer

    @Inject
    lateinit var remoteConfigManager: RemoteConfigApi

    @Inject
    lateinit var analyticsApi: AnalyticsApi

    private val arguments by navArgs<LendingEducationalIntroFragmentArgs>()
    private val args by lazy {
        serializer.decodeFromString<ReadyCashScreenArgs>(
            decodeUrl(arguments.screenArgs)
        )
    }

    private val viewModelProvider: RepeatWithdrawalLandingViewModelAndroid by viewModels { defaultViewModelProviderFactory }
    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }


    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureLendingFragmentRepeatWithdrawalLandingBinding
        get() = FeatureLendingFragmentRepeatWithdrawalLandingBinding::inflate

    private val introAdapter by lazy { IntroRecyclerAdapter() }
    private val trackReadyCashAdapter by lazy { TrackReadyCashAdapter() }
    private var job: Job? = null
    private var currentPosition = 0
    private var isScrolling = false

    companion object {
        private const val DEFAULT_INTRO_SPAN_COUNT = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getData()
    }

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
        setupListeners()
        observeFlow()
    }

    private fun setupUI() {
        binding.toolbar.tvTitle.text = getCustomString(MR.strings.feature_lending_ready_cash_title)
        binding.toolbar.separator.isVisible = true


        //setup track your ready cash list
        binding.rvReadyCashSummary.layoutManager = LinearLayoutManager(requireContext())
        binding.rvReadyCashSummary.adapter = trackReadyCashAdapter
        binding.rvReadyCashSummary.addItemDecorationIfNoneAdded(
            SpaceItemDecoration(
                0,
                12.dp,
                RecyclerView.VERTICAL,
                true
            )
        )

        //Setup intro view
        binding.rvIntro.layoutManager = GridLayoutManager(
            requireContext(),
            DEFAULT_INTRO_SPAN_COUNT,
            GridLayoutManager.HORIZONTAL,
            false
        )
        binding.rvIntro.adapter = introAdapter
        LinearSnapHelper().apply {
            attachToRecyclerView(binding.rvIntro)
        }
        binding.rvIntro.addItemDecorationIfNoneAdded(
            SpaceItemDecoration(
                8.dp,
                0,
                RecyclerView.HORIZONTAL,
                true
            )
        )
        introAdapter.submitList(viewModelProvider.getIntroData())
        autoAnimateIntroCards()
    }

    private fun autoAnimateIntroCards() {
        job?.cancel()
        job = uiScope.doRepeatingTask(repeatInterval = 3000) {
            if (isScrolling.not()) {
                if (currentPosition >= introAdapter.itemCount - 1)
                    currentPosition = -1
                binding.rvIntro.smoothScrollToPosition(++currentPosition)
                updateTabLayoutIndicator(currentPosition)
            }
        }
        binding.rvIntro.addOnScrollListener(object : OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                isScrolling = newState == RecyclerView.SCROLL_STATE_DRAGGING
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (recyclerView.scrollState == RecyclerView.SCROLL_STATE_SETTLING) {
                    val position =
                        (recyclerView.layoutManager as GridLayoutManager).findFirstCompletelyVisibleItemPosition()
                    updateTabLayoutIndicator(position)
                }
            }
        })
    }

    private fun updateTabLayoutIndicator(position: Int) {
        if (position < 0 || position > 2) return
        binding.indicatorView.selectIndicator(position)
    }

    private fun toggleAvailableLimit() {
        binding.groupAvailableBalance.isVisible = true
        binding.tvBalanceOver.isVisible = false
    }

    private fun toggleLimitExhausted() {
        binding.groupAvailableBalance.isInvisible = true
        binding.tvBalanceOver.isVisible = true
    }


    private fun setupListeners() {
        binding.toolbar.btnBack.setDebounceClickListener {
            args.screenData?.let {
                EventBus.getDefault().postSticky(
                    ReadyCashNavigationEvent(
                        whichScreen = it.backScreen,
                        source = args.screenName,
                        popupToId = R.id.repeatWithdrawalLandingFragment
                    )
                )
            }
        }
        binding.btnGetReadyCash.setDebounceClickListener {
            postEvent(LendingEventKeyV2.Lending_RepeatWHomeScreenReadyCashClicked)
            args.screenData?.let {
                EventBus.getDefault().postSticky(
                    ReadyCashNavigationEvent(
                        whichScreen = it.nextScreen,
                        source = args.screenName,
                        popupToId = R.id.repeatWithdrawalLandingFragment
                    )
                )
            }
        }
        binding.clNeedSupportHolder.setDebounceClickListener {
            postEvent(LendingEventKeyV2.Lending_RepeatWHomeScreenNeedSupportClicked)
            val message = getCustomStringFormatted(MR.strings.feature_lending_jar_ready_cash_need_help_template,
                getCustomString(MR.strings.feature_lending_i_need_help_regarding_ready_cash),
                prefs.getUserName().orEmpty(),
                prefs.getUserPhoneNumber().orEmpty()
            )
            requireContext().openWhatsapp(remoteConfigManager.getWhatsappNumber(), message)
        }

        trackReadyCashAdapter.setCardClickListener { loanApplication ->
            viewModel.preApprovedData?.let {
                analyticsApi.postEvent(
                    LendingEventKeyV2.Lending_RepeatWHomeScreenLoanEMIClicked,
                    mapOf(
                        LendingEventKeyV2.entry_type to args.source,
                        LendingEventKeyV2.available_cash_limit to it.availableLimit.orZero().toInt(),
                        LendingEventKeyV2.button_status to (!it.blockLoanButton.orFalse()).toYesOrNo(),
                        LendingEventKeyV2.credit_limit_status to it.limitMessage.orEmpty(),
                        LendingEventKeyV2.paid_loan_amount to loanApplication.paidLoanAmount.orZero(),
                        LendingEventKeyV2.loan_amount to loanApplication.totalLoanAmount.orZero(),
                        LendingEventKeyV2.loan_status to if (loanApplication.status == LoanApplicationStatusV2.CLOSED.name
                            || loanApplication.status == LoanApplicationStatusV2.FORECLOSED.name
                        )
                            "completed" else "active",
                        LendingEventKeyV2.ready_cash_name to loanApplication.readyCashName.orEmpty()
                    )
                )
            }
            loanApplication.applicationId?.let {
                EventBus.getDefault().post(LendingNavigateToRepaymentFlow(it))
            }
        }
    }


    private fun observeFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.preApprovedDataFlow.collect(
                    onLoading = {
                        binding.shimmerPlaceholder.shimmerLayout.isVisible = true
                    },
                    onSuccess = {
                        it?.let {
                            binding.shimmerPlaceholder.shimmerLayout.isVisible = false
                            binding.svContent.isVisible = true
                            viewModel.preApprovedData = it
                            setPreApprovedDataOnUi(it)
                        }

                    },
                    onError = { errorMessage, _ ->
                        binding.shimmerPlaceholder.shimmerLayout.isVisible = false
                        errorMessage.snackBar(binding.rvReadyCashSummary)
                    }
                )
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.loanApplicationsFlow.collect(
                    onLoading = {},
                    onSuccess = {
                        binding.rvReadyCashSummary.isVisible = true
                        it?.let {
                            it.find { it.status == LoanApplicationStatusV2.IN_PROGRESS.name }.let {
                                viewModel.loanId = it?.applicationId
                            }
                            trackReadyCashAdapter.submitList(it.filter { it.status != LoanApplicationStatusV2.IN_PROGRESS.name })
                        }
                    },
                    onError = { errorMessage, _ ->
                        errorMessage.snackBar(binding.rvReadyCashSummary)
                    }
                )
            }
        }
    }

    private fun setPreApprovedDataOnUi(preApprovedData: PreApprovedData) {
        val balanceAmount = preApprovedData.availableLimit.orZero().toInt()
        val offerAmount = preApprovedData.offerAmount.orZero().toInt()
        binding.tvBalanceAmount.text = getCustomStringFormatted(MR.strings.feature_lending_rupee_prefix_string,
            balanceAmount.getFormattedAmount()
        )
        binding.tvCashLimit.text = getCustomStringFormatted(MR.strings.feature_lending_ready_cash_limit_s,
            offerAmount.getFormattedAmount()
        )
        //Limit Available but cannot withdraw  && Limit Frozen  handled from BE
        binding.btnGetReadyCash.isGone = preApprovedData.blockLoanButton.orFalse()
        binding.tvAlertMessage.isVisible = preApprovedData.blockLoanButton.orFalse()
        preApprovedData.limitMessage?.let {
            binding.tvAlertMessage.isVisible = true
            binding.tvAlertMessage.text = it
        } ?: run {
            binding.tvAlertMessage.isVisible = false
        }
        if (preApprovedData.availableLimit.orZero() == 0) { //Limit Exhausted
            toggleLimitExhausted()
            preApprovedData.limitMessage?.let {
                binding.tvAlertMessage.text = it
            }
        } else {
            toggleAvailableLimit()
        }
        binding.lavMoneyWithStage.playAnimation()
        postEvent(LendingEventKeyV2.Lending_RepeatWHomeScreenLaunched)
    }

    private fun postEvent(eventName: String) {
        viewModel.preApprovedData?.let {
            analyticsApi.postEvent(
                eventName,
                mapOf(
                    LendingEventKeyV2.entry_type to args.source,
                    LendingEventKeyV2.available_cash_limit to it.availableLimit.orZero().toInt(),
                    LendingEventKeyV2.button_status to it.blockLoanButton.orFalse().not().toYesOrNo(),
                    LendingEventKeyV2.credit_limit_status to it.limitMessage.orEmpty()
                )
            )
        }
    }

    private fun getData() {
        viewModel.fetchPreApproved()

    }

    override fun onResume() {
        super.onResume()
        //refresh application list on resume
        viewModel.fetchLoanApplicationList()
    }

    override fun onDestroyView() {
        job?.cancel()
        super.onDestroyView()
    }
}