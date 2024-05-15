package com.jar.app.feature_lending.impl.ui.repayments.overview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.withStateAtLeast
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.DispatcherProvider
import com.jar.app.base.util.addItemDecorationIfNoneAdded
import com.jar.app.base.util.dp
import com.jar.app.base.util.encodeUrl
import com.jar.app.base.util.getFormattedAmount
import com.jar.app.base.util.openWhatsapp
import com.jar.app.core_base.domain.model.OneTimePaymentGateway
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.orZero
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.core_ui.extension.playLottieWithUrlAndExceptionHandling
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.core_ui.item_decoration.SpaceItemDecoration
import com.jar.app.feature_lending.LendingNavigationDirections
import com.jar.app.feature_lending.R
import com.jar.app.feature_lending.databinding.FragmentRepaymentOverviewBinding
import com.jar.app.feature_lending.impl.domain.model.experiment.ReadyCashScreen
import com.jar.app.feature_lending.impl.ui.agreement.BreakdownInfoBottomSheetFragment
import com.jar.app.feature_lending.impl.ui.repayments.payment.RepaymentStatusFragment
import com.jar.app.feature_lending.shared.MR
import com.jar.app.feature_lending.shared.domain.LendingEventKeyV2
import com.jar.app.feature_lending.shared.domain.model.experiment.ReadyCashScreenArgs
import com.jar.app.feature_lending.shared.domain.model.experiment.ScreenData
import com.jar.app.feature_lending.shared.domain.model.repayment.RepaymentCardType
import com.jar.app.feature_lending.shared.domain.model.repayment.RepaymentDetailResponse
import com.jar.app.feature_lending.shared.domain.model.repayment.RepaymentStatus
import com.jar.app.feature_lending.shared.domain.model.v2.ForeCloseStatus
import com.jar.app.feature_lending.shared.domain.model.v2.InitiatePaymentRequest
import com.jar.app.feature_lending.shared.domain.model.v2.LoanApplicationStatusV2
import com.jar.app.feature_lending.shared.domain.model.v2.LoanTxnCategory
import com.jar.app.feature_lending.shared.util.LendingConstants
import com.jar.app.feature_one_time_payments_common.shared.ManualPaymentStatus
import com.jar.app.feature_payment.api.PaymentManager
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jar_core_network.api.util.collectUnwrapped
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@AndroidEntryPoint
internal class RepaymentOverviewFragment : BaseFragment<FragmentRepaymentOverviewBinding>() {

    @Inject
    lateinit var remoteConfigManager: RemoteConfigApi

    @Inject
    lateinit var paymentManager: PaymentManager

    @Inject
    lateinit var appScope: CoroutineScope

    @Inject
    lateinit var dispatcherProvider: DispatcherProvider

    @Inject
    lateinit var serializer: Serializer

    @Inject
    lateinit var analyticsApi: AnalyticsApi

    private val repaymentViewModelProvider by viewModels<RepaymentOverviewViewModelAndroid> { defaultViewModelProviderFactory }
    private val repaymentViewModel by lazy {
        repaymentViewModelProvider.getInstance()
    }


    private val args by navArgs<RepaymentOverviewFragmentArgs>()
    private val spaceItemDecoration = SpaceItemDecoration(0.dp, 16.dp)

    private var emiCardAdapter: RepaymentEmiCardAdapter? = null

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentRepaymentOverviewBinding
        get() = FragmentRepaymentOverviewBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))

        binding.toolBar.btnBack.setDebounceClickListener {
            analyticsApi.postEvent(
                event = LendingEventKeyV2.Repay_BackButtonClicked,
                values = mapOf(LendingEventKeyV2.screen_name to LendingEventKeyV2.Overview_Screen)
            )
            popBackStack()
        }

        binding.toolBar.tvTitle.text = getCustomString(MR.strings.feature_lending_jar_loan_overview)
        binding.toolBar.separator.isVisible = true
        binding.toolBar.ivTitleImage.isVisible = false
        binding.toolBar.lottieView.isVisible = false
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
        setupListeners()
        observeFlow()
        getData()
    }

    private fun setupUI() {
        emiCardAdapter = RepaymentEmiCardAdapter(
            loanId = args.loanId,
            onInitiatePayment = { amount, title ->
                repaymentViewModel.paymentTitle = title
                val loanAmount: String =
                    repaymentViewModel.repaymentFlow.asLiveData().value?.data?.data?.totalLoanAmount.orZero()
                        .toString()
                analyticsApi.postEvent(
                    event = LendingEventKeyV2.Repay_OverviewScreenMakePaymentClicked, values =
                    mapOf(
                        LendingEventKeyV2.emi_amount to amount,
                        LendingEventKeyV2.emi_number to title,
                        LendingEventKeyV2.loan_amount to loanAmount,
                    )
                )
                initiatePayment(amount)
            },
            onRefreshStatus = { orderId: String ->
                repaymentViewModel.orderId = orderId
                repaymentViewModel.fetchManualPaymentStatus(orderId, OneTimePaymentGateway.JUSPAY.name)
            },
            onContactUs = { msg ->
                requireContext().openWhatsapp(remoteConfigManager.getWhatsappNumber(), msg)
            }
        )
        binding.rvCards.addItemDecorationIfNoneAdded(spaceItemDecoration)
        binding.rvCards.adapter = emiCardAdapter
    }

    private fun setupListeners() {
        binding.btnEmiDetail.setDebounceClickListener {
            analyticsApi.postEvent(LendingEventKeyV2.Repay_OverviewScreenEMIDetailsClicked)
            navigateTo(
                RepaymentOverviewFragmentDirections.actionRepaymentOverviewFragmentToRepaymentHistoryFragment(args.loanId)
            )
        }

        binding.card1.setDebounceClickListener {
            analyticsApi.postEvent(LendingEventKeyV2.Repay_OverviewScreenReadyCashDetailsClicked)
            val argsData = encodeUrl(
                serializer.encodeToString(
                    ReadyCashScreenArgs(
                        loanId = args.loanId,
                        source = RepaymentOverviewFragment::class.java.name,
                        type = "",
                        screenName = ReadyCashScreen.LOAN_SUMMARY,
                        screenData = ScreenData(
                            shouldShowProgress = false,
                            nextScreen = "",
                            backScreen = "",
                            status = "PENDING"
                        ),
                        isRepeatWithdrawal = false,
                        isRepayment = true
                    )
                )
            )
            navigateTo(
                uri = "android-app://com.jar.app/loanSummaryV2Fragment/$argsData", shouldAnimate = true
            )
        }

        binding.card2.setDebounceClickListener {
            analyticsApi.postEvent(LendingEventKeyV2.Repay_OverviewScreenForeClosureClicked)
            navigateTo(LendingNavigationDirections.actionToForecloseSummaryFragment(args.loanId, true))
        }

        binding.card3.setDebounceClickListener {
            analyticsApi.postEvent(LendingEventKeyV2.Repay_OverviewScreenChargesnFeesClicked)
            navigateTo(
                uri = "android-app://com.jar.app/breakdownInfoBottomSheetFragment/${args.loanId}/${BreakdownInfoBottomSheetFragment.ARGS_CHARGES}"
            )
        }

        findNavController().currentBackStackEntry?.savedStateHandle
            ?.getLiveData<Pair<String, Float>>(LendingConstants.STATUS_SCREEN_ACTION)
            ?.observe(viewLifecycleOwner) {
                if (it.first == LendingConstants.SCREEN_ACTION_PAY_AGAIN) {
                    initiatePayment(it.second)
                    findNavController().currentBackStackEntry?.savedStateHandle?.remove<Pair<String, Float>>(
                        LendingConstants.STATUS_SCREEN_ACTION
                    )
                }
            }
    }

    private fun observeFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                repaymentViewModel.repaymentFlow.collect(
                    onLoading = {
                        binding.containerScrollView.isVisible = false
                        binding.shimmerPlaceholder.isVisible = true
                        binding.shimmerPlaceholder.startShimmer()
                    },
                    onSuccess = {
                        binding.shimmerPlaceholder.isVisible = false
                        binding.containerScrollView.isVisible = true
                        binding.shimmerPlaceholder.stopShimmer()
                        it?.let {
                            setupRepaymentData(it)
                        }
                    },
                    onError = { errorMessage, _ ->
                        binding.shimmerPlaceholder.isVisible = false
                        binding.containerScrollView.isVisible = true
                        binding.shimmerPlaceholder.stopShimmer()
                    }
                )
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                repaymentViewModel.preApprovedDataFlow.collect(
                    onSuccess = {
                        setGetMoreLoanDataCard(it?.availableLimit?.toInt().orZero())
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                repaymentViewModel.initiatePaymentResponseFlow.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        appScope.launch(dispatcherProvider.main) {
                            it?.let {
                                paymentManager.initiateOneTimePayment(
                                    initiatePaymentResponse = it,
                                    paymentGateway = OneTimePaymentGateway.JUSPAY
                                ).collectUnwrapped(
                                    onLoading = {
                                        showProgressBar()
                                    },
                                    onSuccess = { fetchManualPaymentStatusResponse ->
                                        dismissProgressBar()
                                        val status: String
                                        val title: String
                                        when (fetchManualPaymentStatusResponse.getManualPaymentStatus()) {
                                            ManualPaymentStatus.SUCCESS -> {
                                                status =
                                                    RepaymentStatusFragment.PAYMENT_STATUS_SUCCESS
                                                title = getCustomStringFormatted(
                                                    MR.strings.feature_lending_x_over_due_amount,
                                                    repaymentViewModel.paymentTitle
                                                )
                                            }

                                            ManualPaymentStatus.PENDING -> {
                                                status =
                                                    RepaymentStatusFragment.PAYMENT_STATUS_PENDING
                                                title =
                                                    getCustomString(MR.strings.feature_lending_total_payable_amount)
                                            }

                                            ManualPaymentStatus.FAILURE -> {
                                                status =
                                                    RepaymentStatusFragment.PAYMENT_STATUS_FAILURE
                                                title =
                                                    getCustomString(MR.strings.feature_lending_total_payable_amount)
                                            }
                                        }
                                        navigateTo(
                                            "android-app://com.jar.app/repaymentStatusFragment/${status}/${fetchManualPaymentStatusResponse.amount.orZero()}/${title}",
                                            popUpTo = R.id.repaymentOverviewFragment,
                                            inclusive = false
                                        )
                                    },
                                    onError = { message, _ ->
                                        appScope.launch {
                                            withStateAtLeast(Lifecycle.State.RESUMED) {
                                                dismissProgressBar()
                                                message.snackBar(binding.root)
                                            }
                                        }
                                    }
                                )
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
                repaymentViewModel.fetchManualPaymentResponseFlow.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        when (it.getManualPaymentStatus()) {
                            ManualPaymentStatus.SUCCESS -> {
                                getData()
                            }

                            ManualPaymentStatus.PENDING -> {
                                navigateTo(
                                    "android-app://com.jar.app/forecloseStatus/${ForeCloseStatus.PENDING}/${args.loanId}/${repaymentViewModel.orderId}/${true}",
                                    popUpTo = R.id.forecloseSummaryFragment,
                                    inclusive = true
                                )
                            }

                            ManualPaymentStatus.FAILURE -> {
                                navigateTo(
                                    "android-app://com.jar.app/forecloseStatus/${ForeCloseStatus.FAILURE}/${args.loanId}/${repaymentViewModel.orderId}/${true}",
                                    popUpTo = R.id.forecloseSummaryFragment,
                                    inclusive = true
                                )
                            }
                        }
                    },
                    onError = { errorMessage, _ ->
                        dismissProgressBar()
                    }
                )
            }
        }
    }

    private fun setupRepaymentData(repaymentData: RepaymentDetailResponse) {
        appScope.launch(dispatcherProvider.io) {
            val map = mutableMapOf<String, Any>()
            repaymentData.emiDetails?.forEach {
                if (it.type == RepaymentCardType.UPCOMING.name) {
                    map[LendingEventKeyV2.upcoming_emi_number] = it.emiCount.orEmpty()
                    map[LendingEventKeyV2.upcoming_emi_status] = it.status.orEmpty()
                } else {
                    if (it.type == RepaymentCardType.FAILED.name && it.paymentStatus == RepaymentStatus.PAYMENT_OVERDUE.name)
                        map[LendingEventKeyV2.failed_emi_count] = it.emiCount.orEmpty()
                    map[LendingEventKeyV2.last_emi_number] = it.emiCount.orEmpty()
                    map[LendingEventKeyV2.last_emi_status] = it.status.orEmpty()
                    map[LendingEventKeyV2.payment_status] = it.paymentStatus.orEmpty()
                }
            }
            map[LendingEventKeyV2.loan_status] = repaymentData.loanStatus.orEmpty()
            analyticsApi.postEvent(
                event = LendingEventKeyV2.Repay_LoanOverviewScreenLauched, values = map
            )
        }

        binding.tvLoanTaken.text = getCustomStringFormatted(MR.strings.feature_lending_rupee_prefix_string, repaymentData.totalLoanAmount?.toInt().orZero().getFormattedAmount())
        binding.tvAmountPaid.text = getCustomStringFormatted(MR.strings.feature_lending_rupee_prefix_string, repaymentData.paidLoanAMount?.toInt().orZero().getFormattedAmount())
        binding.tvEmiPaid.text = "${repaymentData.paidEMI?.orZero()}/${repaymentData.totalEMI?.orZero()}"
        if (repaymentData.isForecloseEnabled == false) {
            binding.card2.isClickable = false
            binding.card2.isEnabled = false
            binding.card2.alpha = 0.5f
        }
        when (repaymentData.loanStatus) {
            LoanApplicationStatusV2.FORECLOSED.name -> setUpCelebrationLayout(true)
            LoanApplicationStatusV2.CLOSED.name -> setUpCelebrationLayout(false)
            else -> emiCardAdapter?.submitList(repaymentData.emiDetails)
        }
    }

    private fun setUpCelebrationLayout(isForeclosed: Boolean) {
        repaymentViewModel.fetchPreApprovedData()  //This will render the Get More Loan Card
        binding.tvStatic.isVisible = false
        binding.rvCards.isVisible = false
        binding.card2.isVisible = false
        binding.clSuccess.isVisible = true
        binding.lottieViewCelebration.isVisible = true
        binding.tvTitleCelebration.text = getCustomString(if (isForeclosed) MR.strings.feature_lending_yay_loan_is_foreclosed else MR.strings.feature_lending_yay_loan_is_over)
        binding.lottieViewCelebration.playLottieWithUrlAndExceptionHandling(requireContext(), BaseConstants.LottieUrls.CONFETTI_FROM_TOP)
    }

    private fun setGetMoreLoanDataCard(preApprovedAmount: Int) {
        binding.moreCard.isVisible = true
        binding.lottieViewGetMore.playLottieWithUrlAndExceptionHandling(requireContext(), LendingConstants.LottieUrls.GET_MORE_LOAN)
        binding.tvPreApprovedAmount.text = getCustomStringFormatted(MR.strings.feature_lending_you_have_preapproved_x, preApprovedAmount.getFormattedAmount())
        binding.btnGetMore.setDebounceClickListener {
            popBackStack()
        }
    }

    private fun getData() {
        repaymentViewModel.fetchRepaymentDetails(args.loanId)
    }

    private fun initiatePayment(amount: Float) {
        //initiate payment
        repaymentViewModel.initiatePayment(
            InitiatePaymentRequest(
                txnAmt = amount,
                orderId = args.loanId,
                paymentProvider = OneTimePaymentGateway.JUSPAY.name,
                loanTxnCategory = LoanTxnCategory.OVERDUE_PAYMENT.name,
                transactionType = "LOAN_REPAYMENT",
                emiType = repaymentViewModel.paymentTitle
            )
        )
    }
}