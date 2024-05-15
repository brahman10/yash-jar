package com.jar.app.feature_daily_investment.impl.ui.bottom_sheet.mandate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.jar.app.core_base.data.event.RefreshDailySavingEvent
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.app.base.util.addItemDecorationIfNoneAdded
import com.jar.app.base.util.isPresentInBackStack
import com.jar.app.base.util.getPhonePeVersionCode
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.orZero
import com.jar.app.core_ui.api.CoreUiApi
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.core_ui.generic_post_action.data.GenericPostActionStatusData
import com.jar.app.core_ui.generic_post_action.data.PostActionStatus
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.feature_daily_investment.R
import com.jar.app.feature_daily_investment.databinding.LayoutDailyInvestmentMandateBottomsheetBinding
import com.jar.app.feature_daily_investment.impl.domain.DailySavingsEventKey
import com.jar.app.feature_daily_investment.impl.domain.DailySavingsEventKey.DailySetupScreen_BSClicked
import com.jar.app.feature_daily_investment.shared.domain.model.DailyInvestmentMandateBottomSheetData
import com.jar.app.feature_mandate_payment.api.MandatePaymentApi
import com.jar.app.feature_mandate_payment_common.impl.ui.PaymentPageFragmentViewModelAndroid
import com.jar.app.feature_mandate_payments_common.shared.MandatePaymentBuildKonfig
import com.jar.app.feature_mandate_payments_common.shared.domain.model.initiate_mandate.InitiateMandatePaymentApiResponse
import com.jar.app.feature_mandate_payments_common.shared.domain.model.initiate_mandate.InitiateMandatePaymentRequest
import com.jar.app.feature_mandate_payments_common.shared.domain.model.initiate_mandate.MandateWorkflowType
import com.jar.app.feature_savings_common.shared.domain.model.SavingsType
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
internal class DailySavingMandateBottomSheet :
    BaseBottomSheetDialogFragment<LayoutDailyInvestmentMandateBottomsheetBinding>() {

    private val viewModelProvider by viewModels<PaymentPageFragmentViewModelAndroid> { defaultViewModelProviderFactory }

    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }

    private val bottomSheetViewModel by viewModels<DailySavingMandateViewModel> { defaultViewModelProviderFactory }

    private var paymentOptionsAdapter: DailySavingsPaymentOptionsBottomSheetAdapter? = null

    private var mandateStatusAdapter: DailySavingsMandateStatusAdapter? = null

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    @Inject
    lateinit var mandatePaymentApi: MandatePaymentApi

    @Inject
    lateinit var coreUiApi: CoreUiApi

    private var selectedPaymentOption: String? = null

    private var initiateMandatePaymentRequest: InitiateMandatePaymentRequest? = null

    private val args by navArgs<DailySavingMandateBottomSheetArgs>()

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> LayoutDailyInvestmentMandateBottomsheetBinding
        get() = LayoutDailyInvestmentMandateBottomsheetBinding::inflate

    override val bottomSheetConfig: BottomSheetConfig
        get() = BottomSheetConfig(
            isCancellable = false,
            isDraggable = false,
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getData()
    }

    override fun setup() {
        bottomSheetViewModel.fetchBottomSheetData()
        observeData()
        setUI()
        setupListeners()
    }


    fun setupListeners() {
        binding.ivCross.setDebounceClickListener {
            dismiss()
            analyticsHandler.postEvent(
                DailySetupScreen_BSClicked,
                mapOf(
                    DailySavingsEventKey.MandateAmount to args.dsAmount.toString(),
                    DailySavingsEventKey.recommended_bank to viewModel.preferredBankPageItem?.bankName.toString(),
                    DailySavingsEventKey.roundoff_status to if (bottomSheetViewModel.bottomSheetLiveData.value?.data?.data?.mandates?.size.orZero() >= 2) "true" else "false",
                    DailySavingsEventKey.available_upi to bottomSheetViewModel.paymentOptionLiveData.value?.size.toString(),
                    DailySavingsEventKey.pre_selected_upi to bottomSheetViewModel.paymentOptionLiveData.value?.getOrNull(
                        0
                    )?.packageName.orEmpty(),
                    DailySavingsEventKey.button_type to "Close",
                )
            )
        }

        binding.btnProceed.setDebounceClickListener {
            bottomSheetViewModel.isAutoPayResetRequired(args.dsAmount)
        }

    }

    private fun setUI() {
        val dividerItemDecorator = DividerItemDecoration(
            requireContext(), LinearLayoutManager.VERTICAL,
        )

        paymentOptionsAdapter = DailySavingsPaymentOptionsBottomSheetAdapter(
            context = requireContext(),
            onItemClick = { selectedOption ->
                selectedPaymentOption = selectedOption
                bottomSheetViewModel.paymentOptionLiveData.value?.let { it ->
                    paymentOptionsAdapter?.updateSelection(it.indexOfFirst { item ->
                        item.packageName == selectedOption
                    })
                }
                analyticsHandler.postEvent(
                    DailySetupScreen_BSClicked,
                    mapOf(
                        DailySavingsEventKey.MandateAmount to args.dsAmount.toString(),
                        DailySavingsEventKey.recommended_bank to viewModel.preferredBankPageItem?.bankName.toString(),
                        DailySavingsEventKey.roundoff_status to if (bottomSheetViewModel.bottomSheetLiveData.value?.data?.data?.mandates?.size.orZero() >= 1) "true" else "false",
                        DailySavingsEventKey.available_upi to bottomSheetViewModel.paymentOptionLiveData.value?.size.toString(),
                        DailySavingsEventKey.pre_selected_upi to bottomSheetViewModel.paymentOptionLiveData.value?.get(
                            0
                        )?.packageName.toString(),
                        DailySavingsEventKey.selected_upi to selectedOption,
                        DailySavingsEventKey.button_type to "UpiClick"
                    )
                )
            }
        )
        binding.rvPaymentOptions.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvPaymentOptions.adapter = paymentOptionsAdapter

        mandateStatusAdapter = DailySavingsMandateStatusAdapter()
        binding.rvMandateStatus.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMandateStatus.addItemDecorationIfNoneAdded(dividerItemDecorator)
        binding.rvMandateStatus.adapter = mandateStatusAdapter
    }

    private fun setData(data: DailyInvestmentMandateBottomSheetData?) {
        binding.let {
            it.tvHeading.text = data?.staticContent?.headerText
            it.tvPaymentOptions.text = data?.staticContent?.paymentMethodText
            data?.staticContent?.buttonText?.let { it1 -> it.btnProceed.setText(it1) }
            it.tvFooter.text = data?.staticContent?.footerText
        }
    }

    private fun initiateMandatePayment(
        initiateMandatePaymentApiResponse: InitiateMandatePaymentApiResponse,
    ) {
        if (findNavController().isPresentInBackStack(args.containerFragmentId))
            findNavController().getBackStackEntry(args.containerFragmentId)
                .savedStateHandle[com.jar.app.feature_mandate_payments_common.shared.util.MandatePaymentCommonConstants.MANDATE_PAYMENT_RESPONSE_FROM_SDK] =
                initiateMandatePaymentApiResponse
    }

    private fun removeInitiateMandatePaymentData() {
        if (findNavController().isPresentInBackStack(args.containerFragmentId))
            findNavController().getBackStackEntry(args.containerFragmentId)
                .savedStateHandle[com.jar.app.feature_mandate_payments_common.shared.util.MandatePaymentCommonConstants.MANDATE_PAYMENT_RESPONSE_FROM_SDK] =
                null
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {

                viewModel.preferredBankLiveData.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        binding.cvPreferredBankTitle.isVisible = true
                        if (it != null) {
                            binding.tvPreferredBankName.text = it.bankName
                        }
                        if (it != null) {
                            Glide.with(requireActivity())
                                .load(it.bankIconLink)
                                .into((binding.ivPreferredBankLogo))
                        }
                        dismissProgressBar()
                    },
                    onSuccessWithNullData = {
                        dismissProgressBar()
                    },
                    onError = { _, _ ->
                        dismissProgressBar()
                    },
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.paymentPageLiveData.collectLatest {
                    bottomSheetViewModel.createPaymentOptionList(it)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.fetchEnabledPaymentMethodsFlow.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        viewModel.mergeApiResponse(
                            enabledPaymentMethodResponse = it
                        )
                    },
                    onError = { errorMessage, _ ->
                        dismissProgressBar()
                        errorMessage.snackBar(binding.root)
                    }
                )
            }
        }

        bottomSheetViewModel.bottomSheetLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            WeakReference(binding.root),
            onLoading = {
                showProgressBar()
            },
            onSuccess = {
                dismissProgressBar()
                setData(it)
                bottomSheetViewModel.createMandateInfoList(
                    dailySavingsValue = args.dsAmount.toInt().toString(),
                    otherMandateName = it?.mandates?.get(0)?.typeText,
                    otherMandateValue = it?.mandates?.get(0)?.amountText
                )
            },
            onSuccessWithNullData = {
                dismissProgressBar()
            },
            onError = {
                dismissProgressBar()
            },
            suppressError = true
        )

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.initiateMandatePaymentDataLiveData.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        if (it != null) {
                            initiateMandatePayment(it)
                        }
                    },
                    onError = { errorMessage, _ ->
                        dismissProgressBar()
                        errorMessage.snackBar(getRootView())
                    }
                )
            }
        }

        bottomSheetViewModel.isAutoPayResetRequiredLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            WeakReference(binding.root),
            onSuccess = {
                val mandateWorkflowType =
                    it.authWorkflowType?.let { MandateWorkflowType.valueOf(it) }
                        ?: run { MandateWorkflowType.PENNY_DROP }

                if (it.isResetRequired) {
                    initiateMandatePaymentRequest = InitiateMandatePaymentRequest(
                        mandateAmount = it.getFinalMandateAmount(),
                        authWorkflowType = mandateWorkflowType,
                        subscriptionType = SavingsType.DAILY_SAVINGS.name,
                    )
                    viewModel.fetchInitiateMandatePaymentData(
                        mandatePaymentGateway = mandatePaymentApi.getMandatePaymentGateway(),
                        packageName = selectedPaymentOption.toString(),
                        initiateMandatePaymentRequest = initiateMandatePaymentRequest!!,
                        fetchPhonePeVersionCode = {
                            requireContext().getPhonePeVersionCode(MandatePaymentBuildKonfig.PHONEPE_PACKAGE)?.toString()
                        }
                    )
                } else {
                    bottomSheetViewModel.updateDailySaving(args.dsAmount)
                    coreUiApi.openGenericPostActionStatusFragment(
                        GenericPostActionStatusData(
                            postActionStatus = PostActionStatus.ENABLED.name,
                            header = getString(R.string.feature_daily_investment_daily_investment_setup_successfully),
                            headerColorRes = com.jar.app.core_ui.R.color.color_1EA787,
                            title = getString(
                                R.string.feature_daily_investment_x_will_be_auto_saved_starting_tomorrow,
                                args.dsAmount.orZero().toInt()
                            ),
                            titleColorRes = com.jar.app.core_ui.R.color.white,
                            imageRes = com.jar.app.core_ui.R.drawable.core_ui_ic_tick,
                            headerTextSize = 18f,
                            titleTextSize = 16f
                        )
                    ) {
                        EventBus.getDefault().post(RefreshDailySavingEvent())
                        popBackStack(R.id.setupDailyInvestmentFragment, true)
                    }
                }
                analyticsHandler.postEvent(
                    DailySetupScreen_BSClicked,
                    mapOf(
                        DailySavingsEventKey.MandateAmount to it.getFinalMandateAmount().toString(),
                        DailySavingsEventKey.recommended_bank to viewModel.preferredBankPageItem?.bankName.toString(),
                        DailySavingsEventKey.roundoff_status to if (bottomSheetViewModel.bottomSheetLiveData.value?.data?.data?.mandates?.size.orZero() >= 2) "true" else "false",
                        DailySavingsEventKey.roundoff_mandate_amount to args.dsAmount.toString(),
                        DailySavingsEventKey.available_upi to bottomSheetViewModel.paymentOptionLiveData.value?.size.toString(),
                        DailySavingsEventKey.pre_selected_upi to bottomSheetViewModel.getPreSelectedUpi().orEmpty(),
                        DailySavingsEventKey.selected_upi to selectedPaymentOption.toString(),
                        DailySavingsEventKey.button_type to "Proceed",
                    )
                )
            },
            onError = {
                dismissProgressBar()
                it.snackBar(binding.root)
            }
        )

        bottomSheetViewModel.mandateInfoLiveData.observe(viewLifecycleOwner) {
            mandateStatusAdapter?.submitList(it)
        }

        bottomSheetViewModel.paymentOptionLiveData.observe(viewLifecycleOwner) {
            paymentOptionsAdapter?.submitList(it)
            if (it.isNullOrEmpty().not()) {
                selectedPaymentOption = it!!.first().packageName
            }
            analyticsHandler.postEvent(
                DailySavingsEventKey.DailySetupScreen_BottomScreenShown,
                mapOf(
                    DailySavingsEventKey.MandateAmount to args.dsAmount.toString(),
                    DailySavingsEventKey.recommended_bank to viewModel.preferredBankPageItem?.bankName.toString(),
                    DailySavingsEventKey.roundoff_status to if (bottomSheetViewModel.bottomSheetLiveData.value?.data?.data?.mandates?.size.orZero() >= 2) "true" else "false",
                    DailySavingsEventKey.available_upi to it?.size.toString(),
                    DailySavingsEventKey.pre_selected_upi to selectedPaymentOption.orEmpty()
                )
            )
        }
    }

    override fun onDestroyView() {
        removeInitiateMandatePaymentData() // Remove the data from savedStateHandle
        super.onDestroyView()
    }

}