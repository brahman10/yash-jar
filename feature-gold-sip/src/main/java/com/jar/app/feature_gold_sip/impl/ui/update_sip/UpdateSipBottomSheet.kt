package com.jar.app.feature_gold_sip.impl.ui.update_sip

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jar.app.base.data.event.RefreshGoldSipEvent
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.app.base.util.*
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.orZero
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import com.jar.app.core_ui.api.CoreUiApi
import com.jar.app.core_ui.extension.hideKeyboard
import com.jar.app.core_ui.extension.scrollToBottom
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.core_ui.generic_post_action.data.GenericPostActionStatusData
import com.jar.app.core_ui.generic_post_action.data.PostActionStatus
import com.jar.app.core_ui.item_decoration.SpaceItemDecoration
import com.jar.app.core_ui.widget.CustomEditTextWithErrorHandling
import com.jar.app.feature_gold_sip.R
import com.jar.app.feature_gold_sip.databinding.FeatureGoldSipBottomSheetUpdateSipBinding
import com.jar.app.feature_gold_sip.impl.ui.update_sip.adapter.suggestion.SipAmountSuggestionAdapter
import com.jar.app.feature_gold_sip.impl.ui.update_sip.adapter.week_or_month.UpdateWeekOrMonthAdapter
import com.jar.app.feature_gold_sip.shared.util.GoldSipConstants
import com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey
import com.jar.app.feature_gold_sip.shared.util.WeekGenerator
import com.jar.app.feature_gold_sip.shared.GoldSipMR
import com.jar.app.feature_gold_sip.shared.domain.event.GoldSipUpdateEvent
import com.jar.app.feature_gold_sip.shared.domain.model.SipSubscriptionType
import com.jar.app.feature_gold_sip.shared.domain.model.UpdateSipDetails
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jar_core_network.api.util.collectUnwrapped
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
internal class UpdateSipBottomSheet :
    BaseBottomSheetDialogFragment<FeatureGoldSipBottomSheetUpdateSipBinding>() {
    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureGoldSipBottomSheetUpdateSipBinding
        get() = FeatureGoldSipBottomSheetUpdateSipBinding::inflate
    override val bottomSheetConfig: BottomSheetConfig
        get() = BottomSheetConfig(isDraggable = false)

    @Inject
    lateinit var coreUiApi: CoreUiApi

    @Inject
    lateinit var weekGenerator: WeekGenerator

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    private var suggestionAdapter: SipAmountSuggestionAdapter? = null
    private var weekOrMonthAdapter: UpdateWeekOrMonthAdapter? = null
    private var activityRef: WeakReference<FragmentActivity>? = null
    private var newSipAmount = 0f
    private var oldSipAmount = 0
    private var mandateAmount = 0
    private var minSipAmount = 0f
    private var maxSipAmount = 0f
    private var sipSubscriptionDay = 0
    private var recommendedDay = 0
    private var shouldShowUserSipData = true
    private var sipDate = ""
    private val args: UpdateSipBottomSheetArgs by navArgs()
    private var spaceItemDecoration: SpaceItemDecoration? = null
    private var suggestedAmountSpaceItemDecoration = SpaceItemDecoration(4.dp, 2.dp)


    private val viewModelProvider by viewModels<UpdateSipViewModelAndroid> { defaultViewModelProviderFactory }

    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }
    private var sipSubscriptionType: SipSubscriptionType? = null
    private var isAmountValid = true

    override fun getTheme(): Int {
        return com.jar.app.core_ui.R.style.BottomSheetDialogInput
    }

    override fun setup() {
        getData()
        setupUI()
        setupListener()
        observeFlow()
    }

    private fun getData() {
        activityRef = WeakReference(requireActivity())
        sipSubscriptionType = args.sipSubscriptionType
        viewModel.fetchGoldSipDetails()
    }

    private fun setupUI() {
        binding.etAmount.setEditTextEnumType(
            CustomEditTextWithErrorHandling.EditTextType.NUMBER,
            uiScope
        )
        binding.etAmount.setStartIcon(com.jar.app.core_ui.R.drawable.core_ui_ic_rs_sign)
    }

    private fun setSuggestionAmountAdapter() {
        suggestionAdapter = SipAmountSuggestionAdapter { suggestedAmount, pos ->
            binding.etAmount.setEditTextValue(suggestedAmount.amount.toInt().toString())
            clearFocusAndHideKeyboard()
        }
        binding.rvSuggestedAmount.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        binding.rvSuggestedAmount.addItemDecorationIfNoneAdded(suggestedAmountSpaceItemDecoration)
        binding.rvSuggestedAmount.adapter = suggestionAdapter
    }

    private fun setWeekOrMonthAdapter() {
        weekOrMonthAdapter =
            UpdateWeekOrMonthAdapter(sipSubscriptionType!!) { weekOrMonthData, pos ->
                updateSuggestionListClick(pos)
                setWeekOrMonthData(weekOrMonthData.value)
                clearFocusAndHideKeyboard()
            }
        binding.rvDaySelector.layoutManager = GridLayoutManager(
            requireContext(), when (sipSubscriptionType!!) {
                SipSubscriptionType.WEEKLY_SIP -> 3
                SipSubscriptionType.MONTHLY_SIP -> 6
            }
        )

        spaceItemDecoration =
            SpaceItemDecoration(
                0, when (sipSubscriptionType!!) {
                    SipSubscriptionType.WEEKLY_SIP -> 8.dp
                    SipSubscriptionType.MONTHLY_SIP -> 6.dp
                }
            )

        binding.rvDaySelector.addItemDecorationIfNoneAdded(spaceItemDecoration!!)
        binding.rvDaySelector.adapter = weekOrMonthAdapter
    }

    private fun setupListener() {
        binding.tvMonthly.setDebounceClickListener {
            sipSubscriptionType = SipSubscriptionType.MONTHLY_SIP
            checkUncheckSipType()
        }
        binding.tvWeekly.setDebounceClickListener {
            sipSubscriptionType = SipSubscriptionType.WEEKLY_SIP
            checkUncheckSipType()
        }

        binding.btnGoAhead.setDebounceClickListener {
            viewModel.weekOrMonthLocalObjectFlow.value?.let {
                analyticsHandler.postEvent(
                    GoldSipEventKey.Shown_UpdateSIPbottomSheet, mapOf(
                        GoldSipEventKey.Action to GoldSipEventKey.Shown,
                        GoldSipEventKey.Frequency to getCustomString(sipSubscriptionType!!.textRes),
                        GoldSipEventKey.SIP_Amount to oldSipAmount,
                        GoldSipEventKey.SIP_Date to sipDate
                    )
                )
                if (newSipAmount > mandateAmount) {
                    val goldSipUpdateEvent =
                        GoldSipUpdateEvent(
                            newSipAmount,
                            when (sipSubscriptionType!!) {
                                SipSubscriptionType.WEEKLY_SIP -> getCustomString(
                                    weekGenerator.getWeekFromDay(it.value).stringRes
                                )

                                SipSubscriptionType.MONTHLY_SIP -> it.value.toString()
                            },
                            it.value,
                            sipSubscriptionType!!.name
                        )
                    EventBus.getDefault().post(goldSipUpdateEvent)
                } else if (newSipAmount >= minSipAmount) {
                    viewModel.updateGoldSip(
                        UpdateSipDetails(
                            newSipAmount,
                            subscriptionType = sipSubscriptionType!!.name,
                            subscriptionDay = sipSubscriptionDay
                        )
                    )
                } else {
                    binding.btnGoAhead.setDisabled(true)
                    getCustomStringFormatted(
                        GoldSipMR.strings.feature_gold_sip_minimum_amount_required_to_setup_sip_is_x,
                        minSipAmount.toInt()
                    ).snackBar(binding.root)
                }
            } ?: kotlin.run {
                binding.btnGoAhead.setDisabled(true)
                getCustomString(GoldSipMR.strings.feature_gold_sip_please_select_a_day_to_proceed).snackBar(
                    binding.root
                )
            }
        }

        binding.etAmount.setCustomTextValidationListener {
            isAmountValid = false
            if (it.isNullOrEmpty())
                binding.etAmount.showError(getCustomString(GoldSipMR.strings.feature_gold_sip_this_field_cannot_be_left_empty))
            else if (it.toInt() > maxSipAmount)
                binding.etAmount.showError(
                    getCustomStringFormatted(
                        GoldSipMR.strings.feature_gold_sip_maximum_value_for_sip_is_x, maxSipAmount.toInt()
                    )
                )
            else if (it.toInt() < minSipAmount)
                binding.etAmount.showError(
                    getCustomStringFormatted(
                        GoldSipMR.strings.feature_gold_sip_minimum_value_for_sip_is_x,
                        minSipAmount.toInt()
                    )
                )
            else {
                isAmountValid = true
                binding.etAmount.resetDefault()
                newSipAmount = it.toFloat().orZero()
                binding.tvYouWillBeReqToProvideAutopay.isVisible =
                    newSipAmount > mandateAmount
            }
        }

        binding.tvWeekOrMonth.setDebounceClickListener {
            binding.rvDaySelector.isVisible = true
            binding.root.scrollToBottom()
            clearFocusAndHideKeyboard()
        }

        binding.etAmount.setOnEditTextContainerClicked {
            binding.rvSuggestedAmount.isVisible = true
        }

        binding.root.setDebounceClickListener {
            clearFocusAndHideKeyboard()
        }

        binding.etAmount.setIsValidatedListener { isValid, s ->
            isAmountValid = isValid
            binding.btnGoAhead.setDisabled(viewModel.weekOrMonthLocalObjectFlow.value == null || isValid.not())
        }

        binding.ivCross.setDebounceClickListener {
            dismiss()
        }

        binding.etAmount.setOnImeActionDoneListener {
            binding.etAmount.hideKeyboard()
        }

    }

    private fun observeFlow() {

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.goldSipDetailsFlow.collect(
                    onLoading = { showProgressBar() },
                    onSuccess = {
                        dismissProgressBar()
                        it?.let {
                            setWeekOrMonthData(it.subscriptionDay)
                            oldSipAmount = it.subscriptionAmount.toInt()
                            mandateAmount = it.mandateAmount.orZero().toInt()
                            when (sipSubscriptionType!!) {
                                SipSubscriptionType.WEEKLY_SIP -> binding.tvWeekly.performClick()
                                SipSubscriptionType.MONTHLY_SIP -> binding.tvMonthly.performClick()
                            }
                            sipDate = it.updateDate?.epochToDate()
                                ?.getFormattedDate("d MMM''yy, h:mm a").orEmpty()
                            analyticsHandler.postEvent(
                                GoldSipEventKey.Shown_UpdateSIPbottomSheet, mapOf(
                                    GoldSipEventKey.Action to GoldSipEventKey.Shown,
                                    GoldSipEventKey.Frequency to getCustomString(sipSubscriptionType!!.textRes),
                                    GoldSipEventKey.SIP_Amount to oldSipAmount,
                                    GoldSipEventKey.SIP_Date to sipDate
                                )
                            )
                        }
                    },
                    onError = { errorMessage, _ ->
                        errorMessage.snackBar(binding.root)
                        dismissProgressBar()
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.fetchSetupGoldSipFlow.collect(
                    onLoading = { showProgressBar() },
                    onSuccess = {
                        dismissProgressBar()
                        weekOrMonthAdapter = null
                        suggestionAdapter = null
                        minSipAmount = it.sliderMinValue
                        maxSipAmount = it.sliderMaxValue
                        recommendedDay = it.recommendedDay
                        sipSubscriptionType?.let { sipType ->
                            viewModel.fetchWeekOrMonth(
                                sipType,
                                if (sipType == args.sipSubscriptionType) sipSubscriptionDay else recommendedDay,
                                sipType != args.sipSubscriptionType
                            )
                        }
                        setSuggestionAmountAdapter()
                        setWeekOrMonthAdapter()
                        binding.etAmount.setEditTextValue(oldSipAmount.toString())
                        if (shouldShowUserSipData)
                            shouldShowUserSipData = false
                        else {
                            setWeekOrMonthData(it.recommendedDay)
                            it.options.find { it.recommended == true }?.let {
                                binding.etAmount.setEditTextValue(it.amount.toInt().toString())
                            }
                        }
                        suggestionAdapter?.submitList(it.options)
                    },
                    onError = { errorMessage, _ ->
                        errorMessage.snackBar(binding.root)
                        dismissProgressBar()
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.updateGoldSipDetailsFlow.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        EventBus.getDefault().post(RefreshGoldSipEvent())
                        if (it.subscriptionId.isNullOrEmpty())
                            EventBus.getDefault().post(
                                GoldSipUpdateEvent(
                                    sipAmount = it.subscriptionAmount,
                                    sipDay = when (sipSubscriptionType!!) {
                                        SipSubscriptionType.WEEKLY_SIP -> getCustomString(
                                            weekGenerator.getWeekFromDay(it.subscriptionDay).stringRes
                                        )

                                        SipSubscriptionType.MONTHLY_SIP -> it.subscriptionDay.toString()
                                    },
                                    it.subscriptionDay,
                                    subscriptionType = sipSubscriptionType!!.name
                                )
                            )
                        else
                            coreUiApi.openGenericPostActionStatusFragment(
                                GenericPostActionStatusData(
                                    postActionStatus = PostActionStatus.ENABLED.name,
                                    header = getCustomString(GoldSipMR.strings.feature_gold_sip_yay_gold_sip_updated_successfully),
                                    title = getCustomStringFormatted(
                                        GoldSipMR.strings.feature_gold_sip_updated_amount_will_be_debited_from_date_s,
                                        it.nextDeductionDate?.getDateShortMonthNameAndYear()
                                            .orEmpty()
                                    ),
                                    titleColorRes = com.jar.app.core_ui.R.color.color_EBB46A,
                                    description = null,
                                    lottieUrl = BaseConstants.LottieUrls.SMALL_CHECK,
                                )
                            ) {
                                findNavController().getBackStackEntry(R.id.goldSipDetailsFragment).savedStateHandle[GoldSipConstants.UPDATE_SIP_BOTTOM_SHEET_CLOSED] =
                                    true
                                popBackStack()
                            }
                    },
                    onError = { errorMessage, _ ->
                        errorMessage.snackBar(binding.root)
                        dismissProgressBar()
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.weekOrMonthFlow.collectUnwrapped(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        weekOrMonthAdapter?.submitList(it)
                    },
                    onError = { _, _ ->
                        dismissProgressBar()
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.weekOrMonthLocalObjectFlow.collect {
                    binding.btnGoAhead.setDisabled(it == null || isAmountValid.not())
                }
            }
        }
    }

    private fun checkUncheckSipType() {
        clearFocusAndHideKeyboard()
        binding.rvDaySelector.isVisible = false
        binding.rvSuggestedAmount.isVisible = false

        sipSubscriptionType?.let { viewModel.fetchSetupGoldSipData(it) }

        when (sipSubscriptionType) {
            SipSubscriptionType.WEEKLY_SIP -> {
                binding.tvWeekly.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    com.jar.app.core_ui.R.drawable.core_ui_bg_radio_selected,
                    0, 0, 0
                )
                binding.tvMonthly.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    com.jar.app.core_ui.R.drawable.core_ui_bg_radio_unselected,
                    0, 0, 0
                )
                binding.tvYouWillSaveEveryWeekOrMonth.text =
                    getCustomString(GoldSipMR.strings.feature_gold_sip_you_will_save_every_week_on)
            }

            SipSubscriptionType.MONTHLY_SIP -> {
                binding.tvMonthly.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    com.jar.app.core_ui.R.drawable.core_ui_bg_radio_selected,
                    0, 0, 0
                )
                binding.tvWeekly.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    com.jar.app.core_ui.R.drawable.core_ui_bg_radio_unselected,
                    0, 0, 0
                )
                binding.tvYouWillSaveEveryWeekOrMonth.text =
                    getCustomString(GoldSipMR.strings.feature_gold_sip_you_will_save_every_month_on)
            }

            else -> {}
        }
    }

    private fun clearFocusAndHideKeyboard() {
        binding.etAmount.clearFocus()
        binding.etAmount.hideKeyboard()
    }

    private fun setWeekOrMonthData(subscriptionDay: Int) {
        this.sipSubscriptionDay = subscriptionDay
        binding.tvWeekOrMonth.text = when (sipSubscriptionType) {
            SipSubscriptionType.WEEKLY_SIP -> {
                getCustomString(
                    weekGenerator.getWeekFromDay(subscriptionDay).stringRes
                )
            }

            SipSubscriptionType.MONTHLY_SIP -> {
                subscriptionDay.toString()
            }

            null -> ""
        }
    }

    private fun updateSuggestionListClick(position: Int) {
        sipSubscriptionType?.let { sipSubscriptionType ->
            weekOrMonthAdapter?.currentList?.let {
                viewModel.updateListOnItemClick(
                    it, position,
                    sipSubscriptionType == args.sipSubscriptionType
                )
            }
        }
    }
}