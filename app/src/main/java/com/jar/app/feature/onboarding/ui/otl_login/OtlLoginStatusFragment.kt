package com.jar.app.feature.onboarding.ui.otl_login

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.jar.app.HomeNavigationDirections
import com.jar.app.R
import com.jar.app.base.data.event.SyncDeviceDetailsEvent
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.exponentialTimer
import com.jar.app.base.util.toFloatOrZero
import com.jar.app.core_analytics.EventKey
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.orFalse
import com.jar.app.core_base.util.orZero
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_ui.extension.playLottieWithUrlAndExceptionHandling
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.databinding.FragmentOtlLoginStatusBinding
import com.jar.app.feature.home.ui.activity.HomeActivityViewModel
import com.jar.app.feature.onboarding.NewOnboardingViewModelAndroid
import com.jar.app.feature_onboarding.shared.data.state_machine.OnboardingStateMachine
import com.jar.app.feature_onboarding.shared.util.OnboardingConstants
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@AndroidEntryPoint
class OtlLoginStatusFragment : BaseFragment<FragmentOtlLoginStatusBinding>() {
    //OTL = One Tap Login

    @Inject
    lateinit var onboardingStateMachine: OnboardingStateMachine

    @Inject
    lateinit var prefs: PrefsApi

    @Inject
    lateinit var analyticsApi: AnalyticsApi

    @Inject
    lateinit var appScope: CoroutineScope

    private val timeInit = System.currentTimeMillis()
    private var countDownJob: Job? = null
    private var isSuccessViewBeingShown = false
    private var pollingCount = 1

    private val addAnimatorUpdateListener = ValueAnimator.AnimatorUpdateListener {
        val progress = (it.animatedValue.toString().toFloatOrZero() * 100).toInt()
        if (progress == 100 && isSuccessViewBeingShown) {
            navigateToNextStep()
            onboardingStateMachine.navigateAfterTruecallerCompletion()
        }
    }

    companion object {
        private const val INITIAL_TIMER_VALUE = 1000L
    }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentOtlLoginStatusBinding
        get() = FragmentOtlLoginStatusBinding::inflate

    private val homeViewModel by activityViewModels<HomeActivityViewModel> { defaultViewModelProviderFactory }

    private val newOnboardingViewModelProvider by activityViewModels<NewOnboardingViewModelAndroid>()

    private val newOnboardingViewModel by lazy {
        newOnboardingViewModelProvider.getInstance()
    }

    private val viewModelProvider by viewModels<OtlLoginStatusViewModelAndroid> { defaultViewModelProviderFactory }

    private val viewModel by lazy { viewModelProvider.getInstance() }

    private val args by navArgs<OtlLoginStatusFragmentArgs>()
    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData()))
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
        observeFlow()
    }

    private fun setupUI() {
        newOnboardingViewModel.timeSpentMap
        startOtlStatusTimer()
        showLoadingView()
    }

    private fun observeFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.otlLoginFlow.collect(
                    onSuccess = {
                        it?.let {
                            if (it.hasOtherActiveSessions.orFalse()) {
                                navigateTo(
                                    HomeNavigationDirections.actionToActiveSessionDetectedFragment(
                                        null
                                    ),
                                    false
                                )
                            } else {
                                homeViewModel.numberOfDaysOfSms =
                                    it.numberOfDaysOfSms
                                        ?: BaseConstants.DEFAULT_VALUES_FOR_NO_OF_SMS_SYNC
                                newOnboardingViewModel.saveUserData(it)
                                onboardingStateMachine.mUser = it.user

                                //Possible values of authType - SIGNUP/LOGIN  (Used to track re logins)
                                prefs.setAuthType(it.authType.orEmpty())
                                showSuccessView()
                                countDownJob?.cancel()
                                if (it.authType.orEmpty() == BaseConstants.LOGIN) {
                                    analyticsApi.postEvent(
                                        OnboardingConstants.AnalyticsKeys.OLD_USER_LOGIN,
                                        mapOf(OnboardingConstants.AnalyticsKeys.Login_type to OnboardingConstants.AnalyticsKeys.OTL)
                                    )
                                    prefs.setAppWalkThroughShownToUser(true)
                                } else if (it.authType.orEmpty() == BaseConstants.SIGNUP) {
                                    analyticsApi.postEvent(
                                        OnboardingConstants.AnalyticsKeys.NEW_USER_SIGNUP,
                                        mapOf(OnboardingConstants.AnalyticsKeys.Login_type to OnboardingConstants.AnalyticsKeys.OTL)
                                    )
                                    analyticsApi.postEvent(
                                        EventKey.NEW_USER_SIGNUP_MOBILE,
                                        mapOf(OnboardingConstants.AnalyticsKeys.Login_type to OnboardingConstants.AnalyticsKeys.OTL)
                                    )
                                    prefs.setAppWalkThroughShownToUser(false)
                                }

                                prefs.setUserPhoneNumber(it.user.phoneNumber)

                                //Used to track logins
                                analyticsApi.postEvent(
                                    OnboardingConstants.AnalyticsKeys.AUTH_SUCCESSFUL,
                                    mapOf(
                                        OnboardingConstants.AnalyticsKeys.PROP_SOURCE to OnboardingConstants.AnalyticsKeys.TRUECALLER,
                                        OnboardingConstants.AnalyticsKeys.AuthType to it.authType.orEmpty()
                                    )
                                )
                                EventBus.getDefault().post(SyncDeviceDetailsEvent())

                            }
                        } ?: run {
                            if (pollingCount > 2)
                                showFailureViewAndNavigate()
                        }
                    },
                    onSuccessWithNullData = {
                        if (pollingCount > 2)
                            showFailureViewAndNavigate()
                    },
                    onError = { errorMessage, _ ->
                        if (pollingCount > 2)
                            showFailureViewAndNavigate()
                        errorMessage.snackBar(binding.root)
                    }
                )
            }
        }
    }

    private fun showLoadingView() {
        binding.apply {
            if (isSuccessViewBeingShown.not()) {
                loadingLottie.isVisible = true
                successLottie.isVisible = false
                ivIllustration.isVisible = false
                tvDesc.isVisible = false
                loadingLottie.playLottieWithUrlAndExceptionHandling(
                    requireContext(), BaseConstants.LottieUrls.CIRCULAR_LOADER_ANIMATION
                )
                tvTitle.text =
                    getString(R.string.feature_onboarding_we_are_verifying_your_number)
            }
        }
    }

    private fun showSuccessView() {
        binding.apply {
            loadingLottie.cancelAnimation()
            successLottie.setAnimation(com.jar.app.core_ui.R.raw.tick)
            successLottie.playAnimation()
            successLottie.addAnimatorUpdateListener(addAnimatorUpdateListener)
            isSuccessViewBeingShown = true
            successLottie.isVisible = true
            loadingLottie.isVisible = false
            ivIllustration.isVisible = false
            tvDesc.isVisible = false
            tvTitle.text = getString(R.string.feature_onboarding_your_account_has_been_verified)
        }
    }

    private fun showFailureViewAndNavigate() {
        binding.apply {
            successLottie.isVisible = false
            loadingLottie.isVisible = false
            ivIllustration.isVisible = true
            tvDesc.isVisible = true
            tvTitle.text = getString(R.string.feature_onboarding_please_verify_your_number)
            tvDesc.text = getString(
                R.string.feature_onboarding_we_have_sent_otp_at, args.countryCode, args.phoneNumber
            )
            uiScope.launch {
                delay(1000)
                navigateToNextStep()
            }
        }
    }

    private fun startOtlStatusTimer() {
        countDownJob?.cancel()
        countDownJob = appScope.launch {
            while (isActive) { // will continue until the coroutine is cancelled
                fetchOtlStatus()
                delay(INITIAL_TIMER_VALUE.exponentialTimer(1000L, pollingCount++))
            }
        }
    }

    private fun fetchOtlStatus() {
        viewModel.fetchOTLUserInfo(
            args.hasExperianConsent, args.phoneNumber, args.countryCode, args.correlationId
        )
    }

    private fun navigateToNextStep() {
        onboardingStateMachine.navigateAhead()
    }

    override fun onStop() {
        super.onStop()
        countDownJob?.cancel()
    }

    override fun onDestroyView() {
        analyticsApi.postEvent(
            OnboardingConstants.AnalyticsKeys.Exit_OtlStatusScreen_Onboarding,
            mapOf(OnboardingConstants.AnalyticsKeys.TIME_SPENT to System.currentTimeMillis() - timeInit)
        )
        val endTime = System.currentTimeMillis()
        newOnboardingViewModel.updateScreenTime(
            screenName = OnboardingStateMachine.State.OtlLogin,
            timeSpentOnScreen = timeInit.orZero().minus(endTime)
        )
        super.onDestroyView()
    }
}