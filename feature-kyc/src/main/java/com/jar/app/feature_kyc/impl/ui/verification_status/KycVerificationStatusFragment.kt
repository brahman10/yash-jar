package com.jar.app.feature_kyc.impl.ui.verification_status

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.jar.app.base.data.event.GoToHomeEvent
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarDefault
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.decodeUrl
import com.jar.app.base.util.openWhatsapp
import com.jar.app.base.util.orFalse
import com.jar.app.base.util.orTrue
import com.jar.app.core_analytics.EventKey
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.core_ui.extension.playLottieWithUrlAndExceptionHandling
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_kyc.R
import com.jar.app.feature_kyc.databinding.FragmentKycVerificationStatusBinding
import com.jar.app.feature_kyc.shared.domain.model.KycStatus
import com.jar.app.feature_kyc.shared.domain.model.KycVerificationStatus
import com.jar.app.feature_kyc.shared.util.KycConstants
import com.jar.app.feature_kyc.shared.util.KycConstants.AnalyticsKeys.IdentityVerificationSuccess_Screen_Clicked
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@AndroidEntryPoint
internal class KycVerificationStatusFragment :
    BaseFragment<FragmentKycVerificationStatusBinding>() {

    @Inject
    lateinit var remoteConfigApi: RemoteConfigApi

    @Inject
    lateinit var serializer: Serializer

    @Inject
    lateinit var prefsApi: PrefsApi

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    private val args by navArgs<KycVerificationStatusFragmentArgs>()

    private val fromScreen by lazy {
        args.fromScreen
    }

    companion object {
        private const val Identity_Verification_Result = "Identity Verification Result"
    }

    private val kycStatus by lazy {
        serializer.decodeFromString<KycStatus>(decodeUrl(args.kycStatus))
    }

    private var backPressCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                when (kycStatus.verificationStatus) {
                    KycVerificationStatus.VERIFIED.name -> {} //Do nothing on success since we will redirect the user to kyc details screen after lottie animation
                    KycVerificationStatus.PENDING.name -> gotoHome()
                    else -> handleBackPressForOtherStatus()
                }
            }
        }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentKycVerificationStatusBinding
        get() = FragmentKycVerificationStatusBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(
            UpdateAppBarEvent(
                AppBarData(
                    ToolbarDefault(
                        getCustomString(com.jar.app.feature_kyc.shared.MR.strings.feature_kyc_identity_verification),
                        showSeparator = true,
                        showBackButton = kycStatus.verificationStatus != KycVerificationStatus.PENDING.name
                    )
                )
            )
        )
    }

    private fun registerBackPressDispatcher() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            backPressCallback
        )
        backPressCallback.isEnabled = true
    }

    override fun setup(savedInstanceState: Bundle?) {
        registerBackPressDispatcher()
        setupUI()
        initClickListeners()
    }

    private fun initClickListeners() {
        binding.verificationStatusLayout.chatWhatsapp.setDebounceClickListener {
            val number = remoteConfigApi.getWhatsappNumber()
            context?.openWhatsapp(number, kycStatus.shareMsg.orEmpty())
        }
    }

    private fun setupUI() {
        if (kycStatus.verificationStatus == KycVerificationStatus.VERIFIED.name) {
            setupSuccessStatusUI()
        } else {
            setupOtherStatusUI(kycStatus.verificationStatus)
        }
        analyticsHandler.postEvent(
            KycConstants.AnalyticsKeys.SHOWN_IDENTITY_VERIFICATION__RESULT_SCREEN,
            mapOf(
                EventKey.FromScreen to fromScreen,
                KycConstants.AnalyticsKeys.RESULT_SHOWN to kycStatus.verificationStatus.toString()
            )
        )
    }

    private fun setupSuccessStatusUI() {
        kycStatus.getLottieForStatus()?.let {
            binding.lottieViewSuccess.playLottieWithUrlAndExceptionHandling(
                requireContext(),
                it
            )
        }
        binding.tvSuccessDes.text = kycStatus.title
        binding.clSuccess.isVisible = true
        binding.verificationStatusLayout.root.isVisible = false
        uiScope.launch {
            delay(3000)


            //TODO: Test flow till here and check fromScreen == SELL_GOLD_REVAMP
//            if (fromScreen == "Sell Gold Revamp Screen") {
//                navigateTo(
//                    "android-app://com.jar.app/sellGoldRevamp/${kycStatus.verificationStatus}",
//                    popUpTo = R.id.completeKycInfoFragmentV2,
//                    inclusive = true
//                )
//            } else {

            if (fromScreen == BaseConstants.SellGoldFlow.FROM_SELL_GOLD_REVAMP || fromScreen == BaseConstants.SellGoldFlow.FROM_SELL_GOLD_REVAMP_PAN_ONLY) {
                findNavController().getBackStackEntry(R.id.completeKycInfoFragmentV2).savedStateHandle.set(
                    KycConstants.KYC_FLOW_STATE,
                    kycStatus.verificationStatus
                )
                popBackStack(R.id.completeKycInfoFragmentV2, inclusive = true)
            } else {
                navigateTo(
                    "android-app://com.jar.app/kycDetails/$Identity_Verification_Result",
                    popUpTo = R.id.completeKycInfoFragmentV2,
                    inclusive = true
                )
            }
        }
    }

    private fun setupOtherStatusUI(status: String?) {
        kycStatus.getLottieForStatus()?.let {
            binding.verificationStatusLayout.lottieView.playLottieWithUrlAndExceptionHandling(
                requireContext(),
                it
            )
        }
        binding.verificationStatusLayout.tvTitle.text = kycStatus.title
        binding.verificationStatusLayout.tvDescription.text = kycStatus.description
        if (kycStatus.allRetryExhausted.orFalse()) {
            binding.verificationStatusLayout.btnAction.isVisible = false
        } else {
            binding.verificationStatusLayout.btnAction.isVisible = kycStatus.shouldTryAgain.orTrue()
        }
        if (status == KycVerificationStatus.PENDING.name) {
            binding.verificationStatusLayout.chatWhatsapp.isVisible = false
            binding.verificationStatusLayout.frameChatWA.isVisible = false
            binding.verificationStatusLayout.btnAction.setText(
                getCustomString(com.jar.app.feature_kyc.shared.MR.strings.feature_kyc_goto_home)
            )
            binding.verificationStatusLayout.btnAction.isVisible =
                true //Show GotoHome irrespective of the flag if status is PENDING
            initVerifyingClickListeners()
        } else {
            binding.verificationStatusLayout.chatWhatsapp.isVisible = true
            binding.verificationStatusLayout.frameChatWA.isVisible = true
            binding.verificationStatusLayout.btnAction.setText(
                getCustomString(com.jar.app.feature_kyc.shared.MR.strings.feature_kyc_try_again)
            )
            initFailedAndRejectedClickListeners()
        }
        binding.verificationStatusLayout.root.isVisible = true

        binding.clSuccess.isVisible = false
    }

    private fun initVerifyingClickListeners() {
        binding.verificationStatusLayout.btnAction.setDebounceClickListener {
            analyticsHandler.postEvent(IdentityVerificationSuccess_Screen_Clicked)
            gotoHome()
        }
    }

    private fun initFailedAndRejectedClickListeners() {
        binding.verificationStatusLayout.btnAction.setDebounceClickListener {
            analyticsHandler.postEvent(
                KycConstants.AnalyticsKeys.CLICKED_BUTTON_IDENTITY_VERIFICATION_RESULT_SCREEN,
                mapOf(
                    KycConstants.AnalyticsKeys.CLICKED_BUTTON
                            to getCustomLocalizedString(
                        requireContext(),
                        com.jar.app.feature_kyc.shared.MR.strings.feature_kyc_try_again,
                        prefsApi.getCurrentLanguageCode()
                    ),
                )
            )
            if (!kycStatus.isFromFlow) {
                navigateTo(
                    "android-app://com.jar.app/kycVerification/$Identity_Verification_Result",
                    popUpTo = R.id.kycVerificationStatusFragment,
                    inclusive = true
                )
            } else {
                navigateTo(
                    "android-app://com.jar.app/kycVerification/$Identity_Verification_Result",
                    popUpTo = R.id.completeKycInfoFragmentV2,
                    inclusive = false
                )
            }
        }
    }

    private fun gotoHome() {
        analyticsHandler.postEvent(
            KycConstants.AnalyticsKeys.CLICKED_GO_TO_HOME_BUTTON_IDENTITY_VERIFICATION_SCREEN
        )
        EventBus.getDefault().post(GoToHomeEvent("KYC_Status"))
    }

    private fun handleBackPressForOtherStatus() {
        if (!kycStatus.isFromFlow) {
            popBackStack()
        } else {
            popBackStack(R.id.completeKycInfoFragmentV2, false)
        }
    }
}