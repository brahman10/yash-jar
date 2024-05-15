package com.jar.app.feature.splash

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.asLiveData
import androidx.work.WorkManager
import com.bumptech.glide.Glide
import com.jar.app.JarApp
import com.jar.app.R
import com.jar.app.RedirectionNavigationDirections
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.showToast
import com.jar.app.core_analytics.EventKey
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.core_remote_config.RemoteConfigParam
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.core_utils.data.BiometricUtil
import com.jar.app.databinding.FragmentRedirectionBinding
import com.jar.app.feature.onboarding.NewOnboardingViewModelAndroid
import com.jar.app.util.NotificationUtil
import com.jar.app.worker.ConfigInitializer
import com.jar.app.worker.ScheduleNotificationWorker
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
internal class SplashFragment : BaseFragment<FragmentRedirectionBinding>(),
    BiometricUtil.AuthenticationListener {

    @Inject
    lateinit var analyticsApi: AnalyticsApi

    @Inject
    lateinit var prefsApi: PrefsApi

    @Inject
    lateinit var remoteConfigApi: RemoteConfigApi

    @Inject
    lateinit var configInitializer: ConfigInitializer

    @Inject
    lateinit var biometricUtil: BiometricUtil

    @Inject
    lateinit var workManager: WorkManager

    @Inject
    lateinit var notificationUtil: NotificationUtil

    @Inject
    lateinit var onboardingStateMachine: com.jar.app.feature_onboarding.shared.data.state_machine.OnboardingStateMachine

    private val newOnboardingViewModelProvider by activityViewModels<NewOnboardingViewModelAndroid>()

    private val newOnboardingViewModel by lazy{
        newOnboardingViewModelProvider.getInstance()
    }

    private var isConfigFetched: Boolean = false

    private var isAnimationCompleted: Boolean = false

    private val animationListener = ValueAnimator.AnimatorUpdateListener {
        if (viewLifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.CREATED)) {
            if (it.animatedFraction > 0.6f) {
                binding.groupBottom.isVisible = true
            }
            if (it.animatedFraction >= 1f) {
                isAnimationCompleted = true
                binding.lottieView.pauseAnimation()
                if (shouldAuthenticate()) {
                    authenticate()
                } else {
                    if (isConfigFetched)
                        uiScope.launch {
                            delay(500)
                            redirect()
                        }
                }
            }
        }
    }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentRedirectionBinding
        get() = FragmentRedirectionBinding::inflate

    override fun setupAppBar() {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        if(prefsApi.shouldShowSplashScreen().not()) {
            prefsApi.setShouldShowSplashScreen(true)
            redirect()
        }
        super.onCreate(savedInstanceState)
        prefsApi.setRemoteConfigEventSent(false)
        getData()
    }

    override fun setup(savedInstanceState: Bundle?) {
        analyticsApi.postEvent(EventKey.SHOWN_SPLASH_SCREEN)
        observeLiveData()
        startAnimation()

        if (remoteConfigApi.isFestivalCampaignEnabled()) {
            Glide.with(requireContext())
                .load(remoteConfigApi.getDiwaliSplashScreenAsset())
                .placeholder(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.splash_screen_placeholder
                    )
                )
                .into(binding.ivDiwaliAsset)
        } else {
            binding.ivDiwaliAsset.isVisible = false
        }

        configInitializer.fetchConfig(
            onSuccess = {
                configFetched()
            },
            onError = {
                configFetched()
            }
        )
    }

    private fun configFetched() {
        sendEventForCurrentRunningExperiments()
        isConfigFetched = true
        if (isAnimationCompleted)
            redirect()
    }

    private fun getData() {
        newOnboardingViewModel.getPhoneNumberByDeviceId()
        prefsApi.setShowHomeVerticalOverLay(true)
    }

    private fun observeLiveData() {
        newOnboardingViewModel.phoneNumberFlow.asLiveData().observeNetworkResponse(
            viewLifecycleOwner,
            WeakReference(binding.root),
            onSuccess = {
                if (it.phoneNumbers.isNullOrEmpty() && prefsApi.isNewUserCheckEventFired().not()) {
                    analyticsApi.postEvent(EventKey.NEW_USER_CHECK)
                    prefsApi.setIsNewUserCheckEventFired(true)
                }
                onboardingStateMachine.existingPhoneFromDevice = it.phoneNumbers?.firstOrNull()
                if(it.phoneNumbers?.isNotEmpty() == true){
                    onboardingStateMachine.isRegisteredUser = true
                }
            }
        )
    }

    private fun redirect() {
        cancelOnboardingReminderNotification()
        val action = RedirectionNavigationDirections.actionToNewOnBoarding()
        navigateTo(action, false, R.id.redirectionFragment, true)
    }

    private fun startAnimation() {
        binding.lottieView.playAnimation()
        binding.lottieView.addAnimatorUpdateListener(animationListener)
    }

    private fun authenticate() {
        biometricUtil.authenticateInFragment(
            WeakReference(this),
            requireContext().getString(R.string.confirm_your_password),
            this
        )
    }

    private fun shouldAuthenticate() =
        prefsApi.isJarShieldEnabled() && (requireContext().applicationContext as JarApp).isAuthenticationRequestDone.not()

    private fun cancelOnboardingReminderNotification() {
        workManager.cancelAllWorkByTag(ScheduleNotificationWorker.WORK_NAME_ONBOARDING_REMINDER)
        notificationUtil.cancelNotification(NotificationUtil.ONBOARDING_REMINDER_NOTIFICATION_ID)
    }

    private fun sendEventForCurrentRunningExperiments() {
        analyticsApi.postEvent(
            EventKey.Current_Running_Experiments, mapOf(
                EventKey.IS_NEW_BUY_GOLD_FLOW to "yes",
                EventKey.CURRENT_USER_LANGUAGE to prefsApi.getCurrentLanguageCode(),
                RemoteConfigParam.QUICK_ACTION_EXPERIMENT_TYPE.key to remoteConfigApi.getQuickActionExperimentType(),
                RemoteConfigParam.IS_ONBOARDING_SKIP_AGE_AND_GENDER_EXPERIMENT_RUNNING.key to remoteConfigApi.isSkipAgeAndGenderExperimentRunning(),
                RemoteConfigParam.SHOULD_SHOW_DAILY_SAVINGS_V2_FLOW.key to remoteConfigApi.shouldShowDailySavingsV2Flow(),
                RemoteConfigParam.SHOULD_ASK_READ_CONTACT_PERMISSION.key to remoteConfigApi.takeReadContactPermission(),
                RemoteConfigParam.IS_LANGUAGE_EXPERIMENT_RUNNING.key to remoteConfigApi.isLanguageExperimentRunning(),
                RemoteConfigParam.IS_MANDATE_BOTTOM_SHEET_EXPERIMENT_RUNNING.key to remoteConfigApi.isMandateBottomSheetExperimentRunning(),
                RemoteConfigParam.DAILY_SAVINGS_ONBOARDING_EXPERIMENT.key to remoteConfigApi.dailySavingsOnboardingFlowVariant(),
                RemoteConfigParam.SHOULD_SHOW_SKIP_BUTTON_ON_DS_CUSTOM_ONBOARDING_LOTTIE.key to remoteConfigApi.shouldShowSkipButtonOnDSCustomOnboardingLottie(),
                RemoteConfigParam.IS_SMS_PERMISSION_REQUIRED.key to remoteConfigApi.isSmsPermissionRequired(),
                RemoteConfigParam.IS_SHOW_IN_APP_STORY.key to remoteConfigApi.isShowInAppStory(),
                RemoteConfigParam.IN_APP_STORY_MEDIA_URL.key to !remoteConfigApi.getMediaUrlForSoundInAppStory().isNullOrBlank(),
                RemoteConfigParam.IN_APP_STORY_MEDIA_URL.key to !remoteConfigApi.getMediaUrlForSoundInAppStory().isNullOrBlank(),
                RemoteConfigParam.GOAL_SELECTION_FRAGMENT_VARIANT.key to remoteConfigApi.getGoalSelectionFragmentVariant().toString(),
                RemoteConfigParam.SHOULD_USE_OTL.key to remoteConfigApi.shouldUseOTL(),
            )
        )
    }

    override fun onAuthSuccess() {
        (requireContext().applicationContext as JarApp).isAuthenticationRequestDone = true
        if (isConfigFetched)
            redirect()
    }

    override fun onAuthFailed(reason: CharSequence?) {
        (requireContext().applicationContext as JarApp).isAuthenticationRequestDone = true
        reason?.let {
            requireContext().showToast(it.toString())
        }
        requireActivity().finishAffinity()
    }

    override fun onDetach() {
        super.onDetach()
        val app = (requireContext().applicationContext as JarApp)
        if (!app.hasFiredStartTimeEvent) {
            app.hasFiredStartTimeEvent = true
            analyticsApi.postEvent(
                EventKey.EXIT_SPLASH_SCREEN,
                mapOf(
                    EventKey.TIME_SPENT to System.currentTimeMillis() - app.appStartTime,
                )
            )
        }
    }

    override fun onDestroyView() {
        binding.lottieView.removeUpdateListener(animationListener)
        super.onDestroyView()
    }

}