package com.jar.app.feature_lending.impl.ui.choose_amount.amount

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Vibrator
import android.os.VibratorManager
import android.text.InputFilter
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.jar.app.base.data.event.LendingBackPressEvent
import com.jar.app.base.data.event.LendingToolbarTitleEventV2
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.decodeUrl
import com.jar.app.base.util.getFormattedAmount
import com.jar.app.base.util.openWhatsapp
import com.jar.app.core_base.domain.model.User
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.orFalse
import com.jar.app.core_base.util.orZero
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.core_ui.extension.hideKeyboard
import com.jar.app.core_ui.extension.keyboardVisibilityChanges
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.setTypeAmount
import com.jar.app.core_ui.extension.vibrate
import com.jar.app.feature_lending.LendingStepsNavigationDirections
import com.jar.app.feature_lending.R
import com.jar.app.feature_lending.databinding.FragmentSelectLoanAmountBinding
import com.jar.app.feature_lending.impl.domain.event.ReadyCashNavigationEvent
import com.jar.app.feature_lending.impl.ui.host_container.LendingHostViewModelAndroid
import com.jar.app.feature_lending.shared.MR
import com.jar.app.feature_lending.shared.domain.LendingEventKeyV2
import com.jar.app.feature_lending.shared.domain.model.experiment.ReadyCashScreenArgs
import com.jar.app.feature_lending.shared.domain.model.v2.PreApprovedData
import com.jar.app.feature_lending.shared.ui.step_view.LendingStep
import com.jar.app.feature_lending_kyc.impl.util.AmountTextFormatter
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject
import kotlin.math.floor

@AndroidEntryPoint
internal class SelectLoanAmountFragment : BaseFragment<FragmentSelectLoanAmountBinding>() {

    @Inject
    lateinit var remoteConfigApi: RemoteConfigApi

    @Inject
    lateinit var prefs: PrefsApi

    @Inject
    lateinit var serializer: Serializer

    @Inject
    lateinit var analyticsApi: AnalyticsApi

    private var errorMessage: String? = null
    private var isSeekBarScrolled: Boolean = false
    private var isAmountEntered: Boolean = false

    private var initialAmount: Int = 0

    private var isInvalidAmount=false

    private val viewModelProvider: SelectLoanAmountViewModelAndroid by viewModels { defaultViewModelProviderFactory }
    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }

    private val parentViewModelProvider: LendingHostViewModelAndroid by activityViewModels { defaultViewModelProviderFactory }
    private val parentViewModel by lazy {
        parentViewModelProvider.getInstance()
    }

    private val vibrator by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager =
                binding.root.context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else
            binding.root.context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    private val arguments by navArgs<SelectLoanAmountFragmentArgs>()
    private val args by lazy {
        serializer.decodeFromString<ReadyCashScreenArgs>(
            decodeUrl(arguments.screenArgs)
        )
    }

    private val seekChangeListener: SeekBar.OnSeekBarChangeListener by lazy {
        object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val finalProgress = (progress + viewModel.preApprovedData?.minDrawDown.orZero())
                val roundedNumber = (floor(finalProgress.toInt() / 100.0)).toInt() * 100
                val formattedAmount = roundedNumber.getFormattedAmount()
                binding.etAmount.setText(formattedAmount)
                binding.etAmount.setSelection(formattedAmount.length)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                initialAmount = getRawAmount()?.toIntOrNull().orZero()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                val finalProgress =
                    (seekBar?.progress.orZero() + viewModel.preApprovedData?.minDrawDown.orZero())
                analyticsApi.postEvent(
                    event = LendingEventKeyV2.Lending_RCashAmountBarScrolled,
                    values = mapOf(
                        LendingEventKeyV2.changed_from to initialAmount.toString(),
                        LendingEventKeyV2.changed_to to finalProgress,
                        LendingEventKeyV2.user_type to getUsertype()
                    )
                )
                isSeekBarScrolled = true
                binding.etAmount.vibrate(vibrator)

                if (binding.etAmount.hasFocus())
                    binding.etAmount.hideKeyboard()
            }
        }
    }

    private fun showErrorMessage(errorMessage: String?) {
        errorMessage?.let {
            binding.tvError.isVisible = true
            binding.amountEditBoxHolder.setBackgroundResource(com.jar.app.core_ui.R.drawable.core_ui_bg_outlined_d5cdf2_error_12dp)
            binding.tvError.text = it
            isInvalidAmount=true
        } ?: run {
            binding.tvError.text = ""
            binding.tvError.isVisible = false
            binding.amountEditBoxHolder.setBackgroundResource(com.jar.app.core_ui.R.drawable.core_ui_bg_outlined_d5cdf2_12dp)
            isInvalidAmount=false
        }
    }

    private fun getUsertype(): String {
        return if (args.isRepeatWithdrawal) "Repeat" else "New"
    }

    private val textWatcher: TextWatcher by lazy {
        binding.etAmount.doAfterTextChanged {
            updateNoteStack()
            validateAmount()
            binding.btnAction.setDisabled((isSpecialCase().not() && it.isNullOrBlank()) || errorMessage != null)
        }
    }

    private fun validateAmount() {
        val amount = getRawAmount()?.toIntOrNull().orZero()
        errorMessage = if (binding.etAmount.text.isNullOrEmpty()) {
            getCustomString(MR.strings.feature_lending_error_please_enter_amount)
        } else if (amount < getMinAmount()) {
            getCustomStringFormatted(
                MR.strings.feature_lending_error_amount_cant_be_less_than,
                getMinAmount().getFormattedAmount()
            )
        } else if (amount > getMaxAmount()) {
            getCustomStringFormatted(
                MR.strings.feature_lending_error_amount_cant_be_more_than,
                getMaxAmount().getFormattedAmount()
            )
        } else if (amount % 100f != 0f) {
            getCustomString(MR.strings.feature_lending_error_multiple_of_hundred)
        } else {
            null
        }
        showErrorMessage(errorMessage)
    }



    private var backPressCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (args.isRepeatWithdrawal) {
                    popBackStack()
                } else {
                    handleBackNavigation()
                }
            }
        }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentSelectLoanAmountBinding
        get() = FragmentSelectLoanAmountBinding::inflate


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        analyticsApi.postEvent(
            LendingEventKeyV2.Lending_RCashAmountScreenShown,
            mapOf(
                LendingEventKeyV2.user_type to getUsertype(),
                LendingEventKeyV2.lender to args.lender.orEmpty()
                )
        )
    }

    override fun setupAppBar() {

    }

    override fun setup(savedInstanceState: Bundle?) {
        getData()
        observeFlow()
        setupListener()
        registerBackPressDispatcher()
    }

    private fun observeFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.preApprovedDataFlow.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        it?.let {
                            setDataOnUi(it)
                        }
                    },
                    onError = { errorMessage, _ ->
                        dismissProgressBar()
                    }
                )
            }
        }
    }

    private fun setDataOnUi(preApprovedData: PreApprovedData) {
        viewModel.preApprovedData = preApprovedData
        setupUI()
        toggleAmountLayout()
    }

    private fun getData() {
        parentViewModel.preApprovedData?.let {
            setDataOnUi(it)
        } ?: run {
            viewModel.fetchPreApproved()
        }
    }

    private fun setupUI() {
        viewModel.preApprovedData?.lenderLogoUrl?.let {
            Glide.with(requireContext()).load(it).into(binding.ivLender)
        }
        binding.etAmount.filters =
            arrayOf(InputFilter.LengthFilter(getMaxAmount().toString().length + 3))
        binding.etAmount.setTypeAmount(shouldAllowDecimal = false)
        //Don't call playLottieWithUrlAndExceptionHandling here since requirement is set url and animate later on.
        binding.noteStackLottie.setAnimationFromUrl(BaseConstants.LottieUrls.NOTE_STACK)
        binding.tvMin.text = getCustomStringFormatted(
            MR.strings.feature_lending_rupee_prefix_string_min,
            viewModel.preApprovedData?.minDrawDown?.toInt().orZero().getFormattedAmount()
        )
        binding.tvMax.text = getCustomStringFormatted(
            MR.strings.feature_lending_rupee_prefix_string_max,
            getMaxAmount().toInt().getFormattedAmount()
        )
        /**
         * we cannot directly set min for seekbar. It is always be zero.
         * So we will reduce min from max value. And result will always be value+min
         * Ex For 3000(min)-10000(max), We will set max as 10000-3000(min)=7000.
         * If seekbar value is 0, it will become 0+min=3000 and for max it will be 7000+min=10000
         */
        binding.seekBar.max =
            (getMaxAmount() - viewModel.preApprovedData?.minDrawDown.orZero()).toInt()
        binding.seekBar.incrementProgressBy(100)
        binding.ivHelp.isVisible = false
        binding.lendingToolbar.root.isVisible = args.screenData?.shouldShowProgress.orFalse().not()
        binding.lendingToolbar.tvTitle.text =
            getCustomString(MR.strings.feature_lending_select_loan_amount)
        EventBus.getDefault()
            .post(LendingToolbarTitleEventV2(getCustomString(MR.strings.feature_lending_loan_application)))
    }

    private fun setupListener() {
        binding.ivHelp.setDebounceClickListener {
            navigateTo(
                LendingStepsNavigationDirections.actionGlobalLendingWebViewFragment(
                    remoteConfigApi.getHelpAndSupportUrl(prefs.getCurrentLanguageCode()),
                    isInAppHelp = true,
                    isMandateFlow = false,
                    fromStepName = LendingStep.CHOOSE_AMOUNT.name
                )
            )
        }

        binding.btnAction.setDebounceClickListener {
            if (isSpecialCase() || validateInput()) {
                val amount = if (isSpecialCase())
                    getMaxAmount()
                else
                    getRawAmount()?.toIntOrNull().orZero()

                val entryType:String = if (isSeekBarScrolled && isAmountEntered) LendingEventKeyV2.AMOUNT_AND_SEEKBAR_BOTH
                else if (isAmountEntered) LendingEventKeyV2.AMOUNT_SELECTION_INPUT_FIELD
                else if (isSeekBarScrolled) LendingEventKeyV2.AMOUNT_SELECTION_SEEKBAR
                else LendingEventKeyV2.NO_SELECTION_OF_FIELD
                analyticsApi.postEvent(
                    event = LendingEventKeyV2.Lending_RCashAmountContinueClicked,
                    values = mapOf(
                        LendingEventKeyV2.amount to amount,
                        LendingEventKeyV2.user_type to getUsertype(),
                        LendingEventKeyV2.entry_type to entryType,
                        LendingEventKeyV2.lender to args.lender.orEmpty()
                    )
                )
                parentViewModel.selectedAmount = amount.toFloat()
                args.screenData?.let {
                    EventBus.getDefault().postSticky(
                        ReadyCashNavigationEvent(
                            whichScreen = it.nextScreen,
                            source = args.screenName,
                            popupToId = R.id.selectLoanAmountFragment
                        )
                    )
                }
            }
        }

        binding.etAmount.setOnClickListener {
            analyticsApi.postEvent(
                event = LendingEventKeyV2.Lending_EntryFieldSelected,
                mapOf(
                    LendingEventKeyV2.field_name to LendingEventKeyV2.AMOUNT_SELECTION_INPUT_FIELD
                )
            )
        }

        binding.etAmount.setOnFocusChangeListener { _, b ->

            if (isInvalidAmount) {
                binding.amountEditBoxHolder.setBackgroundResource(com.jar.app.core_ui.R.drawable.core_ui_bg_outlined_d5cdf2_error_12dp)
            } else {
                if (b) {
                    binding.amountEditBoxHolder.setBackgroundResource(com.jar.app.core_ui.R.drawable.core_ui_bg_outlined_d5cdf2_12dp)
                    analyticsApi.postEvent(
                        event = LendingEventKeyV2.Lending_RCashAmountTyped,
                        values = mapOf(
                            LendingEventKeyV2.amount to getRawAmount().orEmpty(),
                            LendingEventKeyV2.user_type to getUsertype()
                        )
                    )
                    isAmountEntered = true
                } else {
                    binding.amountEditBoxHolder.setBackgroundResource(com.jar.app.core_ui.R.drawable.core_ui_bg_outlined_776e94_12dp_2dp_thick)
                }
            }
        }

        binding.root.keyboardVisibilityChanges()
            .debounce(500)
            .onEach { visible ->
                if (visible.not())
                    updateSeekbar()
            }
            .launchIn(uiScope)

        binding.etAmount.addTextChangedListener(textWatcher)
        binding.etAmount.addTextChangedListener(AmountTextFormatter(binding.etAmount))

        binding.seekBar.setOnSeekBarChangeListener(seekChangeListener)
        binding.lendingToolbar.btnBack.setDebounceClickListener {
            analyticsApi.postEvent(
                LendingEventKeyV2.Lending_RepeatWBackButtonClicked,
                mapOf(LendingEventKeyV2.screen_name to LendingEventKeyV2.amount_screen)
            )
            handleBackNavigation()
        }
        binding.lendingToolbar.btnNeedHelp.setDebounceClickListener {
            analyticsApi.postEvent(
                LendingEventKeyV2.Lending_RCashNeedHelpClicked,
                mapOf(
                    LendingEventKeyV2.screen_name to LendingEventKeyV2.Lending_LoanAmountScreenShown,
                    LendingEventKeyV2.user_type to getUsertype()
                )
            )
            val message = getCustomStringFormatted(
                MR.strings.feature_lending_jar_ready_cash_need_help_template,
                getCustomString(MR.strings.feature_lending_i_need_help_regarding_choose_ammount),
                prefs.getUserName().orEmpty(),
                prefs.getUserPhoneNumber().orEmpty()
            )
            requireContext().openWhatsapp(remoteConfigApi.getWhatsappNumber(), message)
        }

        binding.clMainContent.setOnClickListener {
            binding.etAmount.hideKeyboard()
        }
    }

    //When grant amount is lowest possible value
    private fun isSpecialCase() =
        (getMaxAmount() <= viewModel.preApprovedData?.minDrawDown.orZero())

    private fun toggleAmountLayout() {
        val isSpecialCase = isSpecialCase()
        binding.amountEditBoxHolder.isVisible = !isSpecialCase
        binding.seekBar.isVisible = !isSpecialCase
        binding.tvMax.isVisible = !isSpecialCase
        binding.tvMin.isVisible = !isSpecialCase
        binding.tvAmount.isVisible = isSpecialCase
        binding.tvEligible.isVisible = isSpecialCase

        if (isSpecialCase) {
            prefs.getUserStringSync()?.let {
                val user = serializer.decodeFromString<User?>(it)
                binding.tvGreetings.text =
                    getCustomStringFormatted(
                        MR.strings.feature_lending_hey_x_congo,
                        user?.firstName.orEmpty()
                    )
            }
            binding.tvAmount.text =
                getCustomStringFormatted(
                    MR.strings.feature_lending_rupee_prefix_float,
                    getMaxAmount().toFloat()
                )
        } else {
            binding.tvGreetings.text =
                getCustomString(MR.strings.feature_lending_enter_amount)
            val amount = if (parentViewModel.selectedAmount > 0) {
                parentViewModel.selectedAmount.toInt()
            } else {
                getMaxAmount()
            }
            val formattedAmount = amount.getFormattedAmount()
            binding.etAmount.setText(formattedAmount)
            binding.etAmount.setSelection(formattedAmount.length)
            binding.seekBar.progress = amount
        }
        updateNoteStack()
    }

    private fun getRawAmount() = binding.etAmount.text?.toString()?.replace(",", "")

    private fun updateSeekbar() {
        val value = getRawAmount()?.toIntOrNull()
            ?: viewModel.preApprovedData?.minDrawDown.orZero()
        binding.seekBar.setOnSeekBarChangeListener(null)
        binding.seekBar.progress = (value - viewModel.preApprovedData?.minDrawDown.orZero()).toInt()
        binding.seekBar.setOnSeekBarChangeListener(seekChangeListener)
    }

    private fun validateInput(): Boolean {
        return errorMessage == null
    }

    private fun updateNoteStack() {
        val enteredAmount =
            if (isSpecialCase()) getMaxAmount() else getRawAmount()?.toIntOrNull().orZero()
        val progress = if (enteredAmount <= viewModel.preApprovedData?.minDrawDown.orZero()) 0f
        else if (enteredAmount >= getMaxAmount()) 1f
        else (enteredAmount.toFloat() / getMaxAmount().toFloat())
        binding.noteStackLottie.progress = if (isSpecialCase()) 0.5f else progress
    }

    private fun getMaxAmount(): Int {
        return viewModel.preApprovedData?.maxDrawDown.orZero()
    }

    private fun getMinAmount(): Int {
        return viewModel.preApprovedData?.minDrawDown.orZero()
    }

    private fun registerBackPressDispatcher() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            backPressCallback
        )
    }

    override fun onDestroyView() {
        backPressCallback.isEnabled = false
        binding.etAmount.removeTextChangedListener(textWatcher)
        binding.seekBar.setOnSeekBarChangeListener(null)
        super.onDestroyView()
    }

    private fun handleBackNavigation() {
        EventBus.getDefault()
            .post(LendingBackPressEvent(LendingEventKeyV2.READY_CASH_AMOUNT_SCREEN))

        args.screenData?.let {
            EventBus.getDefault().postSticky(
                ReadyCashNavigationEvent(
                    whichScreen = it.backScreen,
                    source = args.screenName,
                    popupToId = R.id.selectLoanAmountFragment,
                    isBackFlow = true
                )
            )
        }
    }
}