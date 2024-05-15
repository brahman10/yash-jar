package com.jar.app.feature.onboarding.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.jar.app.JarApp
import com.jar.app.R
import com.jar.app.base.data.livedata.SharedPreferencesUserLiveData
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.core_analytics.EventKey
import com.jar.app.core_base.util.orFalse
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_utils.data.NetworkFlow
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.databinding.FragmentNewOnboardingRedirectionBinding
import com.jar.app.base.data.event.OnboardingCompletedEvent
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.feature.home.ui.activity.HomeActivityViewModel
import com.jar.app.feature_onboarding.shared.data.state_machine.OnboardingStateMachine
import com.jar.app.feature_onboarding.shared.domain.model.CustomOnboardingData
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
internal class OnBoardingRedirectionFragment : BaseFragment<FragmentNewOnboardingRedirectionBinding>() {

    @Inject
    lateinit var onboardingStateMachine: OnboardingStateMachine

    @Inject
    lateinit var prefs: PrefsApi

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    @Inject
    lateinit var userLiveData: SharedPreferencesUserLiveData

    @Inject
    lateinit var networkFlow: NetworkFlow

    private val homeViewModel:HomeActivityViewModel by activityViewModels()
    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentNewOnboardingRedirectionBinding
        get() = FragmentNewOnboardingRedirectionBinding::inflate

    override fun setupAppBar() {}

    override fun setup(savedInstanceState: Bundle?) {
        observeLiveData()
        setupListeners()
        navigate()
    }

    private fun setupListeners() {
        binding.btnContinue.setDebounceClickListener {
            redirect()
            analyticsHandler.postEvent(EventKey.ClickedContinue_LoginScreen_Onboarding)
        }
    }

    private fun observeLiveData() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                networkFlow.networkStatus.collectLatest {
                    toggleMainButton(it)
                }
            }
        }

        userLiveData.observe(viewLifecycleOwner) {
            userLiveData.value?.phoneNumber?.let { it1 ->
                homeViewModel.fetchCustomisedOnboardingFlow(
                    it1
                )
            }
            val name = it?.getFullName()
            val message = if (!name.isNullOrBlank()) {
                getString(R.string.we_missed_you).plus(", ").plus(name).plus("!")
            } else {
                getString(R.string.we_missed_you).plus("!")
            }
            binding.tvWelcomeMessage.text = message
        }

        homeViewModel.customisedOnboardingLiveData.observeNetworkResponse(
            this,
            WeakReference(binding.root),
            onSuccess = {
                onboardingStateMachine.customOnboardingData = CustomOnboardingData(
                    customOnboardingLink = it?.deepLink,
                    version = it?.version,
                    infographicType = it?.infographic?.type,
                    infographicLink = it?.infographic?.url
                )
            },
            onError = {
                onboardingStateMachine.customOnboardingData = null
            })
    }

    private fun navigate() {
        when {
            prefs.isOnboardingComplete() -> {
                EventBus.getDefault().postSticky(OnboardingCompletedEvent())
                navigateTo(BaseConstants.InternalDeepLinks.HOME)
            }
            prefs.isOnBoardingStoryShown().not() || (requireContext().applicationContext as? JarApp)?.skipMissedYouScreen.orFalse() -> redirect()
        }
    }

    private fun redirect() {
        onboardingStateMachine.navigateToPending()
    }

    private fun toggleMainButton(shouldEnable: Boolean) {
        binding.btnContinue.setDisabled(!shouldEnable)
    }
}