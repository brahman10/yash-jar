package com.jar.app.feature_lending.impl.ui.bank.penny_drop

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.jar.app.base.data.event.LendingBackPressEvent
import com.jar.app.base.data.event.LendingToolbarVisibilityEventV2
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.*
import com.jar.app.core_ui.extension.playLottieUrlSequentially
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.core_ui.widget.ProgressViewWithTick
import com.jar.app.feature_lending.LendingStepsNavigationDirections
import com.jar.app.feature_lending.R
import com.jar.app.feature_lending.databinding.FragmentPennyDropVerificationBinding
import com.jar.app.feature_lending.impl.domain.event.ReadyCashNavigationEvent
import com.jar.app.feature_lending.impl.domain.model.experiment.ReadyCashScreen
import com.jar.app.feature_lending.shared.domain.model.experiment.ReadyCashScreenArgs
import com.jar.app.feature_lending.shared.domain.model.temp.LoanStatus
import com.jar.app.feature_lending.shared.domain.model.v2.UpdateLoanDetailsBodyV2
import com.jar.app.feature_lending.impl.ui.withdrawal_wait.LendingServerTimeOutOrPendingFragment
import com.jar.app.feature_lending.shared.domain.LendingEventKeyV2
import com.jar.app.feature_lending.shared.util.LendingConstants
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import javax.inject.Inject
import com.jar.app.feature_lending.shared.MR
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jar_core_network.api.util.collectUnwrapped

@AndroidEntryPoint
internal class PennyDropVerificationFragment :
    BaseFragment<FragmentPennyDropVerificationBinding>() {

    @Inject
    lateinit var analyticsApi: AnalyticsApi

    @Inject
    lateinit var serializer: Serializer

    private val viewModelProvider by viewModels<PennyDropViewModelAndroid> { defaultViewModelProviderFactory }

    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }

    private var timerJob: Job? = null
    private var pollingJob: Job? = null
    private var currentStep = 0
    private var isBankVerificationCalled = false

    private val arguments by navArgs<PennyDropVerificationFragmentArgs>()
    private val args by lazy {
        serializer.decodeFromString<ReadyCashScreenArgs>(
            decodeUrl(arguments.screenArgs)
        )
    }

    private var backPressCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                //Do nothing, user will navigate automatically
                EventBus.getDefault()
                    .post(LendingBackPressEvent(LendingEventKeyV2.PENNYDROP_VERIFICATION_SCREEN))

            }
        }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentPennyDropVerificationBinding
        get() = FragmentPennyDropVerificationBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(
            LendingToolbarVisibilityEventV2(shouldHide = args.screenData?.shouldShowProgress.orFalse().not())
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        analyticsApi.postEvent(LendingEventKeyV2.Lending_PennydropVerificationScreenShown)
        viewModel.fetchReadyCashProgress()
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
        observeFlow()
        registerBackPressDispatcher()
    }

    private fun setupUI() {
        setLoadingUI()
        startTimer()
        startPollingTimer()
    }

    private fun doBankVerification() {
        viewModel.makeBankVerification(
            UpdateLoanDetailsBodyV2(applicationId = args.loanId)
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
                                delay(6000L)//let complete the steps
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
                                it.screenData?.get(ReadyCashScreen.BANK_VERIFICATION)?.status
                            when (status) {
                                LoanStatus.PENDING.name->{
                                    if (isBankVerificationCalled.not()){
                                        doBankVerification()
                                        isBankVerificationCalled = true
                                    }
                                }
                                LoanStatus.VERIFIED.name -> {
                                    binding.lottieView.playLottieUrlSequentially(LendingConstants.LottieUrls.SMALL_CHECK)
                                    binding.step4.updateStatus(
                                        ProgressViewWithTick.STATUS_DONE,
                                        getCustomString(MR.strings.feature_lending_penny_drop_step4)
                                    )
                                    binding.tvVerifying.text =
                                        getCustomString(MR.strings.feature_lending_penny_drop_step4)
                                    uiScope.launch {
                                        //Done intentionally
                                        delay(1000)
                                        args.screenData?.let {
                                            EventBus.getDefault().postSticky(
                                                ReadyCashNavigationEvent(
                                                    whichScreen = it.nextScreen,
                                                    source = args.screenName,
                                                    popupToId = R.id.pennyDropVerificationFragment
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
                                    binding.step4.updateStatus(
                                        ProgressViewWithTick.STATUS_FAILURE,
                                        getCustomString(MR.strings.feature_lending_penny_drop_step4_failure)
                                    )
                                    uiScope.launch {
                                        delay(1000)
                                        navigateTo(
                                            LendingStepsNavigationDirections.actionGlobalBankApplicationRejectedFragment(args.lender),
                                            popUpTo = R.id.pennyDropVerificationFragment,
                                            inclusive = true
                                        )
                                    }
                                }

                                LoanStatus.CALLBACK_PENDING.name -> {
                                    analyticsApi.postEvent(
                                        LendingEventKeyV2.Lending_ApplicationPennyDropCallbackPending,
                                    )
                                    uiScope.launch {
                                        delay(1000)
                                        navigateToErrorOrPending(
                                            LendingServerTimeOutOrPendingFragment.FLOW_TYPE_BANK_PENDING
                                        )
                                    }
                                }
                            }
                        }
                    },
                    onError = { errorMessage, _ ->

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
            popUpTo = R.id.pennyDropVerificationFragment,
            inclusive = true
        )
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = uiScope.countDownTimer(
            totalMillis = 10_000,
            intervalInMillis = 2500,
            onInterval = {
                currentStep++
                when (currentStep) {
                    1 -> {
                        binding.step1.isVisible = true
                        binding.step1.setData(
                            getCustomString(MR.strings.feature_lending_penny_drop_step1_progress),
                            true,
                            true
                        )
                    }
                    2 -> {
                        binding.step1.setData(
                            getCustomString(MR.strings.feature_lending_penny_drop_step1_done),
                            false,
                            false
                        )
                        binding.step2.isVisible = true
                        binding.step2.setData(
                            getCustomString(MR.strings.feature_lending_penny_drop_step2_progress),
                            true,
                            true
                        )
                    }
                    3 -> {
                        binding.step2.setData(
                            getCustomString(MR.strings.feature_lending_penny_drop_step2_done),
                            false,
                            false
                        )
                        binding.step3.isVisible = true
                        binding.step3.setData(
                            getCustomString(MR.strings.feature_lending_penny_drop_step3),
                            true,
                            true
                        )
                    }
                    4 -> {
                        binding.step3.setData(
                            getCustomString(MR.strings.feature_lending_penny_drop_step3),
                            false,
                            false
                        )
                        binding.step4.isVisible = true
                        binding.step4.setData(
                            getCustomString(MR.strings.feature_lending_penny_drop_step4),
                            true,
                            true
                        )
                    }
                }
            }
        )
    }

    private fun startPollingTimer() {
        pollingJob?.cancel()
        pollingJob = uiScope.doRepeatingTask(3_000) {
            viewModel.fetchReadyCashProgress()
        }
    }

    private fun setLoadingUI() {
        binding.lottieView.playLottieUrlSequentially(LendingConstants.LottieUrls.SEARCHING_LOADER)
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