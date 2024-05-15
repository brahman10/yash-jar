package com.jar.app.feature_lending.impl.ui.withdrawal_wait

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.jar.app.base.data.event.GoToHomeEvent
import com.jar.app.base.data.event.LendingToolbarVisibilityEventV2
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.decodeUrl
import com.jar.app.base.util.doRepeatingTask
import com.jar.app.base.util.getLocalString
import com.jar.app.base.util.openWhatsapp
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.core_ui.extension.playLottieUrlSequentially
import com.jar.app.core_ui.extension.playLottieWithUrlAndExceptionHandling
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_lending.LendingStepsNavigationDirections
import com.jar.app.feature_lending.R
import com.jar.app.feature_lending.databinding.FeatureLendingFragmentServerTimeOutOrPendingBinding
import com.jar.app.feature_lending.impl.domain.event.ReadyCashNavigationEvent
import com.jar.app.feature_lending.impl.domain.model.TransitionStateScreenArgs
import com.jar.app.feature_lending.impl.domain.model.experiment.ReadyCashScreen
import com.jar.app.feature_lending.shared.MR
import com.jar.app.feature_lending.shared.domain.LendingEventKeyV2
import com.jar.app.feature_lending.shared.domain.model.experiment.ReadyCashJourney
import com.jar.app.feature_lending.shared.domain.model.experiment.ReadyCashScreenArgs
import com.jar.app.feature_lending.shared.domain.model.temp.LoanStatus
import com.jar.app.feature_lending.shared.domain.model.v2.UpdateLoanDetailsBodyV2
import com.jar.app.feature_lending.shared.domain.model.v2.UpdateStatus
import com.jar.app.feature_lending.shared.util.LendingConstants
import com.jar.app.feature_lending.shared.util.LendingUtil
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
internal class LendingServerTimeOutOrPendingFragment :
    BaseFragment<FeatureLendingFragmentServerTimeOutOrPendingBinding>() {

    companion object {
        const val FLOW_TYPE_BANK_PENDING = 1
        const val FLOW_TYPE_BANK_ERROR = 3
        const val FLOW_TYPE_WITHDRAWAL_SERVER_TIME_OUT = 2
    }

    @Inject
    lateinit var remoteConfigManager: RemoteConfigApi

    @Inject
    lateinit var prefs: PrefsApi

    @Inject
    lateinit var serializer: Serializer

    @Inject
    lateinit var analyticsApi: AnalyticsApi

    private var pollingJob: Job? = null

    private val arguments by navArgs<LendingServerTimeOutOrPendingFragmentArgs>()
    private val args by lazy {
        serializer.decodeFromString<ReadyCashScreenArgs>(
            decodeUrl(arguments.screenArgs)
        )
    }
    private val viewModelProvider: ServerTimeOutOrPendingViewModelAndroid by viewModels { defaultViewModelProviderFactory }
    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }


    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureLendingFragmentServerTimeOutOrPendingBinding
        get() = FeatureLendingFragmentServerTimeOutOrPendingBinding::inflate

    private var backPressCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (arguments.flowType == FLOW_TYPE_WITHDRAWAL_SERVER_TIME_OUT) {
                    EventBus.getDefault().post(GoToHomeEvent("Withdrawal_Error"))
                }
                //Do nothing, user will navigate automatically
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

    override fun setup(savedInstanceState: Bundle?) {
        setupUi()
        initClickListeners()
        observeFlow()
        startPollingTimer()
        registerBackPressDispatcher()
    }

    private fun observeFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.readyCashJourneyFlow.collect(
                    onSuccess = {
                        it?.let {
                            when (arguments.flowType) {
                                FLOW_TYPE_BANK_PENDING -> {
                                    navigateForBank(it)
                                }

                                FLOW_TYPE_WITHDRAWAL_SERVER_TIME_OUT -> {
                                    val status =
                                        it.screenData?.get(ReadyCashScreen.DISBURSAL)?.status
                                    if (LendingUtil.isWithdrawalSuccess(status)) {
                                        navigateAfterWithdrawalSuccess()
                                    }// else hold user to current screen until status get verified.
                                }
                            }
                        }
                    }
                )
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.makeWithdrawalFlow.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        viewModel.fetchLendingProgress()
                    },
                    onError = { errorMessage, _ ->
                        dismissProgressBar()
                    }
                )
            }
        }
    }

    private fun navigateAfterWithdrawalSuccess() {
        navigateTo(
            LendingStepsNavigationDirections.actionGlobalTransitionFragmentState(
                TransitionStateScreenArgs(
                    transitionType = LendingConstants.TransitionType.APPLICATION_SUCCESS,
                    destinationDeeplink = null,
                    flowType = BaseConstants.FROM_LENDING,
                    loanId = args.loanId.orEmpty(),
                    isFromRepeatWithdrawal = args.isRepeatWithdrawal,
                    lender = args.lender
                )
            ),
            popUpTo = R.id.serverTimeOutOrPending,
            inclusive = true
        )
    }

    private fun navigateForBank(readyCashJourney: ReadyCashJourney) {
        val status = readyCashJourney.screenData?.get(ReadyCashScreen.BANK_VERIFICATION)?.status
        if (status == LoanStatus.VERIFIED.name) {
            binding.lottieView.playLottieUrlSequentially(LendingConstants.LottieUrls.SMALL_CHECK)
            uiScope.launch {
                //Done intentionally
                delay(1000)
                args.screenData?.let {
                    EventBus.getDefault().postSticky(
                        ReadyCashNavigationEvent(
                            whichScreen = it.nextScreen,
                            source = args.screenName,
                            popupToId = R.id.serverTimeOutOrPending
                        )
                    )
                }
            }
        } else if (status == LoanStatus.FAILED.name) {
            uiScope.launch {
                delay(1000)
                navigateTo(
                    LendingStepsNavigationDirections.actionGlobalBankApplicationRejectedFragment(args.lender),
                    popUpTo = R.id.serverTimeOutOrPending,
                    inclusive = true
                )
            }
        }
    }


    private fun startPollingTimer() {
        pollingJob?.cancel()
        pollingJob = uiScope.doRepeatingTask(5_000) {
            viewModel.fetchLendingProgress()
        }
    }

    private fun initClickListeners() {
        binding.btnAction.setDebounceClickListener {
            when (arguments.flowType) {
                FLOW_TYPE_BANK_ERROR,
                FLOW_TYPE_BANK_PENDING -> { //go to home
                    analyticsApi.postEvent(
                        LendingEventKeyV2.Lending_ApplicationPendingScreenClicked,
                        mapOf(
                            LendingEventKeyV2.user_type to getUserType(),
                            LendingEventKeyV2.button_type to getCustomLocalizedString(
                                requireContext(),
                                com.jar.app.feature_lending.shared.MR.strings.feature_lending_back_to_home_page,
                                prefs.getCurrentLanguageCode()
                            )
                        )
                    )
                    EventBus.getDefault().post(GoToHomeEvent("PENNY_DROP_PENDING"))
                }

                FLOW_TYPE_WITHDRAWAL_SERVER_TIME_OUT -> { //retry withdrawal
                    analyticsApi.postEvent(
                        LendingEventKeyV2.Lending_ApplicationTimeOutScreenClicked,
                        mapOf(
                            LendingEventKeyV2.user_type to getUserType(),
                            LendingEventKeyV2.button_type to requireContext().getLocalString(com.jar.app.core_ui.R.string.retry)
                        )
                    )
                    viewModel.makeWithdrawal(
                        UpdateLoanDetailsBodyV2(
                            applicationId = args.loanId,
                            withdrawalDetails = UpdateStatus(LoanStatus.IN_PROGRESS.name)
                        )
                    )
                }
            }
        }
        binding.btnContactUs.setDebounceClickListener {
            when (arguments.flowType) {
                FLOW_TYPE_BANK_ERROR,
                FLOW_TYPE_BANK_PENDING -> {
                    analyticsApi.postEvent(
                        LendingEventKeyV2.Lending_ApplicationPendingScreenClicked,
                        mapOf(
                            LendingEventKeyV2.user_type to getUserType(),
                            LendingEventKeyV2.button_type to requireContext().getLocalString(com.jar.app.core_ui.R.string.contact_us)
                        )
                    )
                    val message = getCustomStringFormatted(
                        MR.strings.feature_lending_jar_ready_cash_need_help_template,
                        getCustomString(MR.strings.feature_lending_i_need_help_regarding_my_bank_account),  //feature_lending_i_am_unable_to_proceed_with_my_application
                        prefs.getUserName().orEmpty(),
                        prefs.getUserPhoneNumber().orEmpty()
                    )
                    requireContext().openWhatsapp(remoteConfigManager.getWhatsappNumber(), message)
                }

                FLOW_TYPE_WITHDRAWAL_SERVER_TIME_OUT -> {
                    analyticsApi.postEvent(
                        LendingEventKeyV2.Lending_ApplicationTimeOutScreenClicked,
                        mapOf(
                            LendingEventKeyV2.user_type to getUserType(),
                            LendingEventKeyV2.button_type to requireContext().getLocalString(com.jar.app.core_ui.R.string.contact_us)
                        )
                    )
                    val message = getCustomStringFormatted(
                        MR.strings.feature_lending_jar_ready_cash_need_help_template,
                        getCustomString(MR.strings.feature_lending_i_need_help_regarding_my_bank_account),  //feature_lending_i_am_unable_to_proceed_with_my_application
                        prefs.getUserName().orEmpty(),
                        prefs.getUserPhoneNumber().orEmpty()
                    )
                    requireContext().openWhatsapp(remoteConfigManager.getWhatsappNumber(), message)
                }
            }
        }
        binding.lendingToolbar.btnBack.setDebounceClickListener {
            analyticsApi.postEvent(
                LendingEventKeyV2.Lending_ApplicationTimeOutScreenClicked,
                mapOf(
                    LendingEventKeyV2.user_type to getUserType(),
                    LendingEventKeyV2.button_type to getCustomLocalizedString(
                        requireContext(),
                        com.jar.app.feature_lending.shared.MR.strings.feature_lending_back_to_home_page,
                        prefs.getCurrentLanguageCode()
                    )
                )
            )
            EventBus.getDefault().post(GoToHomeEvent("Withdrawal_Error"))
        }
    }

    private fun setupUi() {
        when (arguments.flowType) {
            FLOW_TYPE_BANK_PENDING -> { //this flow is used in first loan flow
                binding.lendingToolbar.root.isVisible = false
                binding.lottieView.isVisible = true
                binding.ivIllustration.isVisible = false
                binding.lottieView.playLottieWithUrlAndExceptionHandling(
                    requireContext(),
                    LendingConstants.LottieUrls.GENERIC_LOADING
                )
                binding.btnAction.setText(getCustomString(MR.strings.feature_lending_back_to_home_page))
                binding.tvScreenTitle.text =
                    getCustomString(MR.strings.feature_lending_application_pending)
                binding.tvDescription.text =
                    getCustomString(MR.strings.feature_lending_we_will_update_you_soon_with_your_application_status)
                analyticsApi.postEvent(
                    LendingEventKeyV2.Lending_ApplicationPendingScreenShown,
                    mapOf(LendingEventKeyV2.user_type to getUserType())
                )
            }

            FLOW_TYPE_BANK_ERROR -> {
                binding.lendingToolbar.root.isVisible = false
                binding.lottieView.isVisible = true
                binding.ivIllustration.isVisible = false
                binding.lottieView.playLottieWithUrlAndExceptionHandling(
                    requireContext(),
                    LendingConstants.LottieUrls.GENERIC_LOADING
                )
                binding.btnAction.setText(getCustomString(MR.strings.feature_lending_back_to_home_page))
                binding.tvScreenTitle.text =
                    getCustomString(MR.strings.feature_lending_something_went_wrong)
                binding.tvDescription.text =
                    getCustomString(MR.strings.feature_lending_we_apologize_for_inconvenience)
                analyticsApi.postEvent(
                    LendingEventKeyV2.Lending_ApplicationBankErrorScreenShown,
                    mapOf(LendingEventKeyV2.user_type to getUserType())
                )
            }

            FLOW_TYPE_WITHDRAWAL_SERVER_TIME_OUT -> { //this one getting use in repeat withdrawal
                binding.lendingToolbar.root.isVisible = true
                binding.lendingToolbar.tvTitle.text =
                    getCustomString(MR.strings.feature_lending_back_to_home_page)
                binding.lendingToolbar.btnNeedHelp.isVisible = false
                binding.lottieView.isVisible = false
                binding.ivIllustration.isVisible = true
                Glide.with(requireContext())
                    .load(BaseConstants.IllustrationUrls.DEFAULT_LENDING_SERVER_ERROR)
                    .into(binding.ivIllustration)
                binding.btnAction.setText(getString(com.jar.app.core_ui.R.string.retry))
                binding.tvScreenTitle.text =
                    getCustomString(MR.strings.feature_lending_please_try_again)
                binding.tvDescription.text =
                    getCustomString(MR.strings.feature_lending_sorry_unexpected_error)
                analyticsApi.postEvent(
                    LendingEventKeyV2.Lending_ApplicationTimeOutScreenShown,
                    mapOf(LendingEventKeyV2.user_type to getUserType())
                )
                EventBus.getDefault().post(
                    LendingToolbarVisibilityEventV2(
                        shouldHide = true
                    )
                )
            }
        }
    }

    private fun getUserType() = if (args.isRepeatWithdrawal) "Repeat" else "New"
    override fun onDestroyView() {
        pollingJob?.cancel()
        backPressCallback.isEnabled = false
        super.onDestroyView()
    }

    private fun registerBackPressDispatcher() {
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, backPressCallback)
    }
}