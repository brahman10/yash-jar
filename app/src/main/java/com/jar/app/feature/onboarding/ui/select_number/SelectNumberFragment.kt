package com.jar.app.feature.onboarding.ui.select_number

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bureau.base.Environment
import com.bureau.onetaplogin.BureauAuth
import com.bureau.onetaplogin.models.AuthCallback
import com.bureau.onetaplogin.models.AuthenticationStatus
import com.bumptech.glide.Glide
import com.jar.app.BuildConfig
import com.jar.app.R
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.core_analytics.EventKey
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.orZero
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.core_utils.data.NetworkFlow
import com.jar.app.databinding.FragmentSelectNumberBinding
import com.jar.app.feature.onboarding.NewOnboardingViewModelAndroid
import com.jar.app.feature_onboarding.shared.util.OnboardingConstants
import com.jar.app.feature_onboarding.shared.util.RandomUUIDGenerator
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
internal class SelectNumberFragment : BaseFragment<FragmentSelectNumberBinding>() {

    @Inject
    lateinit var remoteConfigApi: RemoteConfigApi

    @Inject
    lateinit var onboardingStateMachine: com.jar.app.feature_onboarding.shared.data.state_machine.OnboardingStateMachine

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    @Inject
    lateinit var prefsApi: PrefsApi

    @Inject
    lateinit var networkFlow: NetworkFlow

    private val timeInit = System.currentTimeMillis()
    private var isNetworkAvailable = true

    private val args by navArgs<SelectNumberFragmentArgs>()

    private val newOnboardingViewModelProvider by activityViewModels<NewOnboardingViewModelAndroid>()

    private val newOnboardingViewModel by lazy {
        newOnboardingViewModelProvider.getInstance()
    }

    private val bureauAuth by lazy {
        BureauAuth.Builder()
            .environment(if (BuildConfig.FLAVOR.contains("staging")) Environment.ENV_SANDBOX else Environment.ENV_PRODUCTION)
            .clientId(BuildConfig.BUREAU_AUTH_CLIENT_ID)
            .timeOutInMs(5000)
            .build()
    }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentSelectNumberBinding
        get() = FragmentSelectNumberBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
        setupListeners()
        observeLiveData()
        getData()
        analyticsHandler.postEvent(EventKey.Shown_ContinueScreen_Onboarding)
    }

    private fun setupUI() {
        val number = args.number.take(3) + "- " + args.number.drop(3)
        binding.btnContinue.setText(getString(R.string.continue_with_x_number, number))
    }

    private fun setupListeners() {
        binding.tvContinueWithDifferentNumber.setOnClickListener {
            //User want to skip the existing number, so reset value
            onboardingStateMachine.existingPhoneFromDevice = null
            onboardingStateMachine.usingNewNumber = true
            onboardingStateMachine.navigateAhead()
            analyticsHandler.postEvent(EventKey.ClickedAnotherNumber_ContinueScreen_Onboarding)
        }

        binding.btnContinue.setOnClickListener {
            analyticsHandler.postEvent(
                EventKey.ClickedContinue_ContinueScreen_Onboarding,
                mapOf(EventKey.NUMBER to args.number)
            )
            onboardingStateMachine.usingNewNumber = false
            if (onboardingStateMachine.shouldAskForConsent == true) {
                val direction =
                    SelectNumberFragmentDirections.actionSelectNumberFragmentToExperianConsentBottomSheet(
                        R.id.selectNumberFragment
                    )
                navigateTo(direction)
            } else {
                initateLogin(formatNumber(args.number), hasExperianConsent = true)
            }
        }
    }

    @SuppressLint("StringFormatInvalid")
    private fun observeLiveData() {

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                networkFlow.networkStatus.collectLatest {
                    isNetworkAvailable = it
                    toggleMainButton(it)
                }
            }
        }

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Boolean>(
            BaseConstants.EXPERIAN_CONSENT
        )?.observe(viewLifecycleOwner) {
            initateLogin(formatNumber(args.number), hasExperianConsent = it)
        }

        newOnboardingViewModel.phoneNumberFlow.asLiveData().observeNetworkResponse(
            viewLifecycleOwner,
            WeakReference(binding.root),
            onSuccess = {
                Glide.with(binding.root.context)
                    .load(it.profilePhoto)
                    .placeholder(com.jar.app.core_ui.R.drawable.core_ui_common_avatar)
                    .into(binding.ivProfilePicture)
                binding.tvLogin.text =
                    getString(R.string.feature_onboarding_select_number_welcome_back,it.userName.orEmpty())
                if (it.phoneNumbers.isNullOrEmpty() && prefsApi.isNewUserCheckEventFired().not()) {
                    analyticsHandler.postEvent(EventKey.NEW_USER_CHECK)
                    prefsApi.setIsNewUserCheckEventFired(true)
                }
                onboardingStateMachine.existingPhoneFromDevice = it.phoneNumbers?.firstOrNull()
                onboardingStateMachine.shouldAskForConsent =
                    it.experianConsent.isNullOrEmpty()
                if (it.phoneNumbers?.isNotEmpty() == true) {
                    onboardingStateMachine.isRegisteredUser = true
                }
            }
        )
    }

    private fun getData() {
        newOnboardingViewModel.getPhoneNumberByDeviceId()
    }

    private fun toggleMainButton(shouldEnable: Boolean) {
        binding.btnContinue.setDisabled(shouldEnable.not())
        binding.tvContinueWithDifferentNumber.isEnabled = shouldEnable
    }

    override fun onDestroyView() {
        val endTime = System.currentTimeMillis()
        newOnboardingViewModel.updateScreenTime(
            screenName = com.jar.app.feature_onboarding.shared.data.state_machine.OnboardingStateMachine.State.EnterName,
            timeSpentOnScreen = timeInit.orZero().minus(endTime)
        )
        analyticsHandler.postEvent(
            EventKey.Exit_ContinueScreen_Onboarding,
            mapOf(EventKey.TIME_SPENT to System.currentTimeMillis() - timeInit)
        )
        super.onDestroyView()
    }

    private fun formatNumber(phoneNumber: String): String {
        return if (phoneNumber.contains("+91"))
            phoneNumber.removePrefix("+91")
        else
            phoneNumber.removePrefix("91")
    }

    private fun initateLogin(number: String, hasExperianConsent: Boolean) {
        viewLifecycleOwner.lifecycleScope.launch {
            showProgressBar()
            val otlLoginNumber = number.prependIndent(BaseConstants.DEFAULT_COUNTRY_CODE).toLong()
            withContext(Dispatchers.IO) {
                if (remoteConfigApi.shouldUseOTL()) {
                    val correlationId = RandomUUIDGenerator.randomUUID()
                    val authCallback = object : AuthCallback {
                        override fun onResult(authenticationStatus: AuthenticationStatus) {
                            if (authenticationStatus.name == AuthenticationStatus.Completed.name) {
                                onboardingStateMachine.correlationId = correlationId
                                onboardingStateMachine.isOtlFlow = true
                            }
                            checkNetworkAndNavigate(number, hasExperianConsent)
                        }
                    }
                    bureauAuth.authenticate(
                        requireContext(),
                        correlationId,
                        otlLoginNumber,
                        authCallback
                    )
                }
            }
        }
    }

    private fun checkNetworkAndNavigate(number: String, hasExperianConsent: Boolean) {
        dismissProgressBar()
        if (isNetworkAvailable) {
            onboardingStateMachine.hasExperianConsent = hasExperianConsent
            onboardingStateMachine.phoneNumber = number
            onboardingStateMachine.countryCode = BaseConstants.DEFAULT_COUNTRY_CODE
            onboardingStateMachine.navigateAhead()
        }
    }

}