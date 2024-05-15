package com.jar.app.feature.onboarding.ui.sms

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import com.jar.app.R
import com.jar.app.base.data.event.SmsPermissionGivenEvent
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.openAppInfo
import com.jar.app.core_analytics.EventKey
import com.jar.app.core_base.util.BaseConstants.StaticContentType
import com.jar.app.core_base.util.orFalse
import com.jar.app.core_base.util.orZero
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.databinding.FargmentNewSmsPermissionBinding
import com.jar.app.feature.home.ui.activity.HomeActivityViewModel
import com.jar.app.feature.onboarding.NewOnboardingViewModelAndroid
import com.jar.app.feature_onboarding.shared.domain.model.CustomOnboardingData
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.OnNeverAskAgain
import permissions.dispatcher.OnPermissionDenied
import permissions.dispatcher.RuntimePermissions
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
@RuntimePermissions
class SmsPermissionFragment : BaseFragment<FargmentNewSmsPermissionBinding>() {

    @Inject
    lateinit var onboardingStateMachine: com.jar.app.feature_onboarding.shared.data.state_machine.OnboardingStateMachine

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    @Inject
    lateinit var prefs: PrefsApi

    private val timeInit = System.currentTimeMillis()

    private var smsPermissionPopupDismissalCount = 0

    private val newOnboardingViewModelProvider by activityViewModels<NewOnboardingViewModelAndroid>()

    private val newOnboardingViewModel by lazy {
        newOnboardingViewModelProvider.getInstance()
    }

    private val homeViewModel by activityViewModels<HomeActivityViewModel> { defaultViewModelProviderFactory }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FargmentNewSmsPermissionBinding
        get() = FargmentNewSmsPermissionBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun setup(savedInstanceState: Bundle?) {
        // Need to access newOnboardingViewModel here first..
        // If we directly access in onDestroy() then we are getting below error"
        // Error -> {SavedStateProvider with the given key is already registered}
        // https://console.firebase.google.com/u/1/project/changejarprod/crashlytics/app/android:com.jar.app/issues/0cf3e608eb098b37ca379b234cc2966b?time=last-seven-days&versions=5.5.0%20(340)&sessionEventKey=640A8D4B004F000141B28A77C81C0756_1787175236159059635
        setupUI()
        setupListeners()
        observeLiveData()
    }

    private fun setupUI() {
        newOnboardingViewModel.timeSpentMap
        navigateToHomeWithPermissionCheck()
        analyticsHandler.postEvent(EventKey.Shown_PermissionScreen_Onboarding)
        prefs.getUserPhoneNumber()?.let { homeViewModel.fetchCustomisedOnboardingFlow(it) }
    }

    private fun observeLiveData() {
        homeViewModel.customisedOnboardingLiveData.observeNetworkResponse(viewLifecycleOwner,
            WeakReference(binding.root),
            onSuccess = {
                onboardingStateMachine.customOnboardingData = CustomOnboardingData(
                    customOnboardingLink = it?.deepLink,
                    version = it?.version,
                    infographicType = it?.infographic?.type,
                    infographicLink = it?.infographic?.url
                )
            },
            onSuccessWithNullData = {
                onboardingStateMachine.customOnboardingData = null
            },
            onError = {
                onboardingStateMachine.customOnboardingData = null
            })
    }

    private fun setupListeners() {
        binding.btnAllow.setDebounceClickListener {
            navigateToHomeWithPermissionCheck()
            analyticsHandler.postEvent(EventKey.ClickedGrantPermission_PermissionScreen_Onboarding)
        }
    }

    @NeedsPermission(Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS)
    fun navigateToHome() {
        EventBus.getDefault().post(SmsPermissionGivenEvent())
        onboardingStateMachine.navigateAhead()
        analyticsHandler.postEvent(
            EventKey.ClickedPermission_Onboarding, mapOf(EventKey.PROP_STATUS to EventKey.ALLOWED)
        )
    }

    @OnPermissionDenied(Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS)
    fun onPermissionDenied() {
        binding.tvError.isVisible = true
        analyticsHandler.postEvent(
            EventKey.ClickedPermission_Onboarding, mapOf(EventKey.PROP_STATUS to EventKey.DENIED)
        )
        analyticsHandler.postEvent(EventKey.ShownError_PermissionScreen_Onboarding)
    }

    @OnNeverAskAgain(Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS)
    fun onPermissionNeverAskAgain() {
        getString(R.string.permission_needed_desc).snackBar(binding.root)
        smsPermissionPopupDismissalCount++
        if (smsPermissionPopupDismissalCount >= 3) {
            requireContext().openAppInfo()
            analyticsHandler.postEvent(
                EventKey.ClickedPermission_Onboarding,
                mapOf(EventKey.PROP_STATUS to EventKey.NEVER_ASK_AGAIN)
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }

    override fun onDestroyView() {
        val endTime = System.currentTimeMillis()
        newOnboardingViewModel.updateScreenTime(
            screenName = com.jar.app.feature_onboarding.shared.data.state_machine.OnboardingStateMachine.State.EnterName,
            timeSpentOnScreen = timeInit.orZero().minus(endTime)
        )
        analyticsHandler.postEvent(
            EventKey.Exit_PermissionScreen_Onboarding,
            mapOf(EventKey.TIME_SPENT to System.currentTimeMillis() - timeInit)
        )
        super.onDestroyView()
    }
}