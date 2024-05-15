package com.jar.app.feature_lending.impl.ui.bank.waiting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import com.jar.app.base.data.event.LendingBackPressEvent
import com.jar.app.base.data.event.LendingToolbarVisibilityEventV2
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.countDownTimer
import com.jar.app.base.util.decodeUrl
import com.jar.app.base.util.doRepeatingTask
import com.jar.app.base.util.encodeUrl
import com.jar.app.base.util.openWhatsapp
import com.jar.app.base.util.orFalse
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.core_ui.extension.playLottieUrlSequentially
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.feature_lending.LendingStepsNavigationDirections
import com.jar.app.feature_lending.R
import com.jar.app.feature_lending.databinding.FeatureLendingFragmentAbflWaitingBinding
import com.jar.app.feature_lending.impl.domain.event.ReadyCashNavigationEvent
import com.jar.app.feature_lending.impl.domain.model.experiment.ReadyCashScreen
import com.jar.app.feature_lending.impl.ui.bank.penny_drop.PennyDropViewModelAndroid
import com.jar.app.feature_lending.impl.ui.withdrawal_wait.LendingServerTimeOutOrPendingFragment
import com.jar.app.feature_lending.shared.MR
import com.jar.app.feature_lending.shared.domain.LendingEventKeyV2
import com.jar.app.feature_lending.shared.domain.model.experiment.ReadyCashScreenArgs
import com.jar.app.feature_lending.shared.domain.model.temp.LoanStatus
import com.jar.app.feature_lending.shared.domain.model.v2.BankVerificationDetails
import com.jar.app.feature_lending.shared.domain.model.v2.UpdateLoanDetailsBodyV2
import com.jar.app.feature_lending.shared.util.LendingConstants
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@AndroidEntryPoint
internal class ABFLWaitingFragment : BaseFragment<FeatureLendingFragmentAbflWaitingBinding>() {

    @Inject
    lateinit var analyticsApi: AnalyticsApi

    @Inject
    lateinit var serializer: Serializer

    @Inject
    lateinit var remoteConfigApi: RemoteConfigApi

    @Inject
    lateinit var prefs: PrefsApi

    private var timerJob: Job? = null
    private var pollingJob: Job? = null
    private var isBankVerificationCalled = false
    private val viewModelProvider by viewModels<PennyDropViewModelAndroid> { defaultViewModelProviderFactory }

    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }
    private val arguments by navArgs<ABFLWaitingFragmentArgs>()
    private val args by lazy {
        serializer.decodeFromString<ReadyCashScreenArgs>(
            decodeUrl(arguments.screenArgs)
        )
    }

    companion object {
        private const val UI_STATE_VERIFYING_DETAILS = 1
        private const val UI_STATE_BANK_MISMATCH = 2
        private const val UI_STATE_APPLICATION_PENDING = 3
    }

    private var backPressCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                //Do nothing, user will navigate automatically
                EventBus.getDefault()
                    .post(LendingBackPressEvent(LendingEventKeyV2.PENNYDROP_VERIFICATION_SCREEN))

            }
        }
    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureLendingFragmentAbflWaitingBinding
        get() = FeatureLendingFragmentAbflWaitingBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(
            LendingToolbarVisibilityEventV2(
                shouldHide = args.screenData?.shouldShowProgress.orFalse().not()
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.fetchReadyCashProgress()
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
        observeFlow()
        setClickListener()
        registerBackPressDispatcher()
    }

    private fun setClickListener() {
        binding.btnGoToHome.setOnClickListener {
            sendEvent(LendingEventKeyV2.GO_TO_HOMESCREEN_CLICKED)
            val fragment =
                (parentFragment as NavHostFragment).parentFragment //this returns LendingHostContainerFragment
            fragment?.popBackStack()
        }
        binding.lendingToolbar.btnNeedHelp.setDebounceClickListener {
            sendEvent(LendingEventKeyV2.NEED_HELP_CLICKED)
            val message = getCustomStringFormatted(
                MR.strings.feature_lending_jar_ready_cash_need_help_template,
                getCustomString(MR.strings.feature_lending_i_need_help_regarding_bank_verification_pending),
                prefs.getUserName().orEmpty(),
                prefs.getUserPhoneNumber().orEmpty()
            )
            requireContext().openWhatsapp(remoteConfigApi.getWhatsappNumber(), message)
        }
    }

    private fun sendEvent(action:String){
        analyticsApi.postEvent(
            event = LendingEventKeyV2.Lending_ABFLWaitingScreen,
            values = mapOf(
                LendingEventKeyV2.action to action,
                LendingEventKeyV2.source to args.source,
                LendingEventKeyV2.screen_title to binding.tvStatusTitle.text.toString(),
                LendingEventKeyV2.screen_text to binding.tvDescription.text.toString(),
                LendingEventKeyV2.lender to args.lender.orEmpty(),
            )
        )
    }
    private fun setupUI() {
        changeUiTo(UI_STATE_VERIFYING_DETAILS)
        startTimer()
    }

    private fun doBankVerification() {
        viewModel.makeBankVerification(
            UpdateLoanDetailsBodyV2(
                applicationId = args.loanId,
                bankVerificationDetails = BankVerificationDetails(status = LoanStatus.IN_PROGRESS.name)
            )
        )
    }

    private fun observeFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.bankVerificationFlow.collect(
                    onSuccess = {
                        //ignore
                    },
                    onError = { message, errorCode ->
                        if (errorCode == LendingConstants.BANK_VERIFICATION_ERROR_CODE) {
                            uiScope.launch {
                                navigateToErrorOrPending(LendingServerTimeOutOrPendingFragment.FLOW_TYPE_BANK_ERROR)
                            }
                        } else {
                            message.snackBar(binding.root)
                        }
                    }
                )
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.readyCashJourneyFlow.collect(
                    onSuccess = {
                        it?.let {
                            val status =
                                it.screenData?.get(ReadyCashScreen.BANK_VERIFICATION_V2)?.status
                            when (status) {
                                LoanStatus.PENDING.name -> {
                                    if (isBankVerificationCalled.not()) {
                                        doBankVerification()
                                        isBankVerificationCalled = true
                                    }
                                }

                                LoanStatus.VERIFIED.name -> {
                                    binding.lottieView.playLottieUrlSequentially(LendingConstants.LottieUrls.SMALL_CHECK)
                                    uiScope.launch {
                                        //Done intentionally
                                        delay(1000)
                                        args.screenData?.let {
                                            EventBus.getDefault().postSticky(
                                                ReadyCashNavigationEvent(
                                                    whichScreen = it.nextScreen,
                                                    source = args.screenName,
                                                    popupToId = R.id.abflWaitingFragment
                                                )
                                            )
                                        }
                                    }
                                }

                                LoanStatus.FAILED.name -> {
                                    analyticsApi.postEvent(
                                        LendingEventKeyV2.Lending_ApplicationRejected,
                                        mapOf(LendingEventKeyV2.lender to args.lender.orEmpty())
                                    )
                                    uiScope.launch {
                                        delay(1000)
                                        navigateTo(
                                            LendingStepsNavigationDirections.actionGlobalBankApplicationRejectedFragment(
                                                args.lender
                                            ),
                                            popUpTo = R.id.abflWaitingFragment,
                                            inclusive = true
                                        )
                                    }
                                }

                                LoanStatus.CALLBACK_PENDING.name -> {
                                    analyticsApi.postEvent(
                                        LendingEventKeyV2.Lending_ApplicationPennyDropCallbackPending,
                                        mapOf(LendingEventKeyV2.lender to args.lender.orEmpty())
                                    )
                                    uiScope.launch {
                                        changeUiTo(UI_STATE_APPLICATION_PENDING)
                                    }
                                }
                            }
                        }
                    },
                    onError = { _, _ ->

                    }
                )
            }
        }
    }

    private fun navigateToErrorOrPending(flowType: Int) {
        val argsData = encodeUrl(
            serializer.encodeToString(
                ReadyCashScreenArgs(
                    loanId = args.loanId,
                    source = args.source,
                    type = args.type,
                    screenName = args.screenName,
                    screenData = args.screenData,
                    isRepeatWithdrawal = args.isRepeatWithdrawal,
                    isRepayment = args.isRepayment
                )
            )
        )
        navigateTo(
            LendingStepsNavigationDirections.actionGlobalServerTimeOutOrPending(
                flowType = flowType,
                screenArgs = argsData
            ),
            popUpTo = R.id.abflWaitingFragment,
            inclusive = true
        )
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = uiScope.countDownTimer(
            totalMillis = 60_000,
            onInterval = {
                val secondsLeft = (it / 1000).toInt()
                if (secondsLeft == 45) { //start polling after waiting 15 sec.
                    startPollingTimer()
                } else {
                    binding.tvDescription.text = getCustomStringFormatted(
                        MR.strings.feature_lending_only_s_seconds_left_for_verification,
                        if (secondsLeft < 10) "0$secondsLeft" else "$secondsLeft"
                    )
                }
            },
            onFinished = {
                changeUiTo(UI_STATE_APPLICATION_PENDING)
            }
        )
    }

    private fun changeUiTo(state: Int) {
        when (state) {
            UI_STATE_VERIFYING_DETAILS -> {
                binding.lendingToolbar.root.isVisible = false
                binding.clVerifyingOrPending.isVisible = true
                binding.btnGoToHome.isVisible = false
                binding.lottieView.playLottieUrlSequentially(LendingConstants.LottieUrls.SEARCHING_LOADER)
                binding.tvStatusTitle.text =
                    getCustomString(MR.strings.feature_lending_verifying_your_bank_details)
                binding.tvDescription.text = getCustomStringFormatted(
                    MR.strings.feature_lending_only_s_seconds_left_for_verification,
                    "60"
                )
                sendEvent(LendingEventKeyV2.shown)
            }

            UI_STATE_APPLICATION_PENDING -> {
                timerJob?.cancel()
                startPollingTimer()
                binding.lendingToolbar.root.isVisible = true
                binding.btnGoToHome.isVisible = true
                binding.lendingToolbar.btnBack.isInvisible = true
                binding.lendingToolbar.tvTitle.isInvisible = true
                binding.clVerifyingOrPending.isVisible = true
                binding.tvStatusTitle.text =
                    getCustomString(MR.strings.feature_lending_application_pending)
                binding.tvDescription.text =
                    getCustomString(MR.strings.feature_lending_uh_oh_its_taking_longer_than_expected)
                sendEvent(LendingEventKeyV2.shown_pending)
            }

            UI_STATE_BANK_MISMATCH -> {
                //Not required as of now. Product requirement changed.
            }
        }
    }

    private fun startPollingTimer() {
        pollingJob?.cancel()
        pollingJob = uiScope.doRepeatingTask(5_000) {
            viewModel.fetchReadyCashProgress()
        }
    }

    private fun registerBackPressDispatcher() {
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, backPressCallback)
    }

    override fun onDestroyView() {
        timerJob?.cancel()
        backPressCallback.isEnabled = false
        super.onDestroyView()
    }
}