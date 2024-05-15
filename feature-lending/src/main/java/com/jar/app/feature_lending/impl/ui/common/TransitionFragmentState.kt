package com.jar.app.feature_lending.impl.ui.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.jar.app.base.data.event.LendingBackPressEvent
import com.jar.app.base.data.event.LendingToolbarVisibilityEventV2
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.encodeUrl
import com.jar.app.core_base.domain.model.User
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_ui.extension.playLottieWithUrlAndExceptionHandling
import com.jar.app.feature_lending.LendingStepsNavigationDirections
import com.jar.app.feature_lending.R
import com.jar.app.feature_lending.databinding.FragmentTransitionStateBinding
import com.jar.app.feature_lending.impl.domain.event.ReadyCashNavigationEvent
import com.jar.app.feature_lending.impl.domain.model.TransitionStateScreenArgs
import com.jar.app.feature_lending.impl.domain.model.experiment.ReadyCashScreen
import com.jar.app.feature_lending.shared.domain.model.experiment.ReadyCashScreenArgs
import com.jar.app.feature_lending.shared.domain.model.experiment.ScreenData
import com.jar.app.feature_lending.shared.domain.model.temp.LoanStatus
import com.jar.app.feature_lending.shared.util.LendingConstants
import com.jar.app.feature_lending.shared.domain.LendingEventKeyV2
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject
import com.jar.app.feature_lending.shared.MR

@AndroidEntryPoint
internal class TransitionFragmentState : BaseFragment<FragmentTransitionStateBinding>() {

    @Inject
    lateinit var prefs: PrefsApi

    @Inject
    lateinit var serializer: Serializer

    @Inject
    lateinit var analyticsApi: AnalyticsApi

    private val args by navArgs<TransitionFragmentStateArgs>()

    private var backPressCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                //Do nothing, user will be automatically redirected
                when (type) {
                    LendingConstants.TransitionType.OTP_SUCCESS -> {
                    }

                    LendingConstants.TransitionType.APPLICATION_SUCCESS -> {
                        EventBus.getDefault()
                            .post(LendingBackPressEvent(LendingEventKeyV2.LOAN_APPLICATION_SUCCESS_SCREEN))
                    }

                    LendingConstants.TransitionType.ALL_DONE -> {
                        EventBus.getDefault()
                            .post(LendingBackPressEvent(LendingEventKeyV2.LOAN_ALL_SET_SCREEN))
                    }
                }
            }
        }

    private val type by lazy {
        args.screenArgs.transitionType
    }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentTransitionStateBinding
        get() = FragmentTransitionStateBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(
            UpdateAppBarEvent(
                AppBarData(
                    ToolbarNone
                )
            )
        )
        EventBus.getDefault().post(
            LendingToolbarVisibilityEventV2(shouldHide = true)
        )
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
        registerBackPressDispatcher()
    }

    private fun setupUI() {
        var lottieUrl = ""
        when (type) {
            LendingConstants.TransitionType.OTP_SUCCESS -> {
                lottieUrl = LendingConstants.LottieUrls.SMALL_CHECK
                binding.tvSuccessTitle.text = getCustomString(MR.strings.feature_lending_otp_verified)
                binding.tvSuccessDesc.text = getCustomString(MR.strings.feature_lending_agreement_signed)
                binding.tvSuccessTitle.isVisible = true
                binding.tvSuccessDesc.isVisible = true
            }
            LendingConstants.TransitionType.APPLICATION_SUCCESS -> {
                analyticsApi.postEvent(
                    LendingEventKeyV2.Lending_LoanApplicationSuccessfulScreenShown,
                    mapOf(
                        LendingEventKeyV2.user_type to getUserType(),
                        LendingEventKeyV2.lender to args.screenArgs.lender.orEmpty()
                        )
                )
                val user = serializer.decodeFromString<User?>(prefs.getUserStringSync().orEmpty())
                lottieUrl = LendingConstants.LottieUrls.TICK_WITH_CELEBRATION
                binding.tvGreetings.text = getCustomStringFormatted(MR.strings.feature_lending_congo_x, user?.firstName.orEmpty())
                binding.tvSuccessTitle.text =
                    getCustomString(MR.strings.feature_lending_application_successful)
                binding.tvSuccessDesc.text =
                    getCustomString(MR.strings.feature_lending_loan_will_credited_within_24_hrs)
                binding.tvGreetings.isVisible = true
                binding.tvSuccessTitle.isVisible = true
                binding.tvSuccessDesc.isVisible = true
            }
            LendingConstants.TransitionType.ALL_DONE -> {
                lottieUrl = LendingConstants.LottieUrls.TICK_WITH_CELEBRATION
                binding.tvSuccessTitle.text = getCustomString(MR.strings.feature_lending_you_are_all_set)
                binding.tvSuccessDesc.isVisible = false
            }
        }
        binding.lottieView.playLottieWithUrlAndExceptionHandling(requireContext(), lottieUrl)
        redirectWithDelay()
    }
    private fun getUserType() = if (args.screenArgs.isFromRepeatWithdrawal) "Repeat" else "New"
    private fun redirectWithDelay() {
        uiScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                delay(2000)
                if (type == LendingConstants.TransitionType.OTP_SUCCESS) {
                    if (args.screenArgs.isFromRepeatWithdrawal){
                        navigateTo(
                            LendingStepsNavigationDirections.actionGlobalTransitionFragmentState(
                                TransitionStateScreenArgs(
                                    transitionType = LendingConstants.TransitionType.APPLICATION_SUCCESS,
                                    destinationDeeplink = null,
                                    flowType = args.screenArgs.flowType,
                                    loanId = args.screenArgs.loanId,
                                    isFromRepeatWithdrawal = args.screenArgs.isFromRepeatWithdrawal,
                                    lender = args.screenArgs.lender
                                )
                            ),
                            popUpTo = R.id.transitionFragmentState,
                            inclusive = true
                        )
                    }else{
                        val argsData = encodeUrl(
                            serializer.encodeToString(
                                ReadyCashScreenArgs(
                                    loanId = args.screenArgs.loanId,
                                    source = args.screenArgs.transitionType,
                                    type = args.screenArgs.flowType,
                                    screenName = ReadyCashScreen.LOAN_AGREEMENT,
                                    screenData = ScreenData(
                                        shouldShowProgress = false,
                                        backScreen = ReadyCashScreen.HOME_SCREEN,
                                        nextScreen = ReadyCashScreen.READY_CASH_DETAILS,
                                        status = LoanStatus.PENDING.name
                                    ),
                                    isRepeatWithdrawal = args.screenArgs.isFromRepeatWithdrawal,
                                    isRepayment = false
                                )
                            )
                        )
                        navigateTo(
                            LendingStepsNavigationDirections.actionGlobalLendingWithdrawalWaitFragment(
                                argsData
                            ),
                            popUpTo = R.id.transitionFragmentState,
                            inclusive = true
                        )
                    }
                } else if (!args.screenArgs.destinationDeeplink.isNullOrEmpty()) {
                    navigateTo(
                        uri = args.screenArgs.destinationDeeplink!!,
                        popUpTo = R.id.transitionFragmentState,
                        inclusive = true
                    )
                } else if (type == LendingConstants.TransitionType.APPLICATION_SUCCESS){
                    EventBus.getDefault().postSticky(
                        ReadyCashNavigationEvent(
                            whichScreen = ReadyCashScreen.READY_CASH_DETAILS,
                            source = args.screenArgs.transitionType,
                            popupToId = R.id.transitionFragmentState,
                            isBackFlow = false,
                        )
                    )
                }else if (type == LendingConstants.TransitionType.ALL_DONE){
                    analyticsApi.postEvent(
                        LendingEventKeyV2.Lending_YourLoanApproved,
                        mapOf(
                            LendingEventKeyV2.user_type to getUserType(),
                            LendingEventKeyV2.loan_id to args.screenArgs.loanId,
                            LendingEventKeyV2.isFromRepeatWithdrawal to args.screenArgs.isFromRepeatWithdrawal,
                            LendingEventKeyV2.lender to args.screenArgs.lender.orEmpty()
                        )
                    )
                    EventBus.getDefault().postSticky(
                        ReadyCashNavigationEvent(
                            whichScreen = ReadyCashScreen.DISBURSAL,
                            source = args.screenArgs.transitionType,
                            popupToId = R.id.transitionFragmentState,
                            isBackFlow = false,
                        )
                    )
                }
            }
        }
    }

    private fun registerBackPressDispatcher() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            backPressCallback
        )
    }

    override fun onDestroyView() {
        backPressCallback.isEnabled = false
        super.onDestroyView()
    }
}