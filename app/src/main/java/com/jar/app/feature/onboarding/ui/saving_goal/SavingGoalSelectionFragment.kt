package com.jar.app.feature.onboarding.ui.saving_goal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.jar.app.R
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.core_base.util.orZero
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.databinding.FragmentSavingGoalSelectionBinding
import com.jar.app.feature.home.ui.activity.HomeActivityViewModel
import com.jar.app.feature.onboarding.NewOnboardingViewModelAndroid
import com.jar.app.feature_mandate_payment_common.impl.util.PackageManagerUtil
import com.jar.app.feature_onboarding.shared.data.state_machine.OnboardingStateMachine
import com.jar.app.feature_onboarding.shared.domain.model.CustomOnboardingData
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
internal class SavingGoalSelectionFragment : BaseFragment<FragmentSavingGoalSelectionBinding>() {

    @Inject
    lateinit var onboardingStateMachine: OnboardingStateMachine

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    @Inject
    lateinit var remoteConfigApi: RemoteConfigApi

    @Inject
    lateinit var prefs: PrefsApi

    @Inject
    lateinit var packageManagerUtil: PackageManagerUtil

    private val timeInit = System.currentTimeMillis()

    private val homeViewModel by activityViewModels<HomeActivityViewModel> { defaultViewModelProviderFactory }

    private val onboardingViewModelProvider by activityViewModels<NewOnboardingViewModelAndroid>()

    private val onBoardingViewModel by lazy {
        onboardingViewModelProvider.getInstance()
    }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentSavingGoalSelectionBinding
        get() = FragmentSavingGoalSelectionBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
        observeLiveData()
    }

    private fun setupUI() {
        val fragment = getFragmentForVariant()
        val fragmentManager = childFragmentManager
        fragmentManager.beginTransaction()
            .replace(R.id.goalSelectionFragmentContainer, fragment)
            .commit()
    }

    private fun observeLiveData() {
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

    override fun onDestroyView() {
        val endTime = System.currentTimeMillis()
        onBoardingViewModel.updateScreenTime(
            screenName = OnboardingStateMachine.State.EnterName,
            timeSpentOnScreen = timeInit.orZero().minus(endTime)
        )
        super.onDestroyView()
    }

    private fun getFragmentForVariant(): Fragment {
        return if(remoteConfigApi.getGoalSelectionFragmentVariant() == 1) SavingsGoalSelectionV1Fragment() else SavingsGoalSelectionV2Fragment()
    }

}