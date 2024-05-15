package com.jar.app.feature.onboarding.ui.onboarding_story

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.jar.app.R
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.currentInternetSpeed
import com.jar.app.core_analytics.EventKey
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.BaseConstants.OnboardingVariants.ONBOARDING_STORY_1
import com.jar.app.core_base.util.BaseConstants.OnboardingVariants.ONBOARDING_STORY_2
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.core_utils.data.NetworkFlow
import com.jar.app.databinding.FragmentOnboardingStoryBinding
import com.jar.app.feature.onboarding.NewOnboardingViewModelAndroid
import com.jar.app.feature_onboarding.shared.data.state_machine.OnboardingStateMachine
import com.jar.app.feature_onboarding.shared.domain.model.OnboardingStoryData
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jar_core_network.api.util.collectUnwrapped
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@AndroidEntryPoint
internal class OnBoardingStoryFragment : BaseFragment<FragmentOnboardingStoryBinding>() {

    @Inject
    lateinit var onboardingStateMachine: OnboardingStateMachine

    @Inject
    lateinit var prefs: PrefsApi

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    @Inject
    lateinit var networkFlow: NetworkFlow

    private val viewModelProviderNewOnboarding by activityViewModels<NewOnboardingViewModelAndroid>()

    private val newOnboardingViewModel by lazy {
        viewModelProviderNewOnboarding.getInstance()
    }

    private val viewModelProvider by viewModels<OnboardingStoryFragmentViewModelAndroid>()

    private val onboardingStoryViewModel by lazy {
        viewModelProvider.getInstance()
    }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentOnboardingStoryBinding
        get() = FragmentOnboardingStoryBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun onResume() {
        activity?.window?.navigationBarColor =
            ContextCompat.getColor(requireContext(), com.jar.app.core_ui.R.color.color_141021)
        super.onResume()
    }

    override fun setup(savedInstanceState: Bundle?) {
        observeLiveData()
        analyticsHandler.postEvent(
            EventKey.Shown_StartNowScreen_Onboarding,
            shouldPushOncePerSession = true
        )
        newOnboardingViewModel.getPhoneNumberByDeviceId()
        onboardingStoryViewModel.fetchOnboardingStoryData()
    }

    private fun observeLiveData() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                networkFlow.networkStatus.collectLatest {
                    if (it) {
                        if(onboardingStoryViewModel.onboardingStoryFlow.value.status == RestClientResult.Status.ERROR){
                            onboardingStoryViewModel.fetchOnboardingStoryData()
                        }
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                onboardingStoryViewModel.onboardingStoryFlow.collect(
                    onSuccess = {
                        binding.shimmerPlaceholder.isVisible = false
                        analyticsHandler.postEvent(
                            EventKey.ONBOARDING_API_RESPONSE_RECEIVED,
                            mapOf(
                                EventKey.value to BaseConstants.VALUE_TRUE,
                                BaseConstants.storyType to it.storyType.orEmpty(),
                                BaseConstants.CurrentInternetSpeed to getInternetSpeed(),
                                ),
                            shouldPushOncePerSession = true
                        )
                        val fragment = getFragmentForVariant(it)
                        val fragmentManager = childFragmentManager
                        if (!isStateSaved) {
                            fragmentManager.beginTransaction()
                                .replace(R.id.onboardingStoryFragmentContainer, fragment)
                                .commit()
                        }
                    },
                    onError = { errorMessage, _ ->
                        analyticsHandler.postEvent(
                            EventKey.ONBOARDING_API_RESPONSE_ERROR,
                            mapOf(
                                EventKey.ERROR_MESSAGE to errorMessage,
                                BaseConstants.CurrentInternetSpeed to getInternetSpeed(),
                            ),
                            shouldPushOncePerSession = true
                        )
                        errorMessage.snackBar(binding.root)
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                newOnboardingViewModel.phoneNumberFlow.collect(
                    onSuccess = {
                        if (it.phoneNumbers.isNullOrEmpty() && prefs.isNewUserCheckEventFired()
                                .not()
                        ) {
                            analyticsHandler.postEvent(EventKey.NEW_USER_CHECK)
                            prefs.setIsNewUserCheckEventFired(true)
                        }
                        onboardingStateMachine.existingPhoneFromDevice =
                            it.phoneNumbers?.firstOrNull()
                        onboardingStateMachine.shouldAskForConsent =
                            it.experianConsent.isNullOrEmpty()

                        if (it.phoneNumbers?.isNotEmpty() == true) {
                            onboardingStateMachine.isRegisteredUser = true
                        }
                    },
                    onError = { errorMessage, _ ->
                        errorMessage.snackBar(binding.root)
                    }
                )
            }
        }
    }


    private fun getFragmentForVariant(onboardingStoryData: OnboardingStoryData) = when (onboardingStoryData.variant) {
            ONBOARDING_STORY_1 -> OnBoardingStoryFragmentVariant1.getInstance(onboardingStoryData)
            ONBOARDING_STORY_2 -> OnBoardingStoryFragmentVariant2.getInstance(onboardingStoryData)
            else -> OnBoardingStoryFragmentVariant0.getInstance(onboardingStoryData)
    }

    private fun getInternetSpeed(): String {
        val connectivityManager =
            requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return connectivityManager.currentInternetSpeed()
    }

    override fun onDestroy() {
        activity?.window?.navigationBarColor =
            ContextCompat.getColor(requireContext(), com.jar.app.core_ui.R.color.bgColor)
        super.onDestroy()
    }
}