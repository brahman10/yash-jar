package com.jar.app.feature_buy_gold_v2.impl.ui.buy_gold_bottomsheet

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
import com.bumptech.glide.Glide
import com.jar.app.base.data.event.RefreshCouponDiscoverEvent
import com.jar.app.base.data.event.SingleHomeFeedBottomSheetProceedBtnEvent
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.app.base.util.DispatcherProvider
import com.jar.app.base.util.addItemDecorationIfNoneAdded
import com.jar.app.base.util.dp
import com.jar.app.base.util.getFormattedAmount
import com.jar.app.base.util.setHtmlText
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.orZero
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.core_ui.item_decoration.SpaceItemDecoration
import com.jar.app.core_ui.widget.PriceValidityTimer
import com.jar.app.feature_buy_gold_v2.R
import com.jar.app.feature_buy_gold_v2.api.BuyGoldV2Api
import com.jar.app.feature_buy_gold_v2.databinding.FeatureBuyGoldV2BottomsheetBinding
import com.jar.app.feature_buy_gold_v2.impl.ui.suggested_amount.SuggestedGoldAmountAdapter
import com.jar.app.feature_buy_gold_v2.shared.MR
import com.jar.app.feature_buy_gold_v2.shared.domain.model.BuyGoldBottomSheetV2Data
import com.jar.app.feature_buy_gold_v2.shared.domain.model.BuyGoldByAmountRequest
import com.jar.app.feature_buy_gold_v2.shared.domain.use_case.impl.BuyGoldRequestType
import com.jar.app.feature_buy_gold_v2.shared.util.BuyGoldV2Constants
import com.jar.app.feature_buy_gold_v2.shared.util.BuyGoldV2EventKey
import com.jar.app.feature_coupon_api.domain.model.CouponCode
import com.jar.app.feature_one_time_payments.shared.data.model.base.InitiatePaymentResponse
import com.jar.app.feature_one_time_payments_common.shared.FetchManualPaymentStatusResponse
import com.jar.app.feature_payment.api.PaymentManager
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jar_core_network.api.util.collectUnwrapped
import com.jar.internal.library.jar_core_network.api.util.orFalse
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
internal class BuyGoldV2BottomSheet :
    BaseBottomSheetDialogFragment<FeatureBuyGoldV2BottomsheetBinding>() {
    @Inject
    lateinit var remoteConfigManager: RemoteConfigApi

    @Inject
    lateinit var paymentManager: PaymentManager

    @Inject
    lateinit var coroutineScope: CoroutineScope

    @Inject
    lateinit var dispatcherProvider: DispatcherProvider

    @Inject
    lateinit var serializer: Serializer

    @Inject
    lateinit var buyGoldApi: BuyGoldV2Api

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    private var initiatePaymentJob: Job? = null

    private var suggestedGoldAmountAdapter: SuggestedGoldAmountAdapter? = null

    private val spaceItemDecoration = SpaceItemDecoration(3.dp, 0.dp)

    private val couponCodeName by lazy {
        BuyGoldV2Constants.NO_CODE
    }

    private var isFirstTimeGoldFetched = true
    private var isTimerFinished = false


    private val inputTextWatcher: TextWatcher by lazy {
        binding.etAmount.doAfterTextChanged {
            val inputString = it?.toString().orEmpty().replace(",", "").ifEmpty { null }
            inputString?.toIntOrNull()?.getFormattedAmount()?.let { formattedString ->
                setTextInInputEditText(formattedString, shouldReattachTextWatcher = true)
            }
            viewModel.buyAmount = inputString?.toFloatOrNull().orZero()
            checkForMinimumAmountAndVolumeValue()
        }
    }

    private val viewModel by activityViewModels<BuyGoldV2BottomSheetViewModel> { defaultViewModelProviderFactory }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureBuyGoldV2BottomsheetBinding
        get() = FeatureBuyGoldV2BottomsheetBinding::inflate
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
        setupListeners()
        setupSuggestedGoldAmountAdapter()
        observeSuggestedGoldAmountData(WeakReference(binding.root))
        observeCurrentBuyPriceLiveData(WeakReference(binding.root))
    }

    private fun setupListeners() {
        binding.btnClose.setDebounceClickListener {
            dismissAllowingStateLoss()
        }

        binding.etAmount.addTextChangedListener(inputTextWatcher)

        binding.amountEditBoxHolder.setOnFocusChangeListener { _, focus ->
            if (lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                if (focus) {
                    binding.amountEditBoxHolder.setBackgroundResource(R.drawable.feature_buy_gold_v2_bg_input)
                } else {
                    binding.amountEditBoxHolder.setBackgroundResource(com.jar.app.core_ui.R.drawable.core_ui_round_black_bg_16dp)
                }
            }
        }

        binding.btnBuyNow.setDebounceClickListener {
            EventBus.getDefault().post(SingleHomeFeedBottomSheetProceedBtnEvent())
            initiateBuyGoldRequest()
        }
    }

    private fun initiateBuyGoldRequest() {
        val currentGoldPrice = viewModel.fetchCurrentBuyPriceResponse
        val couponCodeId = viewModel.couponCodeList?.firstOrNull()?.couponCodeId
        val couponCode = if (viewModel.couponCodeList.isNullOrEmpty()) {
            null
        } else {
            viewModel.couponCodeList?.get(0)?.couponCode
        }

        if (viewModel.buyAmount != 0.0f && currentGoldPrice != null) {
            viewModel.buyGoldByAmount(
                BuyGoldByAmountRequest(
                    amount = viewModel.buyAmount,
                    fetchCurrentGoldPriceResponse = currentGoldPrice,
                    auspiciousTimeId = null,
                    couponCodeId = if (viewModel.buyAmount.toInt() >= viewModel.couponCodeMinimumAmount) couponCodeId
                    else null,
                    couponCode = couponCode,
                    paymentGateway = paymentManager.getCurrentPaymentGateway(),
                    weeklyChallengeFlow = false
                )
            )
        }
    }


    private fun getData() {
        viewModel.fetchSuggestedAmount(couponCodeName, BaseConstants.DailySavingUpdateFlow.QUICK_ACTIONS)
        viewModel.fetchCurrentGoldBuyPrice()
        viewModel.fetchBuyGoldBottomSheetV2Data()
    }

    private fun couponUiDetails(couponCode: CouponCode?) {
         if (couponCode == null) {
             if (viewModel.buyAmount != 0f) {
                 binding.etAmount.setText(viewModel.buyAmount.toInt().toString())
             } else {
                 binding.etAmount.setText(viewModel.popularAmount.toInt().toString())
             }
        } else if (viewModel.buyAmount == 0f) {
            viewModel.buyAmount = couponCode.minimumAmount.orZero()
            binding.etAmount.setText(viewModel.buyAmount.toInt().toString())
        } else {
            binding.etAmount.setText(viewModel.buyAmount.toInt().toString())
        }
        viewModel.couponCodeMinimumAmount = couponCode?.minimumAmount?.toInt().orZero()
        binding.couponHeading.text = couponCode?.title.orEmpty()
        binding.tvCouponType.text = BaseConstants.COUPON
        binding.couponSubHeading.setHtmlText(couponCode?.description.orEmpty())
    }

    private fun setUiStaticDetails(buyGoldBottomSheetV2Data: BuyGoldBottomSheetV2Data?) {
        if (buyGoldBottomSheetV2Data != null) {
            binding.tvHeading.text = buyGoldBottomSheetV2Data.sphBottomSheetStaticData.title
            binding.tvSubHeading.text =
                buyGoldBottomSheetV2Data.sphBottomSheetStaticData.description
            binding.btnBuyNow.setText(buyGoldBottomSheetV2Data.sphBottomSheetStaticData.buttonText)
            Glide.with(requireContext())
                .load(buyGoldBottomSheetV2Data.sphBottomSheetStaticData.icon)
                .into(binding.ivSubHeadingGoldBarLogo)

            if (viewModel.buyAmount == 0f) {
                binding.enteredAmountError.text = getCustomStringFormatted(
                    MR.strings.feature_buy_gold_v2_please_enter_a_amount_thats_more_than_x_rs,
                    remoteConfigManager.getMinimumGoldBuyAmount().toInt() - 1
                )
                toggleInputState(shouldSetDisableState = true)
            }
        }
    }

    private fun getMinutesFromMillis(millis: Long) = TimeUnit.MILLISECONDS.toMinutes(millis)


    private fun observeSuggestedGoldAmountData(weakReference: WeakReference<View>) {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.suggestedAmountFlow.collect(onSuccess = {
                    suggestedGoldAmountAdapter?.submitList(it?.suggestedAmount?.options)
                    viewModel.fetchCouponCodes(BaseConstants.BuyGoldFlowContext.SINGLE_PAGE_HOME_FEED_COUPON)
                }, onError = { errorMessage, _ ->
                    errorMessage.snackBar(weakReference.get()!!)
                })
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.screenStaticDataFlow.collectLatest {
                    setUiStaticDetails(it.data?.data)
                }
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
                        val couponCode =  it.data?.getOrNull(0)
                        binding.couponLayout.isVisible = couponCode != null
                        couponUiDetails(it.data?.getOrNull(0))
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.buyGoldFlow.collect(onLoading = {
                    showProgressBar()
                }, onSuccess = {
                    it?.let {
                        dismissProgressBar()
                        EventBus.getDefault().post(RefreshCouponDiscoverEvent())
                        initiatePayment(it.copy(screenSource = BuyGoldV2EventKey.SinglePageHomeScreenFlow))
                    }
                }, onError = { errorMessage, errorCode ->
                    dismissProgressBar()
                    errorMessage.snackBar(weakReference.get()!!)
                    if (errorCode == BaseConstants.ErrorCode.INVALID_BUY_PRICE_EXCEPTION) {
                        viewModel.fetchCurrentGoldBuyPrice()
                    }
                })
            }
        }
    }

    private fun initiatePayment(initiatePaymentResponse: InitiatePaymentResponse) {
        initiatePaymentJob?.cancel()
        initiatePaymentJob = coroutineScope.launch(dispatcherProvider.main) {
            paymentManager.initiateOneTimePayment(initiatePaymentResponse)
                .collectUnwrapped(onSuccess = {
                    coroutineScope.launch(dispatcherProvider.main) {
                        uiScope.launch {
                            dismissProgressBar()
                        }
                        navigateToPaymentScreen(it)
                    }
                })
        }
    }

    private fun navigateToPaymentScreen(
        fetchManualPaymentStatusResponse: FetchManualPaymentStatusResponse? = null,
    ) {
        fetchManualPaymentStatusResponse?.transactionId?.let { transactionId ->
            fetchManualPaymentStatusResponse.paymentProvider?.let { paymentProvider ->
                buyGoldApi.openOrderStatusFlow(
                    transactionId,
                    paymentProvider,
                    BaseConstants.ManualPaymentFlowType.SinglePageHomeScreenFlow,
                    fetchManualPaymentStatusResponse.oneTimeInvestment.orFalse(),
                    BaseConstants.BuyGoldFlowContext.SINGLE_PAGE_HOMEFEED
                )
            }
        }
    }


    private fun toggleInputState(
        shouldSetDisableState: Boolean, shouldIgnoreErrorUI: Boolean = false
    ) {
        binding.amountEditBoxHolder.setBackgroundResource(
            if (shouldSetDisableState && shouldIgnoreErrorUI.not()) R.drawable.feature_buy_gold_v2_bg_rounded_2e2942_outline_eb6a6e_16dp
            else R.drawable.feature_buy_gold_v2_bg_input
        )
        binding.enteredAmountError.isVisible = shouldSetDisableState && shouldIgnoreErrorUI.not()

        binding.btnBuyNow.setDisabled(shouldSetDisableState)
    }

    private fun checkForMinimumAmountAndVolumeValue() {
        when {
            viewModel.buyAmount < remoteConfigManager.getMinimumGoldBuyAmount() -> {
                binding.enteredAmountError.text = getCustomStringFormatted(
                    MR.strings.feature_buy_gold_v2_please_enter_a_amount_thats_more_than_x_rs,
                    remoteConfigManager.getMinimumGoldBuyAmount().toInt() - 1
                )
                toggleInputState(shouldSetDisableState = true)
            }

            viewModel.buyAmount > remoteConfigManager.getMaximumGoldBuyAmount() -> {
                binding.enteredAmountError.text = getCustomStringFormatted(
                    MR.strings.feature_buy_gold_v2_please_enter_a_amount_thats_less_than_x_rs,
                    remoteConfigManager.getMaximumGoldBuyAmount().toInt() + 1
                )
                toggleInputState(shouldSetDisableState = true)
            }

            else -> {
                toggleInputState(shouldSetDisableState = false)
            }
        }
    }

    private fun observeCurrentBuyPriceLiveData(weakReference: WeakReference<View>) {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.currentGoldBuyPriceFlow.collect(onLoading = {
                    if (!isFirstTimeGoldFetched) {
                        binding.goldPriceProgressLayout.setTimerState(PriceValidityTimer.TimerState.FETCHING_PRICE)
                    }
                    binding.btnBuyNow.isClickable = false
                }, onSuccess = {
                    isFirstTimeGoldFetched = false
                    binding.goldPriceProgressLayout.shouldShowLiveTag(true)
                    binding.goldPriceProgressLayout.setTimerState(PriceValidityTimer.TimerState.NEW_PRICE_FETCHED)
                    uiScope.launch {
                        delay(1000)
                        binding.btnBuyNow.isClickable = true
                        binding.goldPriceProgressLayout.start(
                            livePriceMessage = getCustomStringFormatted(
                                MR.strings.feature_buy_gold_v2_buy_price, it.price
                            ),
                            validityInMillis = it.getValidityInMillis(),
                            uiScope = uiScope,
                            onInterval = {
                                val remainingTimeInMinutes = getMinutesFromMillis(it)
                                if (remainingTimeInMinutes >= 3) {
                                    binding.goldPriceProgressLayout.setProgressBarTintColor(com.jar.app.core_ui.R.color.color_335665)
                                } else {
                                    binding.goldPriceProgressLayout.setProgressBarTintColor(com.jar.app.core_ui.R.color.color_EF8A8A_opacity_30)
                                }
                            },
                            onFinish = {
                                isTimerFinished = true
                                viewModel.fetchCurrentGoldBuyPrice()
                            },
                        )
                        if (isTimerFinished) {
                            isTimerFinished = false
                        }
                    }
                }, onError = { errorMessage, _ ->
                    isFirstTimeGoldFetched = false
                    errorMessage.snackBar(weakReference.get()!!)
                })
            }
        }
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
        binding.etAmount.gravity =
            if (viewModel.buyGoldRequestType == BuyGoldRequestType.AMOUNT) Gravity.LEFT else Gravity.RIGHT
    }

    private fun setupSuggestedGoldAmountAdapter() {
        suggestedGoldAmountAdapter = SuggestedGoldAmountAdapter {
            setTextInInputEditText("${it.amount.toInt()}")
            if (it.recommended == true) {
                viewModel.popularAmount = it.amount
            }
            analyticsHandler.postEvent(
                BuyGoldV2EventKey.BuyGold_AmountBSClicked,
                mapOf(BuyGoldV2EventKey.Amount to it.amount.toString())
            )
        }

        binding.rvSuggestedAmounts.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        binding.rvSuggestedAmounts.addItemDecorationIfNoneAdded(spaceItemDecoration)
        binding.rvSuggestedAmounts.adapter = suggestedGoldAmountAdapter
    }
}