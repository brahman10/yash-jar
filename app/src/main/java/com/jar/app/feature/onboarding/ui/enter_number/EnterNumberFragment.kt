package com.jar.app.feature.onboarding.ui.enter_number

import android.annotation.SuppressLint
import android.app.Activity
import android.content.IntentSender
import android.os.Bundle
import android.view.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
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
import com.google.android.gms.auth.api.credentials.Credential
import com.google.android.gms.auth.api.credentials.Credentials
import com.google.android.gms.auth.api.credentials.CredentialsOptions
import com.google.android.gms.auth.api.credentials.HintRequest
import com.google.android.gms.auth.api.identity.GetPhoneNumberHintIntentRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.common.api.ApiException
import com.jar.app.BuildConfig
import com.jar.app.HomeNavigationDirections
import com.jar.app.R
import com.jar.app.base.data.event.SyncDeviceDetailsEvent
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.*
import com.jar.app.core_analytics.EventKey
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.StringUtils
import com.jar.app.core_base.util.orZero
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.core_ui.extension.*
import com.jar.app.core_ui.extension.setOnImeActionDoneListener
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.core_utils.data.NetworkFlow
import com.jar.app.databinding.FragmentNewEnterNumberBinding
import com.jar.app.feature.home.ui.activity.HomeActivityViewModel
import com.jar.app.feature.onboarding.NewOnboardingViewModelAndroid
import com.jar.app.feature_mandate_payment_common.impl.util.UpiAppsUtil
import com.jar.app.feature_onboarding.shared.data.state_machine.OnboardingStateMachine
import com.jar.app.feature_onboarding.shared.util.OnboardingConstants
import com.jar.app.feature_onboarding.shared.util.RandomUUIDGenerator
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import io.michaelrocks.libphonenumber.android.NumberParseException
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber
import java.lang.ref.WeakReference
import javax.inject.Inject


@AndroidEntryPoint
internal class EnterNumberFragment : BaseFragment<FragmentNewEnterNumberBinding>() {

    @Inject
    lateinit var onboardingStateMachine: OnboardingStateMachine

    @Inject
    lateinit var phoneNumberUtil: PhoneNumberUtil

    @Inject
    lateinit var remoteConfigApi: RemoteConfigApi

    @Inject
    lateinit var prefsApi: PrefsApi

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    @Inject
    lateinit var networkFlow: NetworkFlow

    @Inject
    lateinit var upiAppsUtil: UpiAppsUtil

    private var isNetworkAvailable = true

    private var iButtonEnabled = false

    private val timeInit = System.currentTimeMillis()

    private val args by navArgs<EnterNumberFragmentArgs>()

    private val enterNumberViewModelProvider by viewModels<EnterNumberFragmentViewModelAndroid> { defaultViewModelProviderFactory }

    private val viewModel by lazy { enterNumberViewModelProvider.getInstance() }

    private val homeViewModel by activityViewModels<HomeActivityViewModel> { defaultViewModelProviderFactory }

    private val newOnboardingViewModelProvider by activityViewModels<NewOnboardingViewModelAndroid>()

    private val newOnboardingViewModel by lazy {
        newOnboardingViewModelProvider.getInstance()
    }

    private var phonePickIntentResultLauncher: ActivityResultLauncher<IntentSenderRequest>? = null
    private var phonePickIntentResultLauncherOldApi: ActivityResultLauncher<IntentSenderRequest>? =
        null

    private var experianConsent: Boolean = false

    private var isExperianCtaEnabled = false
    private var isExperianCheckboxChecked = false

    private val bureauAuth by lazy {
        BureauAuth.Builder()
            .environment(if (BuildConfig.FLAVOR.contains("staging")) Environment.ENV_SANDBOX else Environment.ENV_PRODUCTION)
            .clientId(BuildConfig.BUREAU_AUTH_CLIENT_ID)
            .timeOutInMs(5000)
            .build()
    }

    companion object {
        const val SUCCESS = "Success"
        const val FAILURE = "Failure"
        private const val NO_NUMBER_DETECTED_RESULT_CODE = 1002
    }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentNewEnterNumberBinding
        get() = FragmentNewEnterNumberBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        phonePickIntentResultLauncherOldApi =
            registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
                if (result != null) {
                    val intent = result.data
                    val credential = intent?.getParcelableExtra<Credential>(Credential.EXTRA_KEY)
                    val number = credential?.id
                    if (!number.isNullOrBlank()) {
                        try {
                            val data = phoneNumberUtil.parse(number, BaseConstants.REGION_CODE)
                            binding.etNumber.setText(data.nationalNumber.toString())
                        } catch (e: NumberParseException) {
                            binding.etNumber.showKeyboard()
                        }
                    } else {
                        binding.etNumber.showKeyboard()
                    }
                }
                if (result.resultCode == Activity.RESULT_OK)
                    analyticsHandler.postEvent(
                        OnboardingConstants.AnalyticsKeys.ClickedNumber_PhoneNumberSuggestion_Onboarding,
                        OnboardingConstants.AnalyticsKeys.Is_New_Flow,
                        remoteConfigApi.isDetectPhoneNumberNewApi().toString()
                    )
                else if (result.resultCode == Activity.RESULT_CANCELED || result.resultCode == BaseConstants.RESULT_NONE)
                    analyticsHandler.postEvent(OnboardingConstants.AnalyticsKeys.Dismissed_PhoneNumberSuggestion_Onboarding)
                else if (result.resultCode == NO_NUMBER_DETECTED_RESULT_CODE) {
                    analyticsHandler.postEvent(
                        OnboardingConstants.AnalyticsKeys.Dismissed_PhoneNumberSuggestion_Onboarding,
                        mapOf(OnboardingConstants.AnalyticsKeys.NUMBER_DETECTED to false)
                    )
                    binding.etNumber.showKeyboard()
                }
            }
        phonePickIntentResultLauncher =
            registerForActivityResult<IntentSenderRequest, ActivityResult>(
                ActivityResultContracts.StartIntentSenderForResult()
            ) { result: ActivityResult ->
                try {
                    val phoneNumber = Identity.getSignInClient(requireActivity())
                        .getPhoneNumberFromIntent(result.data)
                    if (!phoneNumber.isNullOrBlank()) {
                        try {
                            // If condition has been added to ensure for the numbers which doesn't start with India extension (+91)
                            // For example +4491983425xxxx
                            // That'll be parsed using all digits and take last 10 int
                            val number =
                                if (phoneNumber.startsWith("91") || phoneNumber.startsWith("+91") || phoneNumber.startsWith(
                                        "0"
                                    ) || phoneNumber.length == 10
                                ) {
                                    val data = phoneNumberUtil.parse(
                                        phoneNumber,
                                        BaseConstants.REGION_CODE
                                    )
                                    data.nationalNumber.toString()
                                } else {
                                    phoneNumber.filter { it.isDigit() || it != ' ' }.takeLast(10)
                                }
                            binding.etNumber.setText(number)
                        } catch (e: NumberParseException) {
                            binding.etNumber.showKeyboard()
                        }
                    } else {
                        binding.etNumber.showKeyboard()
                    }
                } catch (e: ApiException) {
                    Timber.e(e)
                }
                if (result.resultCode == Activity.RESULT_OK)
                    analyticsHandler.postEvent(
                        OnboardingConstants.AnalyticsKeys.ClickedNumber_PhoneNumberSuggestion_Onboarding,
                        OnboardingConstants.AnalyticsKeys.Is_New_Flow,
                        remoteConfigApi.isDetectPhoneNumberNewApi().toString()
                    )
                else if (result.resultCode == Activity.RESULT_CANCELED || result.resultCode == BaseConstants.RESULT_NONE)
                    analyticsHandler.postEvent(OnboardingConstants.AnalyticsKeys.Dismissed_PhoneNumberSuggestion_Onboarding)
                else if (result.resultCode == NO_NUMBER_DETECTED_RESULT_CODE)
                    analyticsHandler.postEvent(
                        OnboardingConstants.AnalyticsKeys.Dismissed_PhoneNumberSuggestion_Onboarding,
                        mapOf(OnboardingConstants.AnalyticsKeys.NUMBER_DETECTED to false)
                    )
            }
        EventBus.getDefault().register(this)
    }

    override fun setup(savedInstanceState: Bundle?) {
        // Need to access newOnboardingViewModel here first..
        // If we directly access in onDestroy() then we are getting below error"
        // Error -> {SavedStateProvider with the given key is already registered}
        // https://console.firebase.google.com/u/1/project/changejarprod/crashlytics/app/android:com.jar.app/issues/0cf3e608eb098b37ca379b234cc2966b?time=last-seven-days&versions=5.5.0%20(340)&sessionEventKey=640A8D4B004F000141B28A77C81C0756_1787175236159059635
        newOnboardingViewModel.timeSpentMap
        setupUI()
        setupListeners()
        observeLiveData()
        getData()
        analyticsHandler.postEvent(
            OnboardingConstants.AnalyticsKeys.Shown_LoginScreen_Onboarding
        )

    }

    private fun setupUI() {
        binding.etNumber.text?.length?.let { binding.etNumber.setSelection(it) }
        toggleExperianVerifyButton()
        if (!args.number.isNullOrBlank()) {
            try {
                val result = phoneNumberUtil.parse(args.number, BaseConstants.REGION_CODE)
                binding.etNumber.setText(result.nationalNumber.toString())
            } catch (e: NumberParseException) {
                //Do nothing
            }
        }

        if(onboardingStateMachine.shouldAskForConsent == true){
            val checkBoxText = getString(R.string.feature_onboarding_enter_number_checkbox_text)
            val checkBoxColor =
                ContextCompat.getColor(requireContext(), com.jar.app.core_ui.R.color.color_ACA1D3)

            binding.experianCheckboxText.makeColoredLink(
                message = checkBoxText,
                words = listOf(getString(R.string.feature_onboarding_TC)),
                color = checkBoxColor,
                shouldUnderlineWords = true,
                {
                    navigateTo(
                        EnterNumberFragmentDirections.actionNewEnterNumberFragmentToExperianTCBottomSheet(),
                        true
                    )
                }
            )
        }else
            binding.clExperianCheck.isVisible = false

        val message = getString(R.string.feature_onboarding_flow_tc_text)
        val color =
            ContextCompat.getColor(requireContext(), com.jar.app.core_ui.R.color.color_ACA1D3)

        binding.tvTermsConditions.makeColoredLink(
            message = message,
            words = listOf(getString(R.string.feature_onboarding_TC), getString(R.string.privacy_policy)),
            color = color,
            shouldUnderlineWords = true,
            {
                openUrlInChromeTab(
                    remoteConfigApi.getTermsAndConditionsUrl(),
                    "Jar’s T&C",
                    true
                )
                analyticsHandler.postEvent(OnboardingConstants.AnalyticsKeys.ClickedTermsAndConditions_LoginScreen_Onboarding)
            },
            {
                openUrlInChromeTab(
                    remoteConfigApi.getPrivacyPolicyUrl(),
                    getString(R.string.privacy_policy),
                    true
                )
                analyticsHandler.postEvent(OnboardingConstants.AnalyticsKeys.ClickedPrivacyPolicy_LoginScreen_Onboarding)
            }
        )
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupListeners() {
        binding.btnExperianGetOtp.setDebounceClickListener {
            val number = binding.etNumber.text?.toString()
            if (onboardingStateMachine.isRegisteredUser && !binding.checkbox.isChecked) {
                initateLogin(number!!)
            } else if (StringUtils.isValidPhoneNumber(number) && onboardingStateMachine.isRegisteredUser.not()) {
                binding.tvExperianWarning.isVisible = binding.checkbox.isChecked.not()
                if (binding.checkbox.isChecked) {
                    initateLogin(number.toString())
                }
            } else if (StringUtils.isValidPhoneNumber(number) && onboardingStateMachine.isRegisteredUser) {
                initateLogin(number.toString())
            } else {
                binding.tvErrorMessage.text = getString(R.string.mobile_number_is_not_valid)
                binding.tvErrorMessage.isVisible = true
                binding.etNumber.hideKeyboard()
                updateEventParamsValue()
                sendProceedClickedEvent(number = number.toString())
            }
        }

        binding.clExperianBtn.setDebounceClickListener {
            if(binding.btnExperianGetOtp.isEnabled.not() && binding.checkbox.isChecked.not() && StringUtils.isValidPhoneNumber(binding.etNumber.text?.toString())){
                binding.clExperianCheck.shakeAnimation()
                binding.experianCheckboxText.setTextColor(ContextCompat.getColor(requireContext(), com.jar.app.core_ui.R.color.white))
                val checkBoxText = getString(R.string.feature_onboarding_enter_number_checkbox_text)
                val checkBoxColor =
                    ContextCompat.getColor(requireContext(), com.jar.app.core_ui.R.color.white)

                binding.experianCheckboxText.makeColoredLink(
                    message = checkBoxText,
                    words = listOf("T&C"),
                    color = checkBoxColor,
                    shouldUnderlineWords = true,
                    {
                        navigateTo(
                            EnterNumberFragmentDirections.actionNewEnterNumberFragmentToExperianTCBottomSheet(),
                            true
                        )
                    }
                )
                binding.tvExperianWarning.isVisible = true
            }
        }

        binding.etNumber.textChanges()
            .debounce(250)
            .onEach {
                checkNumberAndUpdateUI(it.toString())
            }
            .launchIn(uiScope)

        binding.etNumber.setOnImeActionDoneListener {
            if (binding.etNumber.text?.length.orZero() < 10) {
                return@setOnImeActionDoneListener
            } else {
                binding.btnExperianGetOtp.performClick()
            }
        }

        binding.experianCheckboxText.setDebounceClickListener {
            val newState = !binding.checkbox.isChecked
            binding.checkbox.isChecked = newState
            check(newState)
        }

        binding.etNumber.setDebounceClickListener {
            analyticsHandler.postEvent(EventKey.ClickedEnterPhoneNumber_LoginScreen_Onboarding)
        }

        binding.checkbox.setOnCheckedChangeListener { _, isChecked ->
            experianCheckState(isChecked)
        }
    }

    private fun experianCheckState(isChecked: Boolean) {
        toggleExperianVerifyButton()
        analyticsHandler.postEvent(
            OnboardingConstants.AnalyticsKeys.Clicked_experian_checkbox,
            mapOf(OnboardingConstants.AnalyticsKeys.Is_checked to isChecked)
        )
        if (!onboardingStateMachine.isRegisteredUser) {
            binding.tvExperianWarning.isVisible = !isChecked
        }
    }

    private fun checkNumberAndUpdateUI(value: String) {
        binding.ivTick.isVisible = StringUtils.isValidPhoneNumber(value)
        binding.tvErrorMessage.isVisible = false
        if (value.length.orZero() >= 10)
            setEditTextBackground(value)
        else
            binding.clNumber.background = ContextCompat.getDrawable(
                requireContext(),
                R.drawable.bg_rounded_272239_20dp
            )
        toggleExperianVerifyButton()
    }

    private fun observeLiveData() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                networkFlow.networkStatus.collectLatest {
                    isNetworkAvailable = it
                    toggleExperianVerifyButton()
                }
            }
        }

        viewModel.truecallerLoginFlow.asLiveData().observeNetworkResponse(
            viewLifecycleOwner,
            WeakReference(binding.root),
            onLoading = {
                showProgressBar()
            },
            onSuccess = {
                dismissProgressBar()
                if (it.hasOtherActiveSessions.orFalse()) {
                    navigateTo(
                        HomeNavigationDirections.actionToActiveSessionDetectedFragment(null), false
                    )
                } else {
                    homeViewModel.numberOfDaysOfSms =
                        it.numberOfDaysOfSms ?: BaseConstants.DEFAULT_VALUES_FOR_NO_OF_SMS_SYNC
                    newOnboardingViewModel.saveUserData(it)
                    onboardingStateMachine.mUser = it.user

                    //Possible values of authType - SIGNUP/LOGIN  (Used to track re logins)
                    prefsApi.setAuthType(it.authType.orEmpty())
                    if (it.authType.orEmpty() == BaseConstants.LOGIN)
                        analyticsHandler.postEvent(OnboardingConstants.AnalyticsKeys.OLD_USER_LOGIN)
                    else if (it.authType.orEmpty() == BaseConstants.SIGNUP) {
                        analyticsHandler.postEvent(OnboardingConstants.AnalyticsKeys.NEW_USER_SIGNUP)
                        analyticsHandler.postEvent(EventKey.NEW_USER_SIGNUP_MOBILE)
                    }

                    prefsApi.setUserPhoneNumber(it.user.phoneNumber)

                    //Used to track logins
                    analyticsHandler.postEvent(
                        OnboardingConstants.AnalyticsKeys.AUTH_SUCCESSFUL,
                        mapOf(
                            OnboardingConstants.AnalyticsKeys.PROP_SOURCE to OnboardingConstants.AnalyticsKeys.TRUECALLER,
                            OnboardingConstants.AnalyticsKeys.AuthType to it.authType.orEmpty()
                        )
                    )
                    EventBus.getDefault().post(SyncDeviceDetailsEvent())

                    onboardingStateMachine.navigateAfterTruecallerCompletion()
                }
            },
            onError = {
                dismissProgressBar()
            }
        )

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Boolean>(
            BaseConstants.EXPERIAN_CONSENT
        )?.observe(viewLifecycleOwner) {
            val number = binding.etNumber.text?.toString()
            initateLogin(number.toString(), it)
        }

        uiScope.launch {
            findNavController().currentBackStackEntryFlow.collectLatest {
                when (it.destination.id) {
                    R.id.newEnterNumberFragment -> startInactivityTimers()
                }
            }
        }
    }

    private fun startInactivityTimers() {
        if (binding.etNumber.text.isNullOrBlank().not())
            return
        requestHint()
    }

    private fun getData() {
        viewModel.getLanguageForCode(prefsApi.getCurrentLanguageCode())
    }


    private fun requestHint() {
        if (remoteConfigApi.isDetectPhoneNumberNewApi()) {
            // run new phone number detection
            requestHintNewAPI()
        } else {
            // run old phone number detection
            requestHintOldAPI(remoteConfigApi.isDetectPhoneNumberNewApi())
        }
    }

    private fun requestHintOldAPI(isNewFlow: Boolean) {
        val hintRequest = HintRequest.Builder()
            .setPhoneNumberIdentifierSupported(true)
            .build()

        val options = CredentialsOptions.Builder()
            .forceEnableSaveDialog()
            .build()

        val credentialClient = Credentials.getClient(requireContext(), options)
        val intent = credentialClient.getHintPickerIntent(hintRequest)
        try {
            if (lifecycle.currentState >= Lifecycle.State.RESUMED) {
                IntentSenderRequest.Builder(intent.intentSender).build()?.let {
                    phonePickIntentResultLauncherOldApi?.launch(it)
                }
                analyticsHandler.postEvent(
                    OnboardingConstants.AnalyticsKeys.Shown_PhoneNumberSuggestion_Onboarding,
                    OnboardingConstants.AnalyticsKeys.Is_New_Flow,
                    isNewFlow.toString(),
                    false
                )
            }
        } catch (e: IntentSender.SendIntentException) {
            e.printStackTrace()
        }
    }

    private fun requestHintNewAPI() {
        val request = GetPhoneNumberHintIntentRequest.builder().build()
        analyticsHandler.postEvent(OnboardingConstants.AnalyticsKeys.Initiated_newphonenumberhint)
        Identity.getSignInClient(requireActivity())
            .getPhoneNumberHintIntent(request)
            .addOnFailureListener { e ->
                requestHintOldAPI(true)
                Timber.e(e)
            }.addOnSuccessListener { pendingIntent ->
                val intentSenderRequest: IntentSenderRequest =
                    IntentSenderRequest.Builder(pendingIntent.intentSender)
                        .build()
                phonePickIntentResultLauncher?.launch(intentSenderRequest)
                analyticsHandler.postEvent(OnboardingConstants.AnalyticsKeys.Shown_newphonenumberhint)
            }
    }

    override fun onDestroyView() {
        analyticsHandler.postEvent(
            OnboardingConstants.AnalyticsKeys.Exit_LoginScreen_Onboarding,
            mapOf(OnboardingConstants.AnalyticsKeys.TIME_SPENT to System.currentTimeMillis() - timeInit)
        )
        val endTime = System.currentTimeMillis()
        newOnboardingViewModel.updateScreenTime(
            screenName = OnboardingStateMachine.State.EnterName,
            timeSpentOnScreen = timeInit.orZero().minus(endTime)
        )
        super.onDestroyView()
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
        phonePickIntentResultLauncher = null
        phonePickIntentResultLauncherOldApi = null
    }

    private fun setEditTextBackground(value: String) {
        binding.clNumber.background = ContextCompat.getDrawable(
            requireContext(),
            if (StringUtils.isValidPhoneNumber(value))
                R.drawable.bg_rounded_272239_20dp
            else
                com.jar.app.core_ui.R.drawable.core_ui_bg_rounded_2e2942_outline_eb6a6e_10dp
        )
        binding.tvErrorMessage.isVisible = StringUtils.isValidPhoneNumber(value).not()
    }

    private fun toggleExperianVerifyButton() {
        val number = binding.etNumber.text?.toString()
        if ((onboardingStateMachine.isRegisteredUser)) {
            binding.btnExperianGetOtp.setDisabled(StringUtils.isValidPhoneNumber(number).not())
        } else {
            binding.btnExperianGetOtp.setDisabled((StringUtils.isValidPhoneNumber(number) && binding.checkbox.isChecked).not())
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onLogoutFromOtherDevices(logoutFromOtherEvent: com.jar.app.feature_onboarding.shared.domain.event.LogoutFromOtherDevicesEvent) {
        EventBus.getDefault().removeStickyEvent(logoutFromOtherEvent)
        viewModel.truecallerLoginSuccessful(
            viewModel.payload,
            viewModel.signature,
            viewModel.signatureAlgorithm,
            true
        )
    }

    private fun initateLogin(number: String, hasExperianConsent: Boolean? = null) {
        updateEventParamsValue()
        viewLifecycleOwner.lifecycleScope.launch {
            showProgressBar()
            withContext(Dispatchers.IO) {
                val otlLoginNumber =
                    number.prependIndent(BaseConstants.DEFAULT_COUNTRY_CODE).toLong()
                if (remoteConfigApi.shouldUseOTL()) {
                    val correlationId = RandomUUIDGenerator.randomUUID()

                    val authCallback = object : AuthCallback {
                        override fun onResult(authenticationStatus: AuthenticationStatus) {
                            if (authenticationStatus.name == AuthenticationStatus.Completed.name) {
                                onboardingStateMachine.correlationId = correlationId
                                onboardingStateMachine.isOtlFlow = true
                            }
                            sendProceedClickedEvent(authenticationStatus.name, number)
                            checkNetworkAndNavigate(number, hasExperianConsent)
                        }
                    }
                    bureauAuth.authenticate(
                        requireContext(),
                        correlationId,
                        otlLoginNumber,
                        authCallback
                    )
                } else {
                    sendProceedClickedEvent(number = number)
                    checkNetworkAndNavigate(number, hasExperianConsent)
                }
            }
        }
    }

    private fun checkNetworkAndNavigate(number: String, hasExperianConsent: Boolean? = null) {
        if(isAdded){
            dismissProgressBar()
        }
        if (isNetworkAvailable) {
            onboardingStateMachine.hasExperianConsent =
                hasExperianConsent ?: binding.checkbox.isChecked
            onboardingStateMachine.phoneNumber = number
            onboardingStateMachine.countryCode = BaseConstants.DEFAULT_COUNTRY_CODE
            onboardingStateMachine.navigateAhead()
        }
    }

    private fun updateEventParamsValue() {
        isExperianCheckboxChecked = binding.btnExperianGetOtp.isEnabled
        isExperianCheckboxChecked = binding.checkbox.isChecked
    }

    private fun sendProceedClickedEvent(resultMessage: String = "", number: String) {
        viewModel.fireEvent(
            OnboardingConstants.AnalyticsKeys.ClickedGetOTP_LoginScreen_Onboarding,
            mutableMapOf(
                OnboardingConstants.AnalyticsKeys.Login_type to if (resultMessage == AuthenticationStatus.Completed.name) OnboardingConstants.AnalyticsKeys.OTL else OnboardingConstants.AnalyticsKeys.OTP,
                OnboardingConstants.AnalyticsKeys.ENABLED to isExperianCtaEnabled,
                OnboardingConstants.AnalyticsKeys.NUMBER to number,
                OnboardingConstants.AnalyticsKeys.Is_checked to isExperianCheckboxChecked
            )
        )
    }
}
