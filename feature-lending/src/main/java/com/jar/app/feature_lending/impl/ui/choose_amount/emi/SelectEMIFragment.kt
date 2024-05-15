package com.jar.app.feature_lending.impl.ui.choose_amount.emi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import androidx.core.text.color
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jar.app.base.data.event.LendingBackPressEvent
import com.jar.app.base.data.event.LendingToolbarTitleEventV2
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.*
import com.jar.app.core_base.util.orFalse
import com.jar.app.core_base.util.orZero
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.item_decoration.SpaceItemDecoration
import com.jar.app.feature_lending.LendingStepsNavigationDirections
import com.jar.app.feature_lending.R
import com.jar.app.feature_lending.databinding.FragmentSelectEmiPlanBinding
import com.jar.app.feature_lending.impl.domain.event.ReadyCashNavigationEvent
import com.jar.app.feature_lending.impl.ui.host_container.LendingHostViewModelAndroid
import com.jar.app.feature_lending.shared.ui.step_view.LendingStep
import com.jar.app.feature_lending.shared.MR
import com.jar.app.feature_lending.shared.domain.LendingEventKeyV2
import com.jar.app.feature_lending.shared.domain.model.experiment.ReadyCashScreenArgs
import com.jar.app.feature_lending.shared.domain.model.v2.DrawdownDetails
import com.jar.app.feature_lending.shared.domain.model.v2.LoanApplicationUpdateResponseV2
import com.jar.app.feature_lending.shared.domain.model.v2.UpdateLoanDetailsBodyV2
import com.jar.app.feature_lending.shared.util.LendingConstants
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
internal class SelectEMIFragment : BaseFragment<FragmentSelectEmiPlanBinding>() {

    @Inject
    lateinit var remoteConfigApi: RemoteConfigApi

    @Inject
    lateinit var analyticsApi: AnalyticsApi

    @Inject
    lateinit var prefs: PrefsApi

    @Inject
    lateinit var serializer: Serializer

    private var isRecommendedClickedOnce = false

    private var adapter: PlanListAdapter? = null
    private var spaceDecoration = SpaceItemDecoration(0.dp, 8.dp)
    private var isScrolled = false

    private val arguments by navArgs<SelectEMIFragmentArgs>()
    private val args by lazy {
        serializer.decodeFromString<ReadyCashScreenArgs>(
            decodeUrl(arguments.screenArgs)
        )
    }

    private val viewModelProvider by viewModels<SelectEmiPlanViewModelAndroid> { defaultViewModelProviderFactory }
    private val parentViewModelProvider by activityViewModels<LendingHostViewModelAndroid> { defaultViewModelProviderFactory }
    private val parentViewModel by lazy {
        parentViewModelProvider.getInstance()
    }


    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }
    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentSelectEmiPlanBinding
        get() = FragmentSelectEmiPlanBinding::inflate

    private var backPressCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                handleBackNavigation()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        analyticsApi.postEvent(
            event = LendingEventKeyV2.Lending_RCashEMIScreenLaunched,
            values = mapOf(
                LendingEventKeyV2.amount to parentViewModel.selectedAmount,
                LendingEventKeyV2.user_type to getUsertype(),
                LendingEventKeyV2.lender to args.lender.orEmpty()
            )
        )
    }

    override fun setupAppBar() {

    }

    private fun getUsertype(): String {
        return if (args.isRepeatWithdrawal) "Repeat" else "New"
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
        setupListeners()
        observeFlow()
        getData()
        registerBackPressDispatcher()
    }

    private fun setupUI() {
        binding.btnAction.setDisabled(true)
        adapter = PlanListAdapter {
            analyticsApi.postEvent(
                event = LendingEventKeyV2.Lending_RCashEMITenureSelected,
                values = mapOf(
                    LendingEventKeyV2.amount to parentViewModel.selectedAmount,
                    LendingEventKeyV2.emi_per_month to it.amountPerMonth,
                    LendingEventKeyV2.tenure to it.tenure,
                    LendingEventKeyV2.roi to viewModel.roi,
                    LendingEventKeyV2.user_type to getUsertype(),
                    LendingEventKeyV2.lender to args.lender.orEmpty()
                )
            )
            binding.btnAction.setDisabled(false)
            viewModel.selectScheme(it)
        }
        binding.rvEmiPlans.adapter = adapter
        binding.rvEmiPlans.addItemDecorationIfNoneAdded(spaceDecoration)
        binding.lendingToolbar.root.isVisible = args.screenData?.shouldShowProgress.orFalse().not()
        binding.lendingToolbar.tvTitle.text =
            getCustomString(MR.strings.feature_lending_flexible_emi_plan)
        setAmountOnUi()
        EventBus.getDefault().post(LendingToolbarTitleEventV2(getCustomString(MR.strings.feature_lending_loan_application)))
    }

    private fun setAmountOnUi() {
        binding.tvAmount.text = getCustomStringFormatted(MR.strings.feature_lending_rupee_prefix_string,
            parentViewModel.selectedAmount.getFormattedAmount()
        )
    }

    private fun setupListeners() {
        binding.btnAction.setDebounceClickListener {
            viewModel.currentlySelectedScheme?.let { creditLineScheme ->
                analyticsApi.postEvent(
                    event = if (args.isRepeatWithdrawal) LendingEventKeyV2.Lending_RCashEMIConfirmClicked
                    else LendingEventKeyV2.Lending_RCashEMIScreenConfirmClicked,
                    values = mapOf(
                        LendingEventKeyV2.amount to parentViewModel.selectedAmount,
                        LendingEventKeyV2.emi_per_month to creditLineScheme.amountPerMonth,
                        LendingEventKeyV2.tenure to creditLineScheme.tenure,
                        LendingEventKeyV2.user_type to getUsertype(),
                        LendingEventKeyV2.lender to args.lender.orEmpty(),
                        LendingEventKeyV2.scrolled to isScrolled
                    )
                )
                viewModel.updateDrawDown(
                    UpdateLoanDetailsBodyV2(
                        applicationId = args.loanId,
                        drawdownDetails = DrawdownDetails(
                            emiAmount = creditLineScheme.amountPerMonth,
                            firstEMIDate = creditLineScheme.firstEmiDate,
                            lastEMIDate = creditLineScheme.lastEmiDate,
                            roi = viewModel.schemeFlow.asLiveData().value?.data?.data?.roi,
                            tenure = creditLineScheme.tenure,
                            totalAmount = viewModel.schemeFlow.asLiveData().value?.data?.data?.amount,
                            totalRepaymentAmount = creditLineScheme.repaymentAmount
                        )
                    )
                )
            }
        }

        binding.rvEmiPlans.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!recyclerView.canScrollVertically(1) && dy > 0) { //scrolled to BOTTOM
                    isScrolled = true
                    analyticsApi.postEvent(
                        LendingEventKeyV2.Lending_RCashEMIScreenScrolled,
                        mapOf(
                            LendingEventKeyV2.user_type to getUsertype(),
                            LendingEventKeyV2.screen_name to LendingEventKeyV2.EMI_SCREEN
                        )
                    )
                }
            }
        })

        binding.lendingToolbar.btnBack.setDebounceClickListener {
            handleBackNavigation()
        }
        binding.lendingToolbar.btnNeedHelp.setDebounceClickListener {
            analyticsApi.postEvent(
                LendingEventKeyV2.Lending_RCashEMINeedHelpClicked,
                mapOf(
                    LendingEventKeyV2.user_type to getUsertype(),
                    LendingEventKeyV2.screen_name to LendingEventKeyV2.EMI_SCREEN
                )
            )
            val message = getCustomStringFormatted(MR.strings.feature_lending_jar_ready_cash_need_help_template,
                getCustomString(MR.strings.feature_lending_i_need_help_regarding_emi_plans),
                prefs.getUserName().orEmpty(),
                prefs.getUserPhoneNumber().orEmpty()
            )
            requireContext().openWhatsapp(remoteConfigApi.getWhatsappNumber(), message)
        }
    }

    private fun handleBackNavigation() {
        EventBus.getDefault().post(LendingBackPressEvent(LendingEventKeyV2.READY_CASH_EMI_SCREEN))

        args.screenData?.let {
            EventBus.getDefault().postSticky(
                ReadyCashNavigationEvent(
                    whichScreen = it.backScreen,
                    source = args.screenName,
                    popupToId = R.id.selectEMIFragment,
                    isBackFlow = true
                )
            )
        }
    }

    private fun observeFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.schemeList.collect {
                    it?.let {
                        adapter?.submitList(it)
                        viewModel.findRecommendedSchemeIndex(it)
                        uiScope.launch {
                            delay(200) //intentionally added
                            if (!isRecommendedClickedOnce) {
                                isRecommendedClickedOnce = true
                                binding.rvEmiPlans.findViewHolderForAdapterPosition(viewModel.recommendedIndex.value)
                                    ?.itemView?.callOnClick()
                            }
                        }
                    }
                }
            }
        }


        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.loanDetailsFlow.collect(
                    onLoading = { showProgressBar() },
                    onSuccess = {
                        dismissProgressBar()
                        it?.let {
                            it.applicationDetails?.drawdown?.let {
                                it.totalAmount?.let {
                                    parentViewModel.selectedAmount = it.toFloat().orZero()
                                    viewModel.fetchCreditLineSchemes(parentViewModel.selectedAmount)
                                    setAmountOnUi()
                                }
                            }
                        }

                    },
                    onError = { errorMessage, _ ->
                        dismissProgressBar()
                    }
                )
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.drawDownFlow.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        it?.let { onDrawDownSuccess(it) }
                    },
                    onError = { _, _ ->
                        dismissProgressBar()
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.schemeFlow.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        viewModel.roi = it?.roi.orZero()
                        val spannable = buildSpannedString {
                            append(getCustomString(MR.strings.feature_lending_interest_rate))
                            append(" ")
                            bold {
                                color(
                                    ContextCompat.getColor(
                                        requireContext(),
                                        com.jar.app.core_ui.R.color.color_D5CDF2
                                    )
                                ) {
                                    append(
                                        getCustomStringFormatted(
                                            MR.strings.feature_lending_per_annum,
                                            it?.roi.orZero()
                                        )
                                    )
                                }
                            }
                            append(" ")
                            append(getCustomString(MR.strings.feature_lending_for_all_plans))

                        }
                        binding.tvInterestRate.text = spannable
                        it?.consentString?.let {
                            binding.tvConsent.isVisible = true
                            binding.tvConsent.text = it
                        }?:run{
                            binding.tvConsent.isVisible = false
                        }
                    },
                    onError = { errorMessage, _ ->
                        dismissProgressBar()
                    }
                )
            }
        }
    }

    private fun getData() {
        if (parentViewModel.selectedAmount > 0f) {
            viewModel.fetchCreditLineSchemes(parentViewModel.selectedAmount)
        } else {
            viewModel.fetchLoanDetails(
                LendingConstants.LendingApplicationCheckpoints.DRAW_DOWN,
                args.loanId.orEmpty()
            )
        }
    }

    private fun onDrawDownSuccess(loanApplicationUpdateResponseV2: LoanApplicationUpdateResponseV2) {
        dismissProgressBar()
        args.screenData?.let {
            EventBus.getDefault().postSticky(
                ReadyCashNavigationEvent(
                    whichScreen = it.nextScreen,
                    source = args.screenName,
                    popupToId = R.id.selectEMIFragment
                )
            )
        }
    }

    private fun registerBackPressDispatcher() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            backPressCallback
        )
    }

    override fun onDestroyView() {
        backPressCallback.isEnabled = false
        adapter = null
        super.onDestroyView()
    }

}