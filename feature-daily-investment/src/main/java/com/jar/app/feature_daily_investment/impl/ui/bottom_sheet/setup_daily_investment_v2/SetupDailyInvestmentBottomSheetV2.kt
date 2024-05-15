package com.jar.app.feature_daily_investment.impl.ui.bottom_sheet.setup_daily_investment_v2

import android.os.Bundle
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jar.app.base.data.event.SingleHomeFeedBottomSheetProceedBtnEvent
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.app.base.util.addItemDecorationIfNoneAdded
import com.jar.app.base.util.dp
import com.jar.app.base.util.getFormattedAmount
import com.jar.app.base.util.orFalse
import com.jar.app.base.util.setHtmlText
import com.jar.app.base.util.showToast
import com.jar.app.core_base.data.event.RefreshDailySavingEvent
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.orZero
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.core_ui.extension.hideKeyboard
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.core_ui.item_decoration.SpaceItemDecoration
import com.jar.app.feature_coupon_api.domain.model.CouponCode
import com.jar.app.feature_daily_investment.NavigationDailyInvestmentDirections
import com.jar.app.feature_daily_investment.R
import com.jar.app.feature_daily_investment.api.data.DailyInvestmentApi
import com.jar.app.feature_daily_investment.databinding.FeatureSetupDailyInvestmentBottomsheetV2Binding
import com.jar.app.feature_daily_investment.impl.domain.DailySavingsEventKey
import com.jar.app.feature_daily_investment.shared.domain.model.DailyInvestmentBottomSheetV2Data
import com.jar.app.feature_daily_investment.impl.ui.SuggestedAmountAdapter
import com.jar.app.feature_daily_investment.impl.ui.setup_daily_investment.SetupDailyInvestmentFragmentDirections
import com.jar.app.feature_mandate_payment.api.MandatePaymentApi
import com.jar.app.feature_mandate_payment.impl.util.MandatePaymentEventKey
import com.jar.app.feature_mandate_payments_common.shared.domain.model.initiate_mandate.InitiateMandatePaymentRequest
import com.jar.app.feature_mandate_payments_common.shared.domain.model.initiate_mandate.MandateWorkflowType
import com.jar.app.feature_mandate_payments_common.shared.domain.model.payment_page.PaymentPageHeaderDetail
import com.jar.app.feature_mandate_payments_common.shared.domain.model.verify_status.MandatePaymentProgressStatus
import com.jar.app.feature_mandate_payments_common.shared.util.MandatePaymentCommonConstants
import com.jar.app.feature_savings_common.shared.domain.model.SavingsType
import com.jar.internal.library.jar_core_network.api.util.collectUnwrapped
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@AndroidEntryPoint
internal class SetupDailyInvestmentBottomSheetV2 :
    BaseBottomSheetDialogFragment<FeatureSetupDailyInvestmentBottomsheetV2Binding>() {

    @Inject
    lateinit var remoteConfigApi: RemoteConfigApi

    @Inject
    lateinit var coroutineScope: CoroutineScope

    @Inject
    lateinit var mandatePaymentApi: MandatePaymentApi

    @Inject
    lateinit var dailyInvestmentApi: DailyInvestmentApi

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    private var initiatePaymentJob: Job? = null

    private val inputTextWatcher: TextWatcher by lazy {
        binding.etAmount.doAfterTextChanged {
            val inputString = it?.toString().orEmpty().replace(",", "").ifEmpty { null }
            inputString?.toIntOrNull()?.getFormattedAmount()?.let { formattedString ->
                setTextInInputEditText(formattedString, shouldReattachTextWatcher = true)
            }
            viewModel.amount = inputString?.toFloatOrNull().orZero()
            checkForMinimumAndMaximumAmount()
        }
    }

    private val viewModel by activityViewModels<SetupDailyInvestmentBottomSheetV2ViewModel> { defaultViewModelProviderFactory }
    private var adapter: SuggestedAmountAdapter? = null
    private val spaceItemDecoration = SpaceItemDecoration(3.dp, 0.dp)
    private var isRoundOffsEnabled = false
    private var isMandateSetupFlow = false


    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureSetupDailyInvestmentBottomsheetV2Binding
        get() = FeatureSetupDailyInvestmentBottomsheetV2Binding::inflate
    override val bottomSheetConfig: BottomSheetConfig
        get() = BottomSheetConfig(
            isCancellable = false,
            isDraggable = false,
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        setStyle(DialogFragment.STYLE_NORMAL, com.jar.app.core_ui.R.style.BottomSheetDialogInput)
        getData()
        super.onCreate(savedInstanceState)
    }

    override fun onDestroy() {
        initiatePaymentJob?.cancel()
        super.onDestroy()
    }

    override fun setup() {
        setupUI()
        observeFlow()
        setupListeners()
    }

    private fun getData() {
        viewModel.fetchSuggestedAmount()
        viewModel.fetchScreenStaticData()
        viewModel.fetchUserRoundOffDetails()
    }

    private fun setupListeners() {
        binding.btnClose.setDebounceClickListener {
            dismissAllowingStateLoss()
        }

        binding.btnProceed.setDebounceClickListener {
            if (viewModel.amount != 0f) {
                if (viewModel.amount <= viewModel.maxDSAmount) {
                    EventBus.getDefault().post(SingleHomeFeedBottomSheetProceedBtnEvent())
                    viewModel.isAutoPayResetRequired(viewModel.amount)
                } else {
                    getString(
                        R.string.feature_daily_investment_max_amount_cannot_be_more_than_rs_x,
                        viewModel.maxDSAmount
                    ).snackBar(binding.root)
                }
            } else {
                getString(R.string.feature_daily_investment_please_enter_the_valid_amount).snackBar(
                    binding.root
                )
            }
        }
    }

    private fun observeFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.dsSuggestedAmountDetails.collectUnwrapped(onLoading = {
                    showProgressBar()
                }, onSuccess = {
                    dismissProgressBar()
                    viewModel.createRvListData(it.data)
                    viewModel.maxDSAmount = it.data.sliderMaxValue
                    viewModel.minDSAmount = it.data.sliderMinValue
                    viewModel.recommendedAmount =
                        viewModel.dsSuggestedAmountDetails.value.data?.data?.recommendedSubscriptionAmount.orZero()
                    viewModel.fetchCouponCodes(BaseConstants.BuyGoldFlowContext.SINGLE_PAGE_HOME_FEED_COUPON)
                }, onError = { _, _ ->
                    dismissProgressBar()
                })
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.rVFlowData.collectLatest {
                    adapter?.submitList(it)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.screenStaticDataFlow.collectUnwrapped(onLoading = {
                    showProgressBar()
                }, onSuccess = {
                    dismissProgressBar()
                    setUiStaticDetails(it.data)
                }, onError = { _, _ ->
                    dismissProgressBar()
                })
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.couponCodesFlow.collectLatest {
                    val couponList = it.data
                    if (couponList.isNullOrEmpty()) {
                        couponUiDetails(it.data?.getOrNull(0))
                        binding.couponLayout.visibility = View.GONE
                    } else {
                        binding.couponLayout.visibility = View.VISIBLE
                        couponUiDetails(it.data?.getOrNull(0))
                        viewModel.couponCodeId = it.data?.getOrNull(0)?.couponCodeId
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.roundOffDetailsFlow.collectUnwrapped(onLoading = {
                    showProgressBar()
                }, onSuccess = {
                    dismissProgressBar()
                    isRoundOffsEnabled =
                        it.data.enabled.orFalse() && it.data.autoSaveEnabled.orFalse()
                }, onError = { _, _ ->
                    dismissProgressBar()
                })
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.updateDailySavingStatusFlow.collectUnwrapped(onLoading = {
                    showProgressBar()
                }, onSuccess = {
                    dismissProgressBar()
                    EventBus.getDefault().post(RefreshDailySavingEvent())
                    binding.etAmount.hideKeyboard()
                    dismissAllowingStateLoss()
                }, onError = { _, _ ->
                    dismissProgressBar()
                })
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isAutoPayResetRequiredFlow.collectUnwrapped(onLoading = {
                    showProgressBar()
                }, onSuccess = {
                    dismissProgressBar()
                    if (it.data.isResetRequired) {
                        if (isRoundOffsEnabled) navigateTo(
                            NavigationDailyInvestmentDirections.actionToPreDailyInvestmentAutopaySetupFragment(
                                BaseConstants.DSPreAutoPayFlowType.SETUP_DS, viewModel.amount
                            )
                        )
                        else if (remoteConfigApi.isMandateBottomSheetExperimentRunning()) {
                            initiatePaymentJob?.cancel()
                            initiatePaymentJob = coroutineScope.launch(Dispatchers.Main) {
                                mandatePaymentApi.initiateMandatePayment(
                                    paymentPageHeaderDetails = PaymentPageHeaderDetail(
                                        toolbarHeader = getString(R.string.feature_daily_savings),
                                        toolbarIcon = R.drawable.feature_daily_investment_ic_daily_saving_tab,
                                        title = getString(R.string.feature_daily_investment_auto_save),
                                        featureFlow = MandatePaymentEventKey.FeatureFlows.SetupDailySaving,
                                        userLifecycle = BaseConstants.SinglePageHomeFeed,
                                        savingFrequency = MandatePaymentEventKey.SavingFrequencies.Daily,
                                        mandateSavingsType = MandatePaymentCommonConstants.MandateStaticContentType.DAILY_SAVINGS_MANDATE_EDUCATION,
                                        bestAmount = viewModel.dsSuggestedAmountDetails.value.data?.data?.recommendedSubscriptionAmount?.toInt()
                                    ),
                                    initiateMandatePaymentRequest = InitiateMandatePaymentRequest(
                                        mandateAmount = viewModel.amount,
                                        authWorkflowType = MandateWorkflowType.TRANSACTION,
                                        subscriptionType = SavingsType.DAILY_SAVINGS.name,
                                        couponCodeId = if (viewModel.amount.toInt() >= viewModel.couponCodeMinimumAmount) viewModel.couponCodeId
                                        else null
                                    )
                                ).collectUnwrapped(onSuccess = { it ->
                                    if (it.second.getAutoInvestStatus() == MandatePaymentProgressStatus.SUCCESS) {
                                        viewModel.enableOrUpdateDailySaving(viewModel.amount)
                                        isMandateSetupFlow = true
                                    }
                                    EventBus.getDefault()
                                        .post(RefreshDailySavingEvent(isSetupFlow = true))
                                    dailyInvestmentApi.openDailySavingSetupStatusFragment(
                                        dailySavingAmount = viewModel.amount,
                                        fetchAutoInvestStatusResponse = it.second,
                                        mandatePaymentResultFromSDK = it.first,
                                        flowName = BaseConstants.SinglePageHomeFeed,
                                        popUpToId = R.id.setupDailyInvestmentFragment,
                                        isMandateBottomSheetFlow = true,
                                        //We can update this once we start using this in actual flows
                                        userLifecycle = "SinglePageHomeFeed"
                                    )
                                }, onError = { errorMessage, _ ->
                                    if (errorMessage.isNotBlank()) requireContext().showToast(
                                        errorMessage
                                    )
                                })
                            }
                        } else navigateTo(
                            SetupDailyInvestmentFragmentDirections.actionSetupDailyInvestmentFragmentToAutoPayRedirectionFragment(
                                dailySavingAmount = viewModel.amount,
                                goldVolume = viewModel.getFinalVolume(viewModel.amount),
                                mandateAmount = it.data.getFinalMandateAmount()
                            )
                        )
                    } else {
                        viewModel.enableOrUpdateDailySaving(viewModel.amount)
                    }
                }, onError = { _, _ ->
                    dismissProgressBar()
                })
            }
        }
    }

    private fun setUiStaticDetails(dailyInvestmentBottomSheetV2Data: DailyInvestmentBottomSheetV2Data?) {
        if (dailyInvestmentBottomSheetV2Data != null) {
            binding.tvHeading.text =
                dailyInvestmentBottomSheetV2Data.sphBottomSheetStaticData.title
            binding.tvSubHeading.text =
                dailyInvestmentBottomSheetV2Data.sphBottomSheetStaticData.description
            binding.btnProceed.setText(dailyInvestmentBottomSheetV2Data.sphBottomSheetStaticData.buttonText)
        }
    }

    private fun couponUiDetails(couponCode: CouponCode?) {
         if (couponCode == null) {
             if (viewModel.amount != 0f) {
                 binding.etAmount.setText(viewModel.amount.toInt().toString())
             } else {
                 binding.etAmount.setText(viewModel.recommendedAmount.toInt().toString())
             }
        } else if (viewModel.amount == 0f) {
            viewModel.amount = couponCode.minimumAmount.orZero()
            binding.etAmount.setText(couponCode.minimumAmount.toInt().toString())
        } else {
             binding.etAmount.setText(viewModel.amount.toInt().toString())
        }
        viewModel.couponCodeMinimumAmount = couponCode?.minimumAmount?.toInt().orZero()
        binding.tvCouponHeading.text = couponCode?.title.orEmpty()
        binding.tvCouponType.text = BaseConstants.COUPON
        binding.tvCouponSubHeading.setHtmlText(couponCode?.description.orEmpty())
    }

    private fun toggleInputState(
        shouldSetDisableState: Boolean, shouldIgnoreErrorUI: Boolean = false
    ) {
        binding.amountEditBoxHolder.setBackgroundResource(
            if (shouldSetDisableState && shouldIgnoreErrorUI.not()) com.jar.app.feature_buy_gold_v2.R.drawable.feature_buy_gold_v2_bg_rounded_2e2942_outline_eb6a6e_16dp
            else com.jar.app.feature_buy_gold_v2.R.drawable.feature_buy_gold_v2_bg_input
        )
        binding.enteredAmountError.isVisible = shouldSetDisableState && shouldIgnoreErrorUI.not()

        binding.btnProceed.setDisabled(shouldSetDisableState)
    }

    private fun setTextInInputEditText(text: String, shouldReattachTextWatcher: Boolean = false) {
        if (binding.etAmount.text.toString() != text) {
            if (shouldReattachTextWatcher) {
                binding.etAmount.removeTextChangedListener(inputTextWatcher)
            }
            binding.etAmount.setText(text)
            binding.etAmount.setSelection(
                binding.etAmount.text.toString().trim().length
            )
            if (shouldReattachTextWatcher) {
                binding.etAmount.addTextChangedListener(inputTextWatcher)
            }
        }
        binding.etAmount.gravity = Gravity.LEFT
    }

    private fun setupUI() {
        binding.etAmount.addTextChangedListener(inputTextWatcher)

        binding.rvSuggestedAmounts.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        adapter = SuggestedAmountAdapter {
            if (it.recommended) {
                viewModel.popularAmount = it.amount
            }
            binding.etAmount.setText("${it.amount.toInt()}")
            viewModel.amount = it.amount
            binding.etAmount.setSelection(binding.etAmount.text?.length.orZero())
            analyticsHandler.postEvent(
                DailySavingsEventKey.DailySavings_AmountBSClicked,
                mapOf(DailySavingsEventKey.amount to it.amount.toString())
            )
        }
        binding.rvSuggestedAmounts.addItemDecorationIfNoneAdded(spaceItemDecoration)
        binding.rvSuggestedAmounts.adapter = adapter

        binding.etAmount.setSelection(binding.etAmount.text?.length.orZero())

        if (viewModel.amount == 0f) {
            binding.enteredAmountError.text = getString(
                R.string.feature_daily_investment_amount_should_be_more_than_x,
                viewModel.minDSAmount.toInt()
            )
            toggleInputState(shouldSetDisableState = true)
        }
    }

    private fun checkForMinimumAndMaximumAmount() {
        when {
            viewModel.amount < viewModel.minDSAmount -> {
                binding.enteredAmountError.text = getString(
                    R.string.feature_daily_investment_amount_should_be_more_than_x,
                    viewModel.minDSAmount.toInt()
                )
                toggleInputState(shouldSetDisableState = true)
            }

            viewModel.amount > viewModel.maxDSAmount -> {
                binding.enteredAmountError.text = getString(
                    R.string.feature_daily_investment_max_amount_cannot_be_more_than_rs_x,
                    viewModel.maxDSAmount.toInt()
                )
                toggleInputState(shouldSetDisableState = true)
            }

            else -> {
                toggleInputState(shouldSetDisableState = false)
            }
        }
    }
}