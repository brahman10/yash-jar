package com.jar.app.feature_gold_lease.impl.ui.order_details

import android.animation.ObjectAnimator
import android.graphics.Typeface
import android.os.Bundle
import android.text.InputFilter
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.*
import com.jar.app.core_base.util.BaseAppDeeplink
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.orZero
import com.jar.app.core_base.util.roundUp
import com.jar.app.core_ui.extension.keyboardVisibilityChanges
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.core_ui.item_decoration.SpaceItemDecoration
import com.jar.app.core_utils.data.DecimalDigitsInputFilter
import com.jar.app.core_utils.data.RoundAmountToIntInputFilter
import com.jar.app.feature_buy_gold_v2.impl.ui.suggested_amount.SuggestedGoldAmountAdapter
import com.jar.app.feature_buy_gold_v2.shared.domain.use_case.impl.BuyGoldRequestType
import com.jar.app.feature_gold_lease.GoldLeaseNavigationDirections
import com.jar.app.feature_gold_lease.R
import com.jar.app.feature_gold_lease.databinding.FragmentGoldLeaseV2OrderDetailBinding
import com.jar.app.feature_gold_lease.impl.ui.GoldLeaseViewModelAndroid
import com.jar.app.feature_gold_lease.shared.domain.model.GoldLeaseV2GoldOptions
import com.jar.app.feature_gold_lease.shared.domain.model.GoldLeaseV2OrderSummaryArgs
import com.jar.app.feature_gold_lease.shared.domain.model.GoldLeaseV2OrderSummaryScreenData
import com.jar.app.feature_gold_lease.shared.util.GoldLeaseConstants
import com.jar.app.feature_gold_lease.shared.util.GoldLeaseEventKey
import com.jar.app.feature_gold_lease.shared.util.GoldLeaseUtil
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jar_core_network.api.util.collectUnwrapped
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.min

@AndroidEntryPoint
internal class GoldLeaseV2OrderDetailFragment :
    BaseFragment<FragmentGoldLeaseV2OrderDetailBinding>() {

    @Inject
    lateinit var goldLeaseUtil: GoldLeaseUtil

    @Inject
    lateinit var serializer: Serializer

    @Inject
    lateinit var analyticsApi: AnalyticsApi

    private var suggestedGoldAmountAdapter: SuggestedGoldAmountAdapter? = null

    private val spaceItemDecoration = SpaceItemDecoration(3.dp, 0.dp)

    private val goldLeaseViewModelProvider by viewModels<GoldLeaseViewModelAndroid> { defaultViewModelProviderFactory }

    private val goldLeaseViewModel by lazy {
        goldLeaseViewModelProvider.getInstance()
    }

    private val viewModelProvider by viewModels<GoldLeaseV2OrderDetailViewModelAndroid> { defaultViewModelProviderFactory }

    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }

    private val args by navArgs<GoldLeaseV2OrderDetailFragmentArgs>()

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentGoldLeaseV2OrderDetailBinding
        get() = FragmentGoldLeaseV2OrderDetailBinding::inflate

    private var isInputTypeSet = false

    private var isPrefillAmountSyncedOnce = false

    private var isShownEventSynced = false

    companion object {
        const val TEXT_SIZE_ANIMATION_MILLIS = 300L
        const val MIN_TEXT_SIZE_FOR_LABEL = 14F
        const val MAX_TEXT_SIZE_FOR_LABEL = 28F
    }

    private var textSizeIncreaseAnimation: ObjectAnimator? = null
    private var textSizeDecreaseAnimation: ObjectAnimator? = null

    private var isTimerFinished = false
    private var isFirstTimeGoldFetched = true
    private var isGoldPriceUpdated = false

    private var bestTagJob: Job? = null
    private var suggestedAmountOptions: GoldLeaseV2GoldOptions? = null

    private val inputTextWatcher: TextWatcher by lazy {
        binding.etBuyGoldInput.doAfterTextChanged {
            if (goldLeaseViewModel.buyGoldRequestType == BuyGoldRequestType.AMOUNT) {
                val inputString = it?.toString().orEmpty().replace(",", "").ifEmpty { null }
                inputString?.toIntOrNull()?.getFormattedAmount()?.let { formattedString ->
                    setTextInInputEditText(formattedString, shouldReattachTextWatcher = true)
                }
                goldLeaseViewModel.buyAmount = getRawAmount()?.toFloatOrNull().orZero()
            } else {
                goldLeaseViewModel.buyVolume = it?.toString()?.toFloatOrNull().orZero()
            }
            checkForMinimumAmountAndVolumeValue()
        }
    }

    override fun setupAppBar() {
        EventBus.getDefault().post(
            UpdateAppBarEvent(
                AppBarData(
                    ToolbarNone
                )
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getData()
    }

    override fun setup(savedInstanceState: Bundle?) {
        observeLiveData()
        setupUI()
        setupListeners()
        checkForKeyboardState()
    }

    private fun setupUI() {
        setupToolbar()
        binding.btnProceed.setDisabled(true)

        suggestedGoldAmountAdapter = SuggestedGoldAmountAdapter {
            if (it.unit != null && it.unit.orEmpty().contains(GoldLeaseConstants.UNIT_GM)) {
                setTextInInputEditText(it.amount.volumeToStringWithoutTrailingZeros())
            } else {
                setTextInInputEditText("${it.amount.toInt()}")
            }
            postClickEvent(GoldLeaseEventKey.Values.OPTION_PILL, it.amount.orZero())
        }
        binding.rvSuggestedGoldAmount.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        binding.rvSuggestedGoldAmount.addItemDecorationIfNoneAdded(spaceItemDecoration)
        binding.rvSuggestedGoldAmount.adapter = suggestedGoldAmountAdapter

        Glide.with(requireContext()).load(args.leasePlan.jewellerIcon.orEmpty())
            .into(binding.ivJewellerIcon)
        binding.tvJewellerName.setHtmlText(args.leasePlan.jewellerName.orEmpty())
        binding.tvJewellerEst.setHtmlText(args.leasePlan.jewellerEstablishedText.orEmpty())
        binding.tvJarBonusTag.isVisible = args.leasePlan.bonusPercentage.orZero() != 0.0f
        val earningsText = if (args.leasePlan.bonusPercentage.orZero() != 0.0f) {
            requireContext().getFormattedTextForXStringValues(
                R.string.feature_gold_lease_x_earnings_plus_y_bonus_bold,
                listOf(
                    args.leasePlan.earningsPercentage.orZero().toString(),
                    args.leasePlan.bonusPercentage.orZero().toString()
                )
            )
        } else {
            requireContext().getFormattedTextForOneStringValue(
                R.string.feature_gold_lease_x_earnings,
                args.leasePlan.earningsPercentage.orZero().toString()
            )
        }
        binding.tvEarningsPercent.text = earningsText
        binding.tvEarningsPercent.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            setMargins(0, 0, if (args.leasePlan.bonusPercentage.orZero() != 0.0f) 4.dp else 0, 0)
        }

        suggestedAmountOptions?.let {
            changeInputType(goldLeaseViewModel.buyGoldRequestType)
        }
    }

    private fun checkForKeyboardState() {
        uiScope.launch {
            binding.root.keyboardVisibilityChanges().collectLatest { isKeyboardShowing ->
                toggleUIWhenKeyboardOpen(isKeyboardShowing.not())
            }
        }
    }

    private fun getData() {
        goldLeaseViewModel.fetchUserGoldBalance()
        goldLeaseViewModel.fetchCurrentGoldBuyPrice()
    }

    private fun observeLiveData() {
        val weakReference: WeakReference<View> = WeakReference(binding.root)

        observeCurrentBuyGoldPriceLiveData(weakReference)
        observeGoldLeaseGoldOptionsLiveData(weakReference)
        observeAmountFromVolumeLiveData(weakReference)
        observeVolumeFromAmountLiveData(weakReference)
        observeUserGoldBalanceLiveData(weakReference)
    }

    private fun observeUserGoldBalanceLiveData(viewWeakRef: WeakReference<View>) {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                goldLeaseViewModel.goldBalanceFlow.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        setGoldLeaseVolumeAndEarningsAndTotal()
                        viewModel.fetchGoldLeaseGoldOptions(args.leasePlan.planId.orEmpty())
                    },
                    onSuccessWithNullData = {
                        dismissProgressBar()
                    },
                    onError = { errorMessage, _ ->
                        dismissProgressBar()
                        errorMessage.snackBar(viewWeakRef.get()!!)
                    }
                )
            }
        }
    }

    private fun observeAmountFromVolumeLiveData(weakReference: WeakReference<View>) {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                goldLeaseViewModel.amountFromVolumeFlow.collectUnwrapped(
                    onSuccess = {
                        if (goldLeaseViewModel.buyGoldRequestType == BuyGoldRequestType.AMOUNT) {
                            setTextInInputEditText(it.getFormattedAmount())
                        } else {
                            binding.tvRupeeSymbol.text = getString(
                                com.jar.app.feature_buy_gold_v2.shared.R.string.feature_buy_gold_v2_rupees_x_string,
                                it.roundUp(2).getFormattedAmount(shouldRemoveTrailingZeros = true)
                            )
                        }
                        goldLeaseViewModel.buyAmount = it
                        if (isInputTypeSet.not()) {
                            isInputTypeSet = true
                            if (suggestedAmountOptions == null) {
                                changeInputType(BuyGoldRequestType.VOLUME)
                            } else {
                                changeInputType(goldLeaseViewModel.buyGoldRequestType)
                            }
                        }
                        setBestTagVisibilityInOptionsList()
                        setGoldLeaseVolumeAndEarningsAndTotal()
                    }
                )
            }
        }
    }

    private fun observeVolumeFromAmountLiveData(weakReference: WeakReference<View>) {
        viewLifecycleOwner.lifecycleScope.launch {
            goldLeaseViewModel.volumeFromAmountFlow.collectUnwrapped(
                onSuccess = {
                    if (goldLeaseViewModel.buyGoldRequestType == BuyGoldRequestType.VOLUME) {
                        setTextInInputEditText(it.volumeToStringWithoutTrailingZeros())
                    } else {
                        binding.tvGramSymbol.text = getCustomStringFormatted(
                            com.jar.app.feature_buy_gold_v2.shared.MR.strings.feature_buy_gold_v2_x_gm_string,
                            it.volumeToStringWithoutTrailingZeros()
                        )
                    }
                    goldLeaseViewModel.buyVolume = it
                    setBestTagVisibilityInOptionsList()
                    setGoldLeaseVolumeAndEarningsAndTotal()
                }
            )
        }
    }

    private fun setBestTagVisibilityInOptionsList() {
        bestTagJob?.cancel()
        bestTagJob = uiScope.launch {
            val suggestedValue =
                if (goldLeaseViewModel.buyGoldRequestType == BuyGoldRequestType.VOLUME) {
                    suggestedAmountOptions?.leaseGoldOptionsVolumeList?.find { it.isBestTag.orFalse() }?.amount
                } else {
                    suggestedAmountOptions?.leaseGoldOptionsAmountList?.find { it.isBestTag.orFalse() }?.amount
                }
            val inputValue =
                if (goldLeaseViewModel.buyGoldRequestType == BuyGoldRequestType.VOLUME) goldLeaseViewModel.buyVolume else goldLeaseViewModel.buyAmount
            if (inputValue > suggestedValue.orZero()) {
                if (goldLeaseViewModel.buyGoldRequestType == BuyGoldRequestType.VOLUME) {
                    val newList = suggestedAmountOptions?.leaseGoldOptionsVolumeList?.map {
                        it.copy(
                            isBestTag = false
                        )
                    }
                    suggestedGoldAmountAdapter?.submitList(newList)
                } else {
                    val newList = suggestedAmountOptions?.leaseGoldOptionsAmountList?.map {
                        it.copy(
                            isBestTag = false
                        )
                    }
                    suggestedGoldAmountAdapter?.submitList(newList)
                }
            } else {
                suggestedAmountOptions?.leaseGoldOptionsVolumeList?.find { suggestedOption -> suggestedOption.recommended.orFalse() }
                    ?.let { bestOption ->
                        bestOption.isBestTag = true
                    }
                suggestedAmountOptions?.leaseGoldOptionsAmountList?.find { suggestedOption -> suggestedOption.recommended.orFalse() }
                    ?.let { bestOption ->
                        bestOption.isBestTag = true
                    }
                suggestedAmountOptions?.let {
                    if (goldLeaseViewModel.buyGoldRequestType == BuyGoldRequestType.AMOUNT) suggestedGoldAmountAdapter?.submitList(
                        it.leaseGoldOptionsAmountList
                    ) else suggestedGoldAmountAdapter?.submitList(it.leaseGoldOptionsVolumeList)
                }
            }
        }
    }

    private fun observeGoldLeaseGoldOptionsLiveData(weakReference: WeakReference<View>) {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.goldLeaseGoldOptionsListFlow.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        it?.let {
                            suggestedAmountOptions = it
                            setupOptionsDataInUI(it)
                        }
                    },
                    onError = { errorMessage, _ ->
                        dismissProgressBar()
                        errorMessage.snackBar(weakReference.get()!!)
                    }
                )
            }
        }
    }

    private fun observeCurrentBuyGoldPriceLiveData(weakReference: WeakReference<View>) {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                goldLeaseViewModel.currentGoldBuyPriceFlow.collect(
                    onLoading = {
                        if (!isFirstTimeGoldFetched) {
                            getString(com.jar.app.core_ui.R.string.fetching_new_gold_prices).snackBar(
                                binding.root,
                                com.jar.app.core_ui.R.drawable.ic_filled_information_icon,
                                progressColor = com.jar.app.core_ui.R.color.color_016AE1,
                                duration = 2000,
                                translationY = 0f
                            )
                        }
                        isFirstTimeGoldFetched = false
                        binding.btnProceed.isClickable = false
                    },
                    onSuccess = {
                        binding.btnProceed.isClickable = true
                        binding.goldPriceTimer.setProgressBarTintColor(com.jar.app.core_ui.R.color.color_789BDE_opacity_30)
                        binding.goldPriceTimer.setRootBackground(com.jar.app.core_ui.R.color.color_789BDE_opacity_10)
                        binding.goldPriceTimer.start(
                            livePriceMessage = getString(
                                R.string.feature_gold_lease_live_buy_price,
                                it.price
                            ),
                            validityInMillis = it.getValidityInMillis(),
                            uiScope = uiScope,
                            onFinish = {
                                isTimerFinished = true
                                isGoldPriceUpdated = true
                                goldLeaseViewModel.fetchCurrentGoldBuyPrice()
                            }
                        )
                        fetchUpdatedPriceCallCaseHandling(it.price)
                        goldLeaseViewModel.currentGoldPrice = it.price
                        goldLeaseViewModel.currentGoldTax = it.applicableTax.orZero()

                        if (isGoldPriceUpdated) {
                            //To Update UI whenever new price is fetched
                            setGoldLeaseVolumeAndEarningsAndTotal()
                        }
                    },
                    onError = { _, errorCode ->
                        dismissProgressBar()
                        if (errorCode == BaseConstants.ErrorCode.INVALID_BUY_PRICE_EXCEPTION) {
                            goldLeaseViewModel.fetchCurrentGoldBuyPrice()
                        }
                    }
                )
            }
        }
    }

    private fun fetchUpdatedPriceCallCaseHandling(price: Float) {
        if (isTimerFinished) {
            isTimerFinished = false
            if (goldLeaseViewModel.currentGoldPrice > price) {
                getString(
                    R.string.feature_gold_lease_latest_buy_price_fetched_decreased_by_x,
                    abs(goldLeaseViewModel.currentGoldPrice - price)
                ).snackBar(
                    binding.root,
                    R.drawable.feature_gold_lease_ic_checkmark,
                    progressColor = com.jar.app.core_ui.R.color.color_1EA787
                )
            } else if (goldLeaseViewModel.currentGoldPrice < price) {
                getString(
                    R.string.feature_gold_lease_latest_buy_price_fetched_increased_by_x,
                    abs(goldLeaseViewModel.currentGoldPrice - price)
                ).snackBar(
                    binding.root,
                    R.drawable.feature_gold_lease_ic_checkmark,
                    progressColor = com.jar.app.core_ui.R.color.color_1EA787
                )
            }
        }
    }

    private fun checkForMinimumAmountAndVolumeValue() {
        when (goldLeaseViewModel.buyGoldRequestType) {
            BuyGoldRequestType.VOLUME -> {
                when {
                    goldLeaseViewModel.buyVolume < args.leasePlan.minimumQuantityComponent?.value.toFloatOrZero() -> {
                        /**Subtracting 0.0001 from the minimum buy volume for error message of below limit case,
                         * For Eg : If Minimum Volume is 0.0015, then the error message should be
                         * "Please enter a value greater than 0.0014"
                         * **/
                        val errorMessage = String.format(
                            getString(com.jar.app.feature_buy_gold_v2.shared.R.string.feature_buy_gold_v2_please_enter_a_volume_thats_more_than_x_gm),
                            args.leasePlan.minimumQuantityComponent?.value.toFloatOrZero() - 0.0001f
                        )
                        binding.tvErrorMessage.text = errorMessage
                        postInputErrorMessageEvent(errorMessage)
                        toggleInputState(isError = true)
                    }
                    goldLeaseViewModel.buyVolume > args.leasePlan.maximumLeaseVolume.orZero() -> {
                        /**Adding 0.0001 to the maximum buy volume for error message of beyond limit case,
                         * For Eg : If Maximum Volume is 31.8282, then the error message should be
                         * "Please enter a value less than 31.8283"
                         * **/
                        val errorMessage = getString(
                            com.jar.app.feature_buy_gold_v2.shared.R.string.feature_buy_gold_v2_please_enter_a_volume_thats_less_than_x_gm,
                            args.leasePlan.maximumLeaseVolume.orZero() + 0.0001f
                        )
                        binding.tvErrorMessage.text = errorMessage
                        postInputErrorMessageEvent(errorMessage)
                        toggleInputState(isError = true)
                    }
                    else -> {
                        toggleInputState(isError = false)
                    }
                }
                goldLeaseViewModel.calculateAmountFromVolume(goldLeaseViewModel.buyVolume)
            }
            BuyGoldRequestType.AMOUNT -> {
                when {
                    goldLeaseViewModel.buyAmount < goldLeaseViewModel.getAmountForXVolume(args.leasePlan.minimumQuantityComponent?.value.toFloatOrZero()) -> {
                        val errorMessage = getString(
                            com.jar.app.feature_buy_gold_v2.shared.R.string.feature_buy_gold_v2_please_enter_a_amount_thats_more_than_x_rs,
                            goldLeaseViewModel.getAmountForXVolume(args.leasePlan.minimumQuantityComponent?.value.toFloatOrZero())
                                .toInt()
                        )
                        binding.tvErrorMessage.text = errorMessage
                        postInputErrorMessageEvent(errorMessage)
                        toggleInputState(isError = true)
                    }
                    goldLeaseViewModel.buyAmount > goldLeaseViewModel.getAmountForXVolume(args.leasePlan.maximumLeaseVolume.orZero()) -> {
                        val errorMessage = getString(
                            com.jar.app.feature_buy_gold_v2.shared.R.string.feature_buy_gold_v2_please_enter_a_amount_thats_less_than_x_rs,
                            goldLeaseViewModel.getAmountForXVolume(args.leasePlan.maximumLeaseVolume.orZero())
                                .toInt()
                        )
                        binding.tvErrorMessage.text = errorMessage
                        postInputErrorMessageEvent(errorMessage)
                        toggleInputState(isError = true)
                    }
                    else -> {
                        toggleInputState(isError = false)
                    }
                }
                goldLeaseViewModel.calculateVolumeFromAmount(goldLeaseViewModel.buyAmount)
            }
        }
    }

    private fun postInputErrorMessageEvent(errorMessage: String) {
        if (binding.tvErrorMessage.isVisible.not()) {
            val property =
                if (goldLeaseViewModel.buyGoldRequestType == BuyGoldRequestType.VOLUME) GoldLeaseEventKey.Properties.VALUE_GM else GoldLeaseEventKey.Properties.VALUE_RS
            analyticsApi.postEvent(
                GoldLeaseEventKey.LeaseOrderDetails.Lease_MainScreen_InputError,
                mapOf(
                    property to errorMessage
                )
            )
        }
    }

    private fun toggleInputState(isError: Boolean) {
        binding.clBuyGoldInput.setBackgroundResource(
            if (isError) com.jar.app.feature_buy_gold_v2.R.drawable.feature_buy_gold_v2_bg_rounded_2e2942_outline_eb6a6e_16dp else if (binding.etBuyGoldInput.hasFocus()) com.jar.app.feature_buy_gold_v2.R.drawable.feature_buy_gold_v2_bg_input else com.jar.app.core_ui.R.drawable.core_ui_round_black_bg_16dp
        )
        binding.tvErrorMessage.isVisible = isError
        binding.btnProceed.setDisabled(isError)
        binding.llSwitch.alpha = if (isError) 0.5f else 1f
        binding.tvTabInRupees.isClickable = isError.not()
        binding.tvTabInGrams.isClickable = isError.not()
    }

    private fun setTextInInputEditText(text: String, shouldReattachTextWatcher: Boolean = false) {
        if (binding.etBuyGoldInput.text.toString() != text) {
            if (shouldReattachTextWatcher) {
                binding.etBuyGoldInput.removeTextChangedListener(inputTextWatcher)
            }
            binding.etBuyGoldInput.setText(text)
            binding.etBuyGoldInput.setSelection(
                binding.etBuyGoldInput.text.toString().trim().length
            )
            if (shouldReattachTextWatcher) {
                binding.etBuyGoldInput.addTextChangedListener(inputTextWatcher)
            }
        }
        binding.etBuyGoldInput.gravity =
            if (goldLeaseViewModel.buyGoldRequestType == BuyGoldRequestType.AMOUNT) Gravity.LEFT else Gravity.RIGHT
    }

    private fun setupOptionsDataInUI(goldLeaseV2GoldOptions: GoldLeaseV2GoldOptions) {
        binding.tvGoldLeaseTitle.setHtmlText(goldLeaseV2GoldOptions.leasedQuantityTitle.orEmpty())
        binding.tvGoldLeaseEarningsTitle.setHtmlText(goldLeaseV2GoldOptions.goldEarningsTitle.orEmpty())
        binding.tvLockInText.setHtmlText(
            goldLeaseV2GoldOptions.lockInPeriodText.replace(
                GoldLeaseConstants.VALUE_PLACEHOLDER,
                args.leasePlan.lockInComponent?.value.orEmpty()
            )
        )

        if (isPrefillAmountSyncedOnce.not()) {
            isPrefillAmountSyncedOnce = true
            goldLeaseV2GoldOptions.prefillVolume?.let {
                prefillRecommendedVolume(it)
            } ?: kotlin.run {
                goldLeaseV2GoldOptions.leaseGoldOptionsVolumeList?.find { it.recommended.orFalse() }
                    ?.let {
                        prefillRecommendedVolume(it.amount.orZero())
                    } ?: kotlin.run {
                    prefillRecommendedVolume(args.leasePlan.minimumQuantityComponent?.value.toFloatOrZero())
                }
            }
        }
    }

    private fun setGoldLeaseVolumeAndEarningsAndTotal() {
        binding.clUserGoldBalance.isVisible = goldLeaseViewModel.goldBalance?.volume.orZero() != 0.0f
        val amount = if (binding.switchLocker.isChecked) {
            goldLeaseViewModel.getAmountForXVolume(goldLeaseViewModel.buyVolume - getLockerVolumeToBeUsed())
                .roundUp(2)
        } else if (goldLeaseViewModel.buyGoldRequestType == BuyGoldRequestType.VOLUME) {
            goldLeaseViewModel.getAmountForXVolume(goldLeaseViewModel.buyVolume).roundUp(2)
        } else ceil(goldLeaseViewModel.buyAmount)

        binding.tvPayableAmount.text = getString(
            com.jar.app.feature_buy_gold_v2.shared.R.string.feature_buy_gold_v2_rupees_x_string,
            amount.toDouble().roundOffDecimal().getFormattedAmount()
        )
        binding.tvPayableAmountDescription.setHtmlText(
            suggestedAmountOptions?.amountPayableText.orEmpty().replace(
                GoldLeaseConstants.VALUE_PLACEHOLDER,
                if (binding.switchLocker.isChecked) (goldLeaseViewModel.buyVolume - getLockerVolumeToBeUsed()).volumeToString()
                else goldLeaseViewModel.buyVolume.volumeToString()
            )
        )
        binding.tvUseJarGold.setHtmlText(
            suggestedAmountOptions?.useJarSavingsPrompt.orEmpty().replace(
                GoldLeaseConstants.VALUE_PLACEHOLDER, getLockerVolumeToBeUsed().volumeToString()
            )
        )
        binding.tvGoldLeaseValue.text = goldLeaseViewModel.buyVolume.volumeToString()
        binding.tvGoldLeaseEarningsValue.text = goldLeaseUtil.getGoldYieldWithoutCommittedGold(
            leasePercent = args.leasePlan.earningsPercentage.orZero() + args.leasePlan.bonusPercentage.orZero(),
            goldVolume = goldLeaseViewModel.buyVolume,
            leaseNoOfDays = args.leasePlan.lockInComponent?.value?.toIntOrNull() ?: 0
        ).volumeToString()
    }

    private fun setupListeners() {
        binding.ivJewellerInfo.setDebounceClickListener {
            clearFocus()
            postClickEvent(GoldLeaseEventKey.Values.INFO_ICON)
            args.leasePlan.jewellerId?.let {
                navigateTo(
                    GoldLeaseNavigationDirections.actionToGoldLeaseV2JewellerDetailsBottomSheetFragment(
                        flowType = args.flowType,
                        jewellerId = it
                    )
                )
            }
        }

        binding.etBuyGoldInput.addTextChangedListener(inputTextWatcher)

        binding.tvTabInRupees.setDebounceClickListener {
            postClickEvent(GoldLeaseEventKey.Values.IN_RS)
            if (goldLeaseViewModel.buyGoldRequestType != BuyGoldRequestType.AMOUNT) {
                changeInputType(BuyGoldRequestType.AMOUNT)
            }
        }

        binding.tvTabInGrams.setDebounceClickListener {
            postClickEvent(GoldLeaseEventKey.Values.IN_GM)
            if (goldLeaseViewModel.buyGoldRequestType != BuyGoldRequestType.VOLUME) {
                changeInputType(BuyGoldRequestType.VOLUME)
            }
        }

        binding.switchLocker.setOnCheckedChangeListener { _, _ ->
            clearFocus()
            setGoldLeaseVolumeAndEarningsAndTotal()
            postClickEvent(GoldLeaseEventKey.Values.LOCKER_TOGGLE)
        }

        binding.etBuyGoldInput.setOnFocusChangeListener { view, focus ->
            if (focus) {
                binding.clBuyGoldInput.setBackgroundResource(com.jar.app.feature_buy_gold_v2.R.drawable.feature_buy_gold_v2_bg_input)
            } else {
                binding.clBuyGoldInput.setBackgroundResource(com.jar.app.core_ui.R.drawable.core_ui_round_black_bg_16dp)
            }


            binding.btnProceed.setDebounceClickListener {
                postClickEvent(GoldLeaseEventKey.Values.PROCEED_BUTTON)
                if (goldLeaseViewModel.buyVolume != 0.0f) {
                    val goldLeaseV2OrderSummaryArgs = GoldLeaseV2OrderSummaryArgs(
                        isNewLeaseUser = args.isNewLeaseUser,
                        flowType = args.flowType,
                        leaseId = null,
                        goldLeaseV2OrderSummaryScreenData = GoldLeaseV2OrderSummaryScreenData(
                            leasePlanList = args.leasePlan,
                            totalVolume = goldLeaseViewModel.buyVolume,
                            jarVolumeUsed = if (binding.switchLocker.isChecked) getLockerVolumeToBeUsed() else 0.0f
                        )
                    )
                    val encoded = encodeUrl(
                        serializer.encodeToString(
                            goldLeaseV2OrderSummaryArgs
                        )
                    )
                    navigateTo(
                        "${BaseAppDeeplink.GoldLease.GOLD_LEASE_ORDER_SUMMARY_SCREEN}/$encoded"
                    )
                }
            }
        }
    }

    private fun postClickEvent(buttonType: String, volumeSelected: Float = 0.0f) {
        analyticsApi.postEvent(
            GoldLeaseEventKey.LeaseOrderDetails.Lease_MainScreenClicked,
            mapOf(
                GoldLeaseEventKey.Properties.FROM_FLOW to args.flowType,
                GoldLeaseEventKey.Properties.USER_TYPE to if (args.isNewLeaseUser) GoldLeaseEventKey.Values.NEW else GoldLeaseEventKey.Values.REPEAT,
                GoldLeaseEventKey.Properties.BUTTON_TYPE to buttonType,
                GoldLeaseEventKey.Properties.QUANTITY_ENTERED to goldLeaseViewModel.buyVolume,
                GoldLeaseEventKey.Properties.AMOUNT_ENTERED to goldLeaseViewModel.buyAmount,
                GoldLeaseEventKey.Properties.JAR_SAVINGS_TOGGLE to if (binding.switchLocker.isChecked) GoldLeaseEventKey.Values.ON else GoldLeaseEventKey.Values.OFF,
                GoldLeaseEventKey.Properties.LOCKER_GOLD_USED to if (binding.switchLocker.isChecked) getLockerVolumeToBeUsed() else 0.0f,
                GoldLeaseEventKey.Properties.NON_LOCKER_GOLD_QUANTITY to if (binding.switchLocker.isChecked) goldLeaseViewModel.buyVolume - getLockerVolumeToBeUsed() else goldLeaseViewModel.buyVolume,
                GoldLeaseEventKey.Properties.NON_LOCKER_GOLD_PRICE to if (binding.switchLocker.isChecked) goldLeaseViewModel.getAmountForXVolume(goldLeaseViewModel.buyVolume - getLockerVolumeToBeUsed()) else goldLeaseViewModel.getAmountForXVolume(goldLeaseViewModel.buyVolume),
                GoldLeaseEventKey.Properties.EARNINGS to args.leasePlan.earningsPercentage.toString(),
                GoldLeaseEventKey.Properties.LOCK_IN to args.leasePlan.lockInComponent?.value.orEmpty(),
                GoldLeaseEventKey.Properties.GOLD_SELECTION to if (goldLeaseViewModel.buyGoldRequestType == BuyGoldRequestType.VOLUME) GoldLeaseEventKey.Values.IN_GM else GoldLeaseEventKey.Values.IN_RS,
                GoldLeaseEventKey.Properties.WEIGHTS_SELECTED to volumeSelected
            )
        )
    }

    private fun prefillRecommendedVolume(volume: Float) {
        goldLeaseViewModel.buyVolume = volume
        setTextInInputEditText(volume.volumeToStringWithoutTrailingZeros())
        goldLeaseViewModel.calculateAmountFromVolume(volume)
    }

    private fun clearFocus() {
        binding.etBuyGoldInput.clearFocus()
    }

    private fun changeInputType(inputType: BuyGoldRequestType) {
        goldLeaseViewModel.buyGoldRequestType = inputType
        binding.tvTabInRupees.setBackgroundResource(
            if (inputType == BuyGoldRequestType.AMOUNT) com.jar.app.core_ui.R.drawable.rounded_bg_3c3357_12dp else 0
        )
        binding.tvTabInGrams.setBackgroundResource(
            if (inputType == BuyGoldRequestType.VOLUME) com.jar.app.core_ui.R.drawable.rounded_bg_3c3357_12dp else 0
        )
        binding.tvTabInRupees.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (inputType == BuyGoldRequestType.AMOUNT) com.jar.app.core_ui.R.color.white else com.jar.app.core_ui.R.color.color_ACA1D3
            )
        )
        binding.tvTabInGrams.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (inputType == BuyGoldRequestType.VOLUME) com.jar.app.core_ui.R.color.white else com.jar.app.core_ui.R.color.color_ACA1D3
            )
        )

        binding.tvRupeeSymbol.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (inputType == BuyGoldRequestType.AMOUNT) com.jar.app.core_ui.R.color.white else com.jar.app.core_ui.R.color.color_ACA1D3
            )
        )
        binding.tvGramSymbol.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (inputType == BuyGoldRequestType.VOLUME) com.jar.app.core_ui.R.color.white else com.jar.app.core_ui.R.color.color_ACA1D3
            )
        )

        binding.tvGramSymbol.setTypeface(
            binding.tvGramSymbol.typeface,
            if (inputType == BuyGoldRequestType.VOLUME) Typeface.BOLD else Typeface.NORMAL
        )
        binding.tvRupeeSymbol.setTypeface(
            binding.tvRupeeSymbol.typeface,
            if (inputType == BuyGoldRequestType.AMOUNT) Typeface.BOLD else Typeface.NORMAL
        )

        if (inputType == BuyGoldRequestType.AMOUNT) {
            binding.etBuyGoldInput.filters = arrayOf(
                InputFilter.LengthFilter(9),
                RoundAmountToIntInputFilter(
                    shouldRoundToInt = true,
                    isInputSeparatedByComma = true
                )
            )
        } else {
            binding.etBuyGoldInput.filters =
                arrayOf(InputFilter.LengthFilter(7), DecimalDigitsInputFilter(4))
        }

        val goldInput =
            if (inputType == BuyGoldRequestType.VOLUME) goldLeaseViewModel.buyAmount else goldLeaseViewModel.buyVolume
        if (inputType == BuyGoldRequestType.AMOUNT) {
            binding.tvRupeeSymbol.text =
                getString(com.jar.app.feature_buy_gold_v2.shared.R.string.feature_buy_gold_v2_rupees_symbol)
            animateDecreaseTextSize(WeakReference(binding.tvGramSymbol))
            animateIncreaseTextSize(WeakReference(binding.tvRupeeSymbol))
            goldLeaseViewModel.calculateAmountFromVolume(goldInput)
        } else {
            binding.tvGramSymbol.text =
                getString(com.jar.app.feature_buy_gold_v2.shared.R.string.feature_buy_gold_v2_gm_label)
            animateIncreaseTextSize(WeakReference(binding.tvGramSymbol))
            animateDecreaseTextSize(WeakReference(binding.tvRupeeSymbol))
            goldLeaseViewModel.calculateVolumeFromAmount(goldInput)
        }
        suggestedAmountOptions?.let {
            if (inputType == BuyGoldRequestType.AMOUNT) suggestedGoldAmountAdapter?.submitList(
                it.leaseGoldOptionsAmountList
            ) else suggestedGoldAmountAdapter?.submitList(it.leaseGoldOptionsVolumeList)
        }
        setGoldLeaseVolumeAndEarningsAndTotal()
    }

    private fun animateDecreaseTextSize(textViewWR: WeakReference<TextView>) {
        textSizeDecreaseAnimation?.end()
        textSizeDecreaseAnimation =
            ObjectAnimator.ofFloat(
                textViewWR.get(), "textSize",
                MAX_TEXT_SIZE_FOR_LABEL,
                MIN_TEXT_SIZE_FOR_LABEL
            )
        textSizeDecreaseAnimation?.duration = TEXT_SIZE_ANIMATION_MILLIS
        textSizeDecreaseAnimation?.interpolator = LinearInterpolator()
        textSizeDecreaseAnimation?.start()
    }

    private fun animateIncreaseTextSize(textViewWR: WeakReference<TextView>) {
        textSizeIncreaseAnimation?.end()
        textSizeIncreaseAnimation =
            ObjectAnimator.ofFloat(
                textViewWR.get(), "textSize",
                MIN_TEXT_SIZE_FOR_LABEL,
                MAX_TEXT_SIZE_FOR_LABEL
            )
        textSizeIncreaseAnimation?.duration = TEXT_SIZE_ANIMATION_MILLIS
        textSizeIncreaseAnimation?.interpolator = LinearInterpolator()
        textSizeIncreaseAnimation?.start()
    }

    private fun setupToolbar() {
        binding.toolbar.root.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(), com.jar.app.core_ui.R.color.bgColor
            )
        )
        binding.toolbar.tvTitle.isVisible = true
        binding.toolbar.lottieView.isVisible = false
        binding.toolbar.ivEndImage.isVisible = false

        binding.toolbar.separator.isVisible = true

        binding.toolbar.tvTitle.text = getString(R.string.feature_gold_lease_order_details)

        //Setup FAQ Button
        binding.toolbar.tvEnd.setBackgroundResource(com.jar.app.core_ui.R.drawable.bg_rounded_40_121127)
        binding.toolbar.tvEnd.setPadding(16.dp, 8.dp, 16.dp, 8.dp)
        binding.toolbar.tvEnd.setTextColor(
            ContextCompat.getColor(
                requireContext(), com.jar.app.core_ui.R.color.color_EEEAFF
            )
        )
        binding.toolbar.tvEnd.text = getString(R.string.feature_gold_lease_faqs)
        binding.toolbar.tvEnd.isVisible = true

        binding.toolbar.tvEnd.setDebounceClickListener {
            analyticsApi.postEvent(
                GoldLeaseEventKey.CommonEvents.Lease_FAQButtonClicked,
                mapOf(
                    GoldLeaseEventKey.Properties.FROM_FLOW to args.flowType,
                    GoldLeaseEventKey.Properties.SCREEN_NAME to GoldLeaseEventKey.Screens.LEASE_DETAILS_SCREEN
                )
            )
            navigateTo(
                GoldLeaseNavigationDirections.actionToGoldLeaseFaqBottomSheetFragment(
                    flowType = args.flowType
                )
            )
        }

        binding.toolbar.btnBack.setDebounceClickListener {
            analyticsApi.postEvent(
                GoldLeaseEventKey.CommonEvents.Lease_BackButtonClicked,
                mapOf(
                    GoldLeaseEventKey.Properties.FROM_FLOW to args.flowType,
                    GoldLeaseEventKey.Properties.SCREEN_NAME to GoldLeaseEventKey.Screens.LEASE_DETAILS_SCREEN
                )
            )
            popBackStack()
        }
    }

    private fun toggleUIWhenKeyboardOpen(shouldShow: Boolean) {
        binding.clUserGoldBalance.isVisible = shouldShow && goldLeaseViewModel.goldBalance?.volume.orZero() != 0.0f
        binding.clPayableAmount.isVisible = shouldShow
        binding.separatorLine.isVisible = shouldShow
        binding.btnProceed.isVisible = shouldShow
    }

    private fun getLockerVolumeToBeUsed() = min(
        goldLeaseViewModel.buyVolume.orZero(),
        goldLeaseViewModel.goldBalance?.volume.orZero()
    )

    private fun getRawAmount() = binding.etBuyGoldInput.text?.toString()?.replace(",", "")

    override fun onDestroyView() {
        isFirstTimeGoldFetched = true
        isGoldPriceUpdated = false
        isInputTypeSet = false
        suggestedGoldAmountAdapter = null
        textSizeIncreaseAnimation?.cancel()
        textSizeDecreaseAnimation?.cancel()
        binding.etBuyGoldInput.removeTextChangedListener(inputTextWatcher)
        super.onDestroyView()
    }

    override fun onResume() {
        super.onResume()
        if (isShownEventSynced.not()) {
            isShownEventSynced = true
            analyticsApi.postEvent(
                GoldLeaseEventKey.LeaseOrderDetails.Lease_MainScreenLaunched,
                mapOf(
                    GoldLeaseEventKey.Properties.LAUNCH_TYPE to GoldLeaseEventKey.Values.DIRECT,
                    GoldLeaseEventKey.Properties.FROM_FLOW to args.flowType,
                    GoldLeaseEventKey.Properties.USER_TYPE to if (args.isNewLeaseUser) GoldLeaseEventKey.Values.NEW else GoldLeaseEventKey.Values.REPEAT
                )
            )
        } else {
            analyticsApi.postEvent(
                GoldLeaseEventKey.LeaseOrderDetails.Lease_MainScreenLaunched,
                mapOf(
                    GoldLeaseEventKey.Properties.LAUNCH_TYPE to GoldLeaseEventKey.Values.BACK_BUTTON_CLICKED,
                    GoldLeaseEventKey.Properties.FROM_FLOW to args.flowType,
                    GoldLeaseEventKey.Properties.USER_TYPE to if (args.isNewLeaseUser) GoldLeaseEventKey.Values.NEW else GoldLeaseEventKey.Values.REPEAT
                )
            )
        }
    }
}
