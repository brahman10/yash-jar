package com.jar.app.feature.onboarding.ui.enter_otp

import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.text.Spannable
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import androidx.core.text.toSpannable
import androidx.core.text.underline
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.tasks.Task
import com.jar.app.HomeNavigationDirections
import com.jar.app.R
import com.jar.app.base.data.event.OTPReceivedEvent
import com.jar.app.base.data.event.SyncDeviceDetailsEvent
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.data.receiver.OTPSmsBroadcastReceiver
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.*
import com.jar.app.core_analytics.EventKey
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.BaseConstants.DEFAULT_VALUES_FOR_NO_OF_SMS_SYNC
import com.jar.app.core_base.util.orFalse
import com.jar.app.core_base.util.orZero
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.core_ui.extension.hideKeyboard
import com.jar.app.core_ui.extension.makeColoredLink
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.setOnImeActionDoneListener
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.core_utils.data.NetworkFlow
import com.jar.app.databinding.FragmentNewEnterOtpBinding
import com.jar.app.feature.home.ui.activity.HomeActivityViewModel
import com.jar.app.feature.onboarding.NewOnboardingViewModelAndroid
import com.jar.app.feature.onboarding.ui.enter_number.EnterNumberFragment
import com.jar.app.feature.truecaller.TruecallerLoginManager
import com.jar.app.feature_onboarding.shared.domain.model.CustomOnboardingData
import com.jar.app.feature_onboarding.shared.util.OnboardingConstants
import com.jar.app.feature_user_api.domain.model.RequestOtpData
import com.jar.app.util.*
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import com.truecaller.android.sdk.TrueError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.threeten.bp.Duration
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
class EnterOtpFragment : BaseFragment<FragmentNewEnterOtpBinding>() {

    @Inject
    lateinit var onboardingStateMachine: com.jar.app.feature_onboarding.shared.data.state_machine.OnboardingStateMachine

    @Inject
    lateinit var remoteConfigManager: RemoteConfigApi

    @Inject
    lateinit var analyticsApi: AnalyticsApi

    @Inject
    lateinit var prefsApi: PrefsApi

    @Inject
    lateinit var remoteConfigApi: RemoteConfigApi

    @Inject
    lateinit var networkFlow: NetworkFlow

    private var truecallerLoginManager: TruecallerLoginManager? = null

    private val timeInit = System.currentTimeMillis()

    private val args by navArgs<EnterOtpFragmentArgs>()

    private val viewModelProvider by viewModels<EnterOtpFragmentViewModelAndroid> { defaultViewModelProviderFactory }

    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }

    private val homeViewModel by activityViewModels<HomeActivityViewModel> { defaultViewModelProviderFactory }

    private val newOnboardingViewModelProvider by activityViewModels<NewOnboardingViewModelAndroid>()

    private val newOnboardingViewModel by lazy {
        newOnboardingViewModelProvider.getInstance()
    }

    private val otpSmsBroadcastReceiver by lazy { OTPSmsBroadcastReceiver() }

    private var requestOtpData: RequestOtpData? = null

    private var timerJob: Job? = null

    private var fetchOtpStatusJob: Job? = null

    private var whatsAppNumber = ""

    private var timeInitTruecaller = System.currentTimeMillis()

    private var otpTimeLeft = -1L
    private var callTimeLeft = -1L
    private var otpErrorCount = 0

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentNewEnterOtpBinding
        get() = FragmentNewEnterOtpBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val number = args.phoneNumber
        homeViewModel.fetchCustomisedOnboardingFlow("+91$number")
        truecallerLoginManager =
            TruecallerLoginManager(requireContext().applicationContext, remoteConfigManager)
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
        requestOtp()
        analyticsApi.postEvent(
            EventKey.Shown_OTPScreen_Onboarding
        )
    }

    private var trueCallerListener: TruecallerLoginManager.TruecallerLoginListener? =
        object : TruecallerLoginManager.TruecallerLoginListener {

            override fun onTruecallerLoginSuccess(
                payload: String,
                signature: String,
                signatureAlgorithm: String,
            ) {
                viewModel.trueCallerAuthDone = true
                viewModel.truecallerLoginSuccessful(payload, signature, signatureAlgorithm, false)
                analyticsApi.postEvent(
                    EventKey.Clicked_UseTruecaller_Onboarding,
                    mapOf(EventKey.RESPONSE to EnterNumberFragment.SUCCESS)
                )
                analyticsApi.postEvent(
                    EventKey.Exit_Truecaller_Onboarding,
                    mapOf(EventKey.TIME_SPENT to System.currentTimeMillis() - timeInitTruecaller)
                )
            }

            override fun onTruecallerLoginFailure(errorType: Int) {
                viewModel.trueCallerAuthDone = true
                when (errorType) {
                    TrueError.ERROR_TYPE_CONTINUE_WITH_DIFFERENT_NUMBER -> analyticsApi.postEvent(
                        EventKey.Clicked_AnotherMobileNumber_Onboarding
                    )

                    TrueError.ERROR_TYPE_USER_DENIED -> {
                    }

                    else -> analyticsApi.postEvent(
                        EventKey.Clicked_UseTruecaller_Onboarding, mapOf(
                            EventKey.RESPONSE to EnterNumberFragment.FAILURE,
                            EventKey.ERROR to errorType
                        )
                    )
                }
                analyticsApi.postEvent(
                    EventKey.Exit_Truecaller_Onboarding,
                    mapOf(EventKey.TIME_SPENT to System.currentTimeMillis() - timeInitTruecaller)
                )
            }

            override fun onTruecallerNotUsable() {
                viewModel.trueCallerAuthDone = true
            }

            override fun onVerificationRequired() {
                viewModel.trueCallerAuthDone = true
                analyticsApi.postEvent(EventKey.CLICKEDANOTHERMOBILENUMBER_ONBOARDING)
            }
        }

    private fun startSMSRetrieverClient() {
        val task1 = SmsRetriever.getClient(requireContext()).startSmsUserConsent(args.phoneNumber)
        task1.addOnSuccessListener { it }
        activity?.registerReceiver(
            otpSmsBroadcastReceiver, IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        )
        val client = SmsRetriever.getClient(requireContext())
        val task: Task<Void> = client.startSmsRetriever()
        task.addOnSuccessListener {}
        task.addOnFailureListener {}
    }

    private fun setupUI() {
        toggleMainButton(true)
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
                analyticsApi.postEvent(EventKey.ClickedTermsAndConditions_LoginScreen_Onboarding)
            },
            {
                openUrlInChromeTab(
                    remoteConfigApi.getPrivacyPolicyUrl(),
                    getString(R.string.privacy_policy),
                    true
                )
                analyticsApi.postEvent(EventKey.ClickedPrivacyPolicy_LoginScreen_Onboarding)
            }
        )
        setOtpCallSpannable()
    }

    private fun setOtpCallSpannable(isOtp: Boolean = true) {
        val phoneNumber = args.countryCode.plus(args.phoneNumber)
        val phoneNumberSplit = try {
            phoneNumber.slice(0..1) + "- " + phoneNumber.slice(2..11)
        } catch (e: Exception) {
            phoneNumber
        }
        val spannable = buildSpannedString {
            if (isOtp) {
                append(getString(R.string.feature_onboarding_enter_otp_description))
                binding.tvTimer.isVisible = false
            } else append(getString(R.string.you_will_receive_call))
            append(" +")
            append(phoneNumberSplit)
        }.toSpannable()
        spannable.setSpan(
            ForegroundColorSpan(
                ContextCompat.getColor(
                    requireContext(), com.jar.app.core_ui.R.color.color_EEEAFF
                )
            ), spannable.indexOf("+"), spannable.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.tvDescription.text = spannable
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupListeners() {
        binding.btnVerify.setDebounceClickListener {
            analyticsApi.postEvent(EventKey.ClickedVerify_OTPScreen_Onboarding)
            it.context.hideKeyboard(it)
            val otp = binding.otpView.text?.toString()
            if (!otp.isNullOrBlank() && requestOtpData != null) {
                viewModel.loginViaOtp(
                    args.phoneNumber, args.countryCode, otp, requestOtpData?.reqId!!
                )
            } else {
                getString(R.string.please_enter_a_valid_otp).snackBar(binding.root)
            }
        }

        binding.tvResendOtp.setDebounceClickListener {
            if (otpTimeLeft < 0) {
                requestOtp()
                setOtpCallSpannable()
                analyticsApi.postEvent(EventKey.ClickedResendOTP_OTPScreen_Onboarding)
            }
        }
        binding.tvCallTimer.setDebounceClickListener {
            requestOtpViaCall()
            setOtpCallSpannable(false)
            analyticsApi.postEvent(EventKey.ClickedCallToVerify_OTPScreen_Onboarding)
        }

        binding.otpView.textChanges().debounce(300).onEach {
            toggleMainButton()
            if (binding.tvOtpError.isVisible) toggleOtpErrorLayout(false)
        }.launchIn(uiScope)


        binding.otpView.setOnImeActionDoneListener {
            if (isBindingInitialized() && lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) binding.btnVerify.performClick()
        }

        view?.setOnTouchListener { v: View, _ ->
            v.hideKeyboard()
            false
        }
        binding.btnViewOtpOnWhatsapp.setDebounceClickListener {
            requireContext().openWhatsapp(whatsAppNumber)
            analyticsApi.postEvent(EventKey.ClickedViewOtpOnWhatsapp_OTPScreen_Onboarding)
        }
        binding.btnLoginWithTruecaller.setDebounceClickListener {
            viewModel.trueCallerAuthDone = false
            initiateTruecaller()
            analyticsApi.postEvent(EventKey.Clicked_ContinueWithTruecaller_Onboarding)
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

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.otpLoginFlow.collect(onLoading = { showProgressBar() }, onSuccess = {
                    dismissProgressBar()
                    it?.let {
                        if (it.hasOtherActiveSessions.orFalse()) {
                            navigateTo(
                                HomeNavigationDirections.actionToActiveSessionDetectedFragment(
                                    null
                                ), false
                            )
                        } else {
                            homeViewModel.numberOfDaysOfSms =
                                it.numberOfDaysOfSms ?: DEFAULT_VALUES_FOR_NO_OF_SMS_SYNC
                            newOnboardingViewModel.saveUserData(it)
                            newOnboardingViewModel.updateWeeklyChallengeData()
                            onboardingStateMachine.mUser = it.user

                            //Possible values of authType - SIGNUP/LOGIN  (Used to track re logins)
                            prefsApi.setAuthType(it.authType.orEmpty())
                            if (it.authType.orEmpty() == BaseConstants.LOGIN) {
                                analyticsApi.postEvent(
                                    EventKey.OLD_USER_LOGIN,
                                    mapOf(OnboardingConstants.AnalyticsKeys.Login_type to OnboardingConstants.AnalyticsKeys.OTP)
                                )
                                prefsApi.setAppWalkThroughShownToUser(true)
                            } else if (it.authType.orEmpty() == BaseConstants.SIGNUP) {
                                analyticsApi.postEvent(
                                    EventKey.NEW_USER_SIGNUP,
                                    mapOf(OnboardingConstants.AnalyticsKeys.Login_type to OnboardingConstants.AnalyticsKeys.OTP)
                                )
                                analyticsApi.postEvent(EventKey.NEW_USER_SIGNUP_MOBILE)
                                prefsApi.setAppWalkThroughShownToUser(false)
                            }

                            prefsApi.setUserPhoneNumber(it.user.phoneNumber)
                            //Used to track logins
                            analyticsApi.postEvent(
                                EventKey.AUTH_SUCCESSFUL, mapOf(
                                    EventKey.PROP_SOURCE to EventKey.OTP,
                                    EventKey.AuthType to it.authType.orEmpty()
                                )
                            )
                            EventBus.getDefault().post(SyncDeviceDetailsEvent())
                            onboardingStateMachine.navigateAhead()
                        }
                    }
                }, onError = { errorMessage, _ ->
                    dismissProgressBar()
                    toggleOtpErrorLayout(true, errorMessage)
                })
            }
        }

        viewModel.truecallerLoginFlow.asLiveData().asSingleLiveEvent()
            .observeNetworkResponse(viewLifecycleOwner, WeakReference(binding.root), onLoading = {
                showProgressBar()
            }, onSuccess = {
                dismissProgressBar()
                if (it.hasOtherActiveSessions.orFalse()) {
                    navigateTo(
                        HomeNavigationDirections.actionToActiveSessionDetectedFragment(null), false
                    )
                } else {
                    homeViewModel.numberOfDaysOfSms =
                        it.numberOfDaysOfSms ?: DEFAULT_VALUES_FOR_NO_OF_SMS_SYNC
                    newOnboardingViewModel.saveUserData(it)
                    onboardingStateMachine.mUser = it.user

                    //Possible values of authType - SIGNUP/LOGIN  (Used to track re logins)
                    prefsApi.setAuthType(it.authType.orEmpty())
                    if (it.authType.orEmpty() == BaseConstants.LOGIN) {
                        analyticsApi.postEvent(EventKey.OLD_USER_LOGIN)
                    }
                    else if (it.authType.orEmpty() == BaseConstants.SIGNUP) {
                        analyticsApi.postEvent(EventKey.NEW_USER_SIGNUP)
                        analyticsApi.postEvent(EventKey.NEW_USER_SIGNUP_MOBILE)
                    }

                    prefsApi.setUserPhoneNumber(it.user.phoneNumber)

                    //Used to track logins
                    analyticsApi.postEvent(
                        EventKey.AUTH_SUCCESSFUL, mapOf(
                            EventKey.PROP_SOURCE to EventKey.TRUECALLER,
                            EventKey.AuthType to it.authType.orEmpty()
                        )
                    )
                    EventBus.getDefault().post(SyncDeviceDetailsEvent())

                    onboardingStateMachine.navigateAfterTruecallerCompletion()
                }
            }, onError = {
                dismissProgressBar()
            })


        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.requestOtpFlow.collect(onSuccess = {
                    startTimer()
                    startOtpStatusTimer()
                    requestOtpData = it
                }, onSuccessWithNullData = {
                    startTimer()
                    startOtpStatusTimer()
                }, onError = { errorMessage, _ ->
                    errorMessage.snackBar(binding.root)
                })
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.fetchOtpStatusFlow.collect(onSuccess = {
                    whatsAppNumber = it.whatsappNumber
                    binding.whatsappDataGroup.isVisible = it.shouldShowWhatsappCta
                    if (truecallerLoginManager?.isTruecallerUsable()
                            .orFalse()
                    ) binding.btnLoginWithTruecaller.isVisible = it.shouldShowWhatsappCta
                }, onError = { errorMessage, _ ->
                    toggleOtpErrorLayout(
                        true,
                        if (errorMessage.isBlank()) getString(com.jar.app.core_ui.R.string.something_went_wrong) else errorMessage
                    )
                })
            }
        }


        homeViewModel.customisedOnboardingLiveData.observeNetworkResponse(this,
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

    private fun requestOtp() {
        binding.whatsappDataGroup.isVisible = false
        viewModel.requestOtp(args.hasExperianConsent, args.phoneNumber, args.countryCode)
    }

    private fun requestOtpViaCall() {
        binding.whatsappDataGroup.isVisible = false
        viewModel.requestOtpViaCall(args.phoneNumber, args.countryCode)
    }

    private fun startOtpStatusTimer() {
        fetchOtpStatusJob?.cancel()
        fetchOtpStatusJob = uiScope.countDownTimer(Duration.ofSeconds(15).toMillis(),
            onInterval = {},
            onFinished = {
                viewModel.fetchOTPStatus(args.phoneNumber, args.countryCode)
            })
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = uiScope.countDownTimer(Duration.ofSeconds(30).toMillis(), onInterval = {
            otpTimeLeft = it
            callTimeLeft = it
            binding.tvTimer.isVisible = true
            binding.tvResendOtp.isVisible = false
            binding.tvCallTimer.isVisible = false
            binding.tvTimer.text = getResendOtpString(it)
        }, onFinished = {
            binding.tvTimer.isVisible = false
            otpTimeLeft = -1L
            callTimeLeft = -1L
            binding.tvResendOtp.isVisible = true
            binding.tvResendOtp.text = getResendOtpString(0)
            binding.tvCallTimer.isVisible = true
            analyticsApi.postEvent(
                EventKey.Shown_OtherSignup_Options, mapOf(
                    BaseConstants.truecallerShown to if (binding.btnLoginWithTruecaller.isVisible) "YES" else "NO",
                    BaseConstants.otpWhatsAppShown to if (binding.btnViewOtpOnWhatsapp.isVisible) "YES" else "NO"
                )
            )
        })
    }

    private fun getResendOtpString(millisLeft: Long): Spannable {
        val spannableBuilder = buildSpannedString {
            if (millisLeft > 0) {
                val t1 = getString(R.string.feature_onboarding_otp_valid_till).plus(" ")
                val t2 = millisLeft.milliSecondsToCountDown()
                append(t1)
                bold{
                    append(t2)
                }
            } else {
                underline {
                    append(getString(R.string.resend_otp))
                }
            }
        }
        val spannable = spannableBuilder.toSpannable()
        val start = if (millisLeft > 0) spannable.lastIndexOf(" ") else 0
        spannable.setSpan(
                ForegroundColorSpan(
                    ContextCompat.getColor(
                        requireContext(), if(millisLeft > 0)  com.jar.app.core_ui.R.color.white else com.jar.app.core_ui.R.color.color_D5CDF2
                    )
                ), start, spannable.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        return spannable
    }

    override fun onStart() {
        super.onStart()
        startSMSRetrieverClient()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        activity?.unregisterReceiver(otpSmsBroadcastReceiver)
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onOTPReceivedEvent(otpReceivedEvent: OTPReceivedEvent) {
        EventBus.getDefault().removeStickyEvent(otpReceivedEvent)
        val otp = otpReceivedEvent.originalMessage.getOtp(requestOtpData?.length) ?: ""
        binding.otpView.setText(otp)
        analyticsApi.postEvent(EventKey.OTP_SMSDetected)
        toggleMainButton()
        viewModel.loginViaOtp(
            args.phoneNumber, args.countryCode, otp, requestOtpData?.reqId!!
        )
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onLogoutFromOtherDevices(logoutFromOtherEvent: com.jar.app.feature_onboarding.shared.domain.event.LogoutFromOtherDevicesWhileOnboarding) {
        EventBus.getDefault().removeStickyEvent(logoutFromOtherEvent)
        prefsApi.setNewOnboardingState(com.jar.app.feature_onboarding.shared.data.state_machine.OnboardingStateMachine.State.EnterName.toString())
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onLogoutFromOtherDevices(logoutFromOtherEvent: com.jar.app.feature_onboarding.shared.domain.event.LogoutFromOtherDevicesEvent) {
        EventBus.getDefault().removeStickyEvent(logoutFromOtherEvent)
        viewModel.loginViaOtp(
            args.phoneNumber, args.countryCode, viewModel.currentOtp, requestOtpData?.reqId!!, true
        )
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onNavigateBackEvent(navigateBackEvent: com.jar.app.feature_onboarding.shared.domain.event.NavigateBackEvent) {
        uiScope.launch(Dispatchers.Main) {
            delay(500)
            requireActivity().onBackPressed() //This is intentional.. new onboarding has special back press handling.. Don't replace with popBackStack..
        }
    }

    private fun toggleMainButton(disableAnyway: Boolean = false) {
        val shouldEnable = if (disableAnyway) false
        else ((binding.otpView.text?.length ?: 0) == (requestOtpData?.length ?: 6))
        binding.btnVerify.setDisabled(shouldEnable.not())
    }

    private fun toggleOtpErrorLayout(isError: Boolean, errorMsg: String = "") {
        binding.otpView.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (isError) com.jar.app.core_ui.R.color.color_EB6A6E else com.jar.app.core_ui.R.color.white
            )
        )
        binding.otpView.setLineColor(
            ContextCompat.getColor(
                requireContext(),
                if (isError) com.jar.app.core_ui.R.color.color_EB6A6E else com.jar.app.core_ui.R.color.white
            )
        )
        if (isError) {
            binding.tvOtpError.text = errorMsg
            analyticsApi.postEvent(
                EventKey.ShownErrorMessage_OTPScreen_Onboarding, mapOf(EventKey.MESSAGE to errorMsg)
            )
        }
        binding.tvOtpError.isVisible = isError
    }

    private fun initiateTruecaller() {
        if (viewModel.trueCallerAuthDone) return
        truecallerLoginManager?.showTruecallerPopup(WeakReference(this), trueCallerListener!!)
        analyticsApi.postEvent(EventKey.Shown_Truecaller_Onboarding)
        timeInitTruecaller = System.currentTimeMillis()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        truecallerLoginManager?.onActivityResult(
            WeakReference(requireActivity()), requestCode, resultCode, data
        )
    }

    override fun onDestroyView() {
        truecallerLoginManager?.clear()
        trueCallerListener = null
        truecallerLoginManager = null
        val endTime = System.currentTimeMillis()
        newOnboardingViewModel.updateScreenTime(
            screenName = com.jar.app.feature_onboarding.shared.data.state_machine.OnboardingStateMachine.State.EnterName,
            timeSpentOnScreen = timeInit.orZero().minus(endTime)
        )
        analyticsApi.postEvent(
            EventKey.Exit_OTPScreen_Onboarding,
            mapOf(EventKey.TIME_SPENT to System.currentTimeMillis() - timeInit)
        )
        super.onDestroyView()
    }
}