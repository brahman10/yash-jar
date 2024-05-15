package com.jar.app.feature_gold_sip.impl.ui.gold_sip_type_selection

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.SeekBar
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.jar.app.base.data.event.GoToHomeEvent
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.*
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.orZero
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.core_ui.extension.*
import com.jar.app.core_ui.extension.setOnImeActionDoneListener
import com.jar.app.feature_gold_sip.NavigationGoldSipDirections
import com.jar.app.feature_gold_sip.R
import com.jar.app.feature_gold_sip.databinding.FeatureGoldSipFragmentTypeSelectionBinding
import com.jar.app.feature_gold_sip.shared.GoldSipMR
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.internal.library.jar_core_network.api.util.collect
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.threeten.bp.LocalDate
import javax.inject.Inject

@AndroidEntryPoint
internal class GoldSipTypeSelectionFragment :
    BaseFragment<FeatureGoldSipFragmentTypeSelectionBinding>() {
    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureGoldSipFragmentTypeSelectionBinding
        get() = FeatureGoldSipFragmentTypeSelectionBinding::inflate

    @Inject
    lateinit var appScope: CoroutineScope

    @Inject
    lateinit var serializer: Serializer

    @Inject
    lateinit var remoteConfigApi: RemoteConfigApi

    @Inject
    lateinit var prefs: PrefsApi

    private var currentAnimation: ObjectAnimator? = null

    private var isAmountValid = false
    private var isSeekbarUpdateFromUser = false
    private var incrementBy = 0
    private val viewModelProvider by viewModels<GoldSipTypeSelectionViewModelAndroid> { defaultViewModelProviderFactory }

    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }

    private val sipTypeSelectionScreenData by lazy {
        args.sipTypeSelectionScreenData?.let {
            serializer.decodeFromString<SipTypeSelectionScreenData?>(decodeUrl(it))
        }
    }

    private var subscriptionType: com.jar.app.feature_gold_sip.shared.domain.model.SipSubscriptionType? =
        null

    private var sipAmount = 0f
    private var sliderMinValue = 0f
    private var sliderMaxValue = 0f
    private var recommendedDay = 0
    private var sipDay = ""
    private var sipDayValue = 0

    private val args: GoldSipTypeSelectionFragmentArgs by navArgs()

    companion object {
        const val GoldSipTypeSelectionFragment = "GoldSipTypeSelectionFragment"
    }

    private var backPressCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                handleBackPress()
            }
        }

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getData()
    }

    private fun readSubscriptionType() = sipTypeSelectionScreenData?.sipSubscriptionType?.let {
        com.jar.app.feature_gold_sip.shared.domain.model.SipSubscriptionType.valueOf(it)
    } ?: run {
        com.jar.app.feature_gold_sip.shared.domain.model.SipSubscriptionType.valueOf(
            remoteConfigApi.getSipSubscriptionType()
        )
    }

    override fun setup(savedInstanceState: Bundle?) {
//        getData()
        setupUI()
        setupListener()
        observeLiveData()
        checkForKeyboardState()
        registerBackPressDispatcher()
    }

    private fun getData() {
        subscriptionType = readSubscriptionType()
        subscriptionType?.let { viewModel.fetchSetupGoldSipData(it) }
    }

    private fun setupUI() {
        sipTypeSelectionScreenData?.sipSubscriptionType?.let {
            binding.btnSkip.isVisible =
                sipTypeSelectionScreenData?.shouldShowSelectionContainer.orFalse().not()
            binding.clSipTypeContainer.isVisible =
                sipTypeSelectionScreenData?.shouldShowSelectionContainer.orFalse()
        }
        if (sipTypeSelectionScreenData == null || sipTypeSelectionScreenData?.shouldShowSelectionContainer.orFalse())
            binding.toolbar.tvTitle.text = getCustomString(GoldSipMR.strings.feature_gold_sip_label)
        else {
            subscriptionType?.let {
                binding.toolbar.tvTitle.text = when (it) {
                    com.jar.app.feature_gold_sip.shared.domain.model.SipSubscriptionType.WEEKLY_SIP -> {
                        binding.tvWeekly.performClick()
                        getCustomString(GoldSipMR.strings.feature_gold_sip_weekly_saving_plan)
                    }

                    com.jar.app.feature_gold_sip.shared.domain.model.SipSubscriptionType.MONTHLY_SIP -> {
                        binding.tvMonthly.performClick()
                        getCustomString(GoldSipMR.strings.feature_gold_sip_monthly_saving_plan)
                    }
                }
            }
        }

        binding.toolbar.separator.isVisible = true
        binding.toolbar.ivTitleImage.setImageResource(com.jar.app.core_ui.R.drawable.core_ui_ic_gold_sip)
        binding.noteStackLottie.setAnimationFromUrl(
            BaseConstants.LottieUrls.NOTE_STACK
        )
        viewModel.fireSipSetupEvent(
            eventName = com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Shown_SIPsetupScreen,
            eventParamsMap = mapOf(com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Action to com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Shown)
        )
    }

    private fun setupListener() {
        binding.tvWeekly.setDebounceClickListener {
            viewModel.fetchSetupGoldSipData(com.jar.app.feature_gold_sip.shared.domain.model.SipSubscriptionType.WEEKLY_SIP)
        }
        binding.tvMonthly.setDebounceClickListener {
            viewModel.fetchSetupGoldSipData(com.jar.app.feature_gold_sip.shared.domain.model.SipSubscriptionType.MONTHLY_SIP)
        }

        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar,
                progressValue: Int,
                fromUser: Boolean
            ) {
                if (isBindingInitialized()) {
                    isSeekbarUpdateFromUser = true
                    updateSipProgress(progressValue + sliderMinValue)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                viewModel.fireSipSetupEvent(
                    eventName = com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Shown_SIPsetupScreen,
                    eventParamsMap = mapOf(
                        com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Action to com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Slider_Moved,
                        if (subscriptionType != null) {
                            com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Frequency to getCustomString(
                                subscriptionType!!.textRes
                            )
                        } else {
                            com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Frequency to ""

                        },
                        com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.SIP_Amount to sipAmount,
                    )
                )
            }
        })

        binding.btnProceed.setDebounceClickListener {
            subscriptionType?.let {
                val localDate = LocalDate.now()
                viewModel.currentlySelectedValue = sipAmount

                viewModel.fireSipSetupEvent(
                    eventName = com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Shown_SIPsetupScreen,
                    eventParamsMap = mapOf(
                        com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Action to com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.ClickedProceed,
                        com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Frequency to getCustomString(
                            it.textRes
                        ),
                        com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.SIP_Amount to sipAmount,
                    )
                )
                navigateTo(
                    NavigationGoldSipDirections.actionToSipMandateRedirectionFragment(
                        com.jar.app.feature_gold_sip.shared.domain.event.GoldSipUpdateEvent(
                            sipAmount = sipAmount,
                            sipDay = localDate.dayOfWeek.name,
                            sipDayValue = if (localDate.dayOfWeek.value < 7) localDate.dayOfWeek.value + 1 else 1,
                            subscriptionType = it.name
                        ),
                        isOnboardingFlow = sipTypeSelectionScreenData != null
                    )
                )
            }
        }

        binding.toolbar.btnBack.setDebounceClickListener {
            handleBackPress()
        }

        binding.etAmount.doAfterTextChanged {
            var message = ""
            if (it.isNullOrEmpty()) {
                isAmountValid = false
                message =
                    getCustomString(GoldSipMR.strings.feature_gold_sip_this_field_cannot_be_left_empty)
            } else if (it.toString().toInt() > sliderMaxValue) {
                isAmountValid = false
                message = getCustomStringFormatted(
                    GoldSipMR.strings.feature_gold_sip_maximum_value_for_sip_is_x,
                    sliderMaxValue.toInt()
                )
            } else if (it.toString().toInt() < sliderMinValue) {
                isAmountValid = false
                message = getCustomStringFormatted(
                    GoldSipMR.strings.feature_gold_sip_minimum_value_for_sip_is_x,
                    sliderMinValue.toInt()
                )
            } else {
                isAmountValid = true
                isSeekbarUpdateFromUser = false
                updateSipProgress(it.toString().toFloat())
            }
            if (isAmountValid.not()) {
                makeErrorInEditAmountBox(message)
            } else {
                binding.btnProceed.setDisabled(currentAnimation?.isRunning.orFalse())
                resetErrorEditAmountBox()
            }
        }

        binding.etAmount.setOnImeActionDoneListener {
            binding.etAmount.hideKeyboard()
        }

        binding.ivEdit.setDebounceClickListener {
            binding.etAmount.requestFocus()
            binding.etAmount.setSelection(binding.etAmount.text?.length.orZero())
            binding.etAmount.showKeyboard()
        }

        binding.btnSkip.setDebounceClickListener {
            subscriptionType?.let {
                viewModel.fireSipSetupEvent(
                    eventName = com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Clicked_Button_Skip,
                    eventParamsMap = mapOf(
                        com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.FromScreen to com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Onboarding,
                        com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.FromFlow to getCustomString(
                            it.textRes
                        )
                    )
                )
            }
            EventBus.getDefault().post(GoToHomeEvent(GoldSipTypeSelectionFragment))
        }
    }

    private fun makeErrorInEditAmountBox(errorMessage: String) {
        val errorColor =
            ContextCompat.getColor(requireContext(), com.jar.app.core_ui.R.color.color_FF8D91)
        binding.etAmount.setTextColor(errorColor)
        binding.amountEditBoxHolder.setBackgroundResource(R.drawable.feature_gold_sip_bg_amount_edit_box_error)
        binding.etAmount.setCompoundDrawablesWithIntrinsicBounds(
            R.drawable.feature_gold_sip_ic_rs_sign_error,
            0,
            0,
            0
        )
        binding.etAmount.shakeAnimation()
        binding.tvAmountError.isVisible = true
        binding.tvAmountError.text = errorMessage
        binding.btnProceed.setDisabled(true)
        binding.scrollView.scrollToBottom()
    }

    private fun resetErrorEditAmountBox() {
        binding.btnProceed.setDisabled(false)
        binding.etAmount.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                com.jar.app.core_ui.R.color.white
            )
        )
        binding.etAmount.setCompoundDrawablesWithIntrinsicBounds(
            com.jar.app.core_ui.R.drawable.core_ui_ic_rs_sign_bold,
            0,
            0,
            0
        )
        binding.amountEditBoxHolder.setBackgroundResource(R.drawable.feature_gold_sip_bg_amount_edit_box)
        binding.tvAmountError.isGone = true
    }

    private fun checkForKeyboardState() {
        uiScope.launch {
            binding.root.keyboardVisibilityChanges().collectLatest { isKeyboardShowing ->
                if (isKeyboardShowing)
                    binding.scrollView.scrollToBottom()
            }
        }
    }

    private fun updateSipProgress(progress: Float) {
        sipAmount = progress
        if (isSeekbarUpdateFromUser) {
            binding.etAmount.setText(progress.toInt().toString())
        } else {
            binding.seekBar.progress = (progress - sliderMinValue).toInt()
        }
        binding.noteStackLottie.progress =
            (progress / sliderMaxValue)
        binding.etAmount.setSelection(binding.etAmount.text?.length.orZero())
    }

    private fun observeLiveData() {

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.fetchSetupGoldSipFlow.collect(
                    onLoading = { showProgressBar() },
                    onSuccess = {
                        dismissProgressBar()
                        subscriptionType =
                            com.jar.app.feature_gold_sip.shared.domain.model.SipSubscriptionType.valueOf(
                                it.subscriptionType.uppercase()
                            )
                        setViewsAccordingToSelectedSipType()
                        binding.tvMinValue.text =
                            getString(
                                com.jar.app.core_ui.R.string.core_ui_rs_x_int,
                                it.sliderMinValue.toInt()
                            )
                        binding.tvMaxValue.text =
                            getString(
                                com.jar.app.core_ui.R.string.core_ui_rs_x_int,
                                it.sliderMaxValue.toInt()
                            )
                        sipAmount =
                            it.recommendedSubscriptionAmount.orZero()
                        recommendedDay = it.recommendedDay
                        sliderMinValue = it.sliderMinValue
                        sliderMaxValue = it.sliderMaxValue
                        incrementBy = it.sliderStepCount
                        binding.seekBar.incrementProgressBy(incrementBy)
                        binding.seekBar.max =
                            (sliderMaxValue - sliderMinValue).toInt()

                        viewModel.fireSipSetupEvent(
                            eventName = com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Shown_SIPsetupScreen,
                            eventParamsMap = mapOf(
                                com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Action to com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Shown,
                                if (subscriptionType != null) {
                                    com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Frequency to getCustomString(
                                        subscriptionType!!.textRes
                                    )
                                } else {
                                    com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Frequency to ""
                                },
                                com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.SIP_Amount to sipAmount,
                                com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.DefaultFrequency to remoteConfigApi.getSipSubscriptionType()
                            )
                        )
                        viewModel.currentlySelectedValue?.let { currentValue ->
                            animateSeekbarToTheRecommendedValue(currentValue - it.sliderMinValue.orZero())
                        } ?: kotlin.run {
                            animateSeekbarToTheRecommendedValue(it.recommendedSubscriptionAmount.orZero() - it.sliderMinValue.orZero())
                        }
                    },
                    onError = { errorMessage, _ ->
                        errorMessage.snackBar(binding.root)
                        dismissProgressBar()
                    }
                )
            }
        }

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Boolean>(
            com.jar.app.feature_gold_sip.shared.util.GoldSipConstants.SELECT_DAY_OR_DATE_BOTTOM_SHEET_CLOSED
        )?.observe(viewLifecycleOwner) {
            getCustomString(GoldSipMR.strings.feature_gold_sip_you_need_to_select_a_day_to_proceed).snackBar(
                binding.root,
                com.jar.app.core_ui.R.drawable.ic_filled_information_icon,
                progressColor = com.jar.app.core_ui.R.color.color_016AE1
            )
        }
    }

    private fun registerBackPressDispatcher() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            backPressCallback
        )
    }

    private fun handleBackPress() {
        subscriptionType?.let {

            viewModel.fireSipSetupEvent(
                eventName = com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Shown_SIPsetupScreen,
                eventParamsMap = mapOf(
                    com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Action to com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Back_Clicked,
                    com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Frequency to getCustomString(
                        it.textRes
                    ),
                    com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.SIP_Amount to sipAmount,
                )
            )
        }
        navigateTo(
            GoldSipTypeSelectionFragmentDirections.actionGoldSipTypeSelectionFragmentToContinueSipSetupBottomSheet(
                sipTypeSelectionScreenData != null
            )
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        currentAnimation?.cancel()
        backPressCallback.isEnabled = false
    }

    private fun setViewsAccordingToSelectedSipType() {
        subscriptionType?.let {
            when (it) {
                com.jar.app.feature_gold_sip.shared.domain.model.SipSubscriptionType.WEEKLY_SIP -> {
                    binding.tvWeekly.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.feature_gold_sip_bg_rounded_60dp_7745ff
                    )
                    binding.tvMonthly.background = null
                    binding.tvHowMuchYouLikeToSave.text =
                        getCustomString(GoldSipMR.strings.feature_gold_sip_how_would_you_like_to_save_weekly)
                }

                com.jar.app.feature_gold_sip.shared.domain.model.SipSubscriptionType.MONTHLY_SIP -> {
                    binding.tvMonthly.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.feature_gold_sip_bg_rounded_60dp_7745ff
                    )
                    binding.tvWeekly.background = null
                    binding.tvHowMuchYouLikeToSave.text =
                        getCustomString(GoldSipMR.strings.feature_gold_sip_how_would_you_like_to_save_monthly)
                }
            }
        }
    }

    private fun animateSeekbarToTheRecommendedValue(recommendedAmount: Float) {
        currentAnimation?.cancel()
        currentAnimation =
            ObjectAnimator.ofInt(binding.seekBar, "progress", 0, recommendedAmount.toInt())
        subscriptionType?.let {
            currentAnimation?.duration = when (it) {
                com.jar.app.feature_gold_sip.shared.domain.model.SipSubscriptionType.WEEKLY_SIP -> recommendedAmount.toLong() / 2
                com.jar.app.feature_gold_sip.shared.domain.model.SipSubscriptionType.MONTHLY_SIP -> recommendedAmount.toLong() / 4
            }
        }
        currentAnimation?.interpolator = LinearInterpolator()
        currentAnimation?.start()
    }

    private fun clearFocusAndHideKeyboard() {
        binding.etAmount.clearFocus()
        binding.etAmount.hideKeyboard()
    }
}