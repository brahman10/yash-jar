package com.jar.app.feature_lending.impl.ui.foreclosure

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.whenResumed
import androidx.navigation.fragment.navArgs
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.*
import com.jar.app.core_base.domain.model.OneTimePaymentGateway
import com.jar.app.core_base.util.orZero
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.core_ui.item_decoration.SpaceItemDecoration
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.feature_lending.R
import com.jar.app.feature_lending.databinding.FragmentForecloseSummaryBinding
import com.jar.app.feature_lending.shared.domain.model.v2.ForeCloseStatus
import com.jar.app.feature_lending.shared.domain.model.v2.ForeCloseTxnData
import com.jar.app.feature_lending.shared.domain.model.v2.InitiatePaymentRequest
import com.jar.app.feature_lending.shared.domain.model.v2.LoanTxnCategory
import com.jar.app.feature_lending.impl.ui.common.KeyValueAdapter
import com.jar.app.feature_lending.shared.domain.LendingEventKeyV2
import com.jar.app.feature_lending.shared.util.LendingConstants
import com.jar.app.feature_one_time_payments_common.shared.ManualPaymentStatus
import com.jar.app.feature_payment.api.PaymentManager
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.internal.library.jar_core_network.api.util.collectUnwrapped
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import javax.inject.Inject
import com.jar.app.feature_lending.shared.MR
import com.jar.internal.library.jar_core_network.api.util.collect

@AndroidEntryPoint
internal class ForecloseSummaryFragment : BaseFragment<FragmentForecloseSummaryBinding>() {

    private var adapter: KeyValueAdapter? = null

    @Inject
    lateinit var paymentManager: PaymentManager

    @Inject
    lateinit var appScope: CoroutineScope

    @Inject
    lateinit var dispatcherProvider: DispatcherProvider

    @Inject
    lateinit var analyticsApi: AnalyticsApi

    @Inject
    lateinit var serializer: Serializer

    private val viewModelProvider: ForeclosureSummaryViewModelAndroid by viewModels { defaultViewModelProviderFactory }
    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }


    private val args by navArgs<ForecloseSummaryFragmentArgs>()

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentForecloseSummaryBinding
        get() = FragmentForecloseSummaryBinding::inflate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        analyticsApi.postEvent(
            event = LendingEventKeyV2.Lending_ForeclosureSummaryShown,
            values = mapOf(LendingEventKeyV2.source to getFlowType())
        )
    }

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
        binding.toolbar.tvTitle.text = getCustomString(MR.strings.feature_lending_foreclose_summary)
        binding.toolbar.btnBack.setDebounceClickListener {
            popBackStack()
        }
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
        setupListeners()
        observeFlow()
        observeCurrentNetworkStateLiveData()
        getData()
    }

    private fun setupUI() {
        adapter = KeyValueAdapter()
        binding.rvForeCloseSummary.adapter = adapter
        binding.rvForeCloseSummary.addItemDecorationIfNoneAdded(SpaceItemDecoration(0, 8))
    }

    private fun setupListeners() {
        binding.btnAction.setDebounceClickListener {
            analyticsApi.postEvent(LendingEventKeyV2.Lending_ForeclosureMakePaymentClicked)
            //initiate payment
            viewModel.loanDetailsV2?.let {
                viewModel.initiatePayment(
                    InitiatePaymentRequest(
                        txnAmt = it.applicationDetails?.foreclosure?.totalAmount.orZero(),
                        orderId = it.applicationId.orEmpty(),
                        paymentProvider = OneTimePaymentGateway.JUSPAY.name,
                        loanTxnCategory = LoanTxnCategory.FORECLOSURE.name,
                        transactionType = "LOAN_REPAYMENT"
                    )
                )
            }

        }
    }

    private fun observeFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.loanDetailsFlow.collect(
                    onLoading = {
                        binding.shimmerPlaceholder.shimmerLayout.startShimmer()
                        binding.shimmerPlaceholder.shimmerLayout.isVisible = true
                        binding.llBottom.isVisible = false
                        binding.clContentHolder.isVisible = false
                    },
                    onSuccess = {
                        binding.shimmerPlaceholder.shimmerLayout.stopShimmer()
                        binding.shimmerPlaceholder.shimmerLayout.isVisible = false
                        binding.llBottom.isVisible = true
                        binding.clContentHolder.isVisible = true
                        viewModel.loanDetailsV2 = it
                        it?.applicationDetails?.foreclosure?.let {
                            adapter?.submitList(it.details)
                            binding.tvTotalAmount.text = getCustomStringFormatted(
                                MR.strings.feature_lending_rupee_prefix_string,
                                it.totalAmount?.getFormattedAmount().orEmpty()
                            )
                            binding.tvTotalAmountValue.text = getCustomStringFormatted(
                                MR.strings.feature_lending_rupee_prefix_string,
                                it.totalAmount?.getFormattedAmount().orEmpty()
                            )
                        }
                    },
                    onError = { errorMessage, _ ->
                        binding.shimmerPlaceholder.shimmerLayout.stopShimmer()
                        binding.shimmerPlaceholder.shimmerLayout.isVisible = false
                        binding.llBottom.isVisible = true
                        binding.clContentHolder.isVisible = true
                    }
                )
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.initiatePaymentResponseFlow.collect(
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
                                        when (fetchManualPaymentStatusResponse.getManualPaymentStatus()) {
                                            ManualPaymentStatus.SUCCESS -> { //redirect to success screen
                                                val data = encodeUrl(
                                                    serializer.encodeToString(
                                                        ForeCloseTxnData(
                                                            txnId = fetchManualPaymentStatusResponse.transactionId.orEmpty(),
                                                            txnDate = fetchManualPaymentStatusResponse.transactionDate?.getDateShortMonthNameAndYear()
                                                                .orEmpty(),
                                                            paidUsing = fetchManualPaymentStatusResponse.paymentProvider.orEmpty(),
                                                            paidUsingDetail = fetchManualPaymentStatusResponse.description.orEmpty()
                                                        )
                                                    )
                                                )
                                                navigateTo(
                                                    "android-app://com.jar.app/forecloseSuccess/$data/${args.loanId}/${getFlowType()}",
                                                    popUpTo = R.id.forecloseSummaryFragment,
                                                    inclusive = true
                                                )
                                            }

                                            ManualPaymentStatus.PENDING -> { //redirect to pending screen
                                                navigateTo(
                                                    "android-app://com.jar.app/forecloseStatus/${ForeCloseStatus.PENDING}/${args.loanId}/${it.orderId}/${args.isFromRepayment}",
                                                    popUpTo = R.id.forecloseSummaryFragment,
                                                    inclusive = true
                                                )
                                            }

                                            ManualPaymentStatus.FAILURE -> {  //redirect to failed screen
                                                navigateTo(
                                                    "android-app://com.jar.app/forecloseStatus/${ForeCloseStatus.FAILURE}/${args.loanId}/${it.orderId}/${args.isFromRepayment}",
                                                    popUpTo = R.id.forecloseSummaryFragment,
                                                    inclusive = true
                                                )
                                            }
                                        }
                                    },
                                    onError = { message, errorCode ->
                                        dismissProgressBar()
                                        uiScope.launch {
                                            whenResumed {
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
    }

    private fun getData() {
        viewModel.fetchLoanDetails(
            checkPoint = LendingConstants.LendingApplicationCheckpoints.FORECLOSURE,
            shouldPassCheckpoint = true,
            loanId = args.loanId
        )
    }

    override fun onResume() {
        super.onResume()
        viewModelProvider.observeNetwork()
    }

    private fun observeCurrentNetworkStateLiveData() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModelProvider.networkStateFlow.collect {
                    binding.toolbar.clNetworkContainer.isSelected = it
                    binding.toolbar.tvInternetConnectionText.text =
                        if (it) getString(com.jar.app.core_ui.R.string.core_ui_we_are_back_online) else getString(
                            com.jar.app.core_ui.R.string.core_ui_no_internet_available_please_try_again
                        )
                    binding.toolbar.tvInternetConnectionText.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        if (it) com.jar.app.core_ui.R.drawable.ic_wifi_on else com.jar.app.core_ui.R.drawable.ic_wifi_off,
                        0,
                        0,
                        0
                    )
                    if (it) {
                        if (binding.toolbar.networkExpandableLayout.isExpanded) {
                            uiScope.launch {
                                delay(500)
                                binding.toolbar.networkExpandableLayout.collapse(true)
                            }
                        }
                    } else {
                        binding.toolbar.networkExpandableLayout.expand(true)
                    }
                }
            }
        }
    }

    private fun getFlowType(): String {
        return if (args.isFromRepayment) "Repayment Flow" else "Foreclosure Flow"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter = null
    }
}