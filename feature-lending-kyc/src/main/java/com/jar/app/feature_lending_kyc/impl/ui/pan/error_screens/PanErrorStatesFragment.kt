package com.jar.app.feature_lending_kyc.impl.ui.pan.error_screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.text.toSpannable
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.jar.app.base.data.event.GoToHomeEvent
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.encodeUrl
import com.jar.app.base.util.isPresentInBackStack
import com.jar.app.base.util.openWhatsapp
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.orFalse
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.core_ui.extension.playLottieWithUrlAndExceptionHandling
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_lending_kyc.FeatureLendingKycStepsNavigationDirections
import com.jar.app.feature_lending_kyc.R
import com.jar.app.feature_lending_kyc.databinding.FeatureLendingKycFragmentPanErrorStatesBinding
import com.jar.app.feature_lending_kyc.impl.data.Step
import com.jar.app.feature_lending_kyc.impl.domain.event.ToolbarStepsVisibilityEvent
import com.jar.app.feature_lending_kyc.shared.domain.model.PanErrorScreenPrimaryButtonAction
import com.jar.app.feature_lending_kyc.shared.domain.model.PanErrorScreenSecondaryButtonAction
import com.jar.app.feature_lending_kyc.impl.ui.pan.report_not_fetched.CreditReportNotAvailableFragment
import com.jar.app.feature_lending_kyc.impl.ui.steps.LendingKycNavigationGenerator
import com.jar.app.feature_lending_kyc.shared.domain.model.ManualPanEntryScreenArguments
import com.jar.app.feature_lending_kyc.shared.util.LendingKycEventKey
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@AndroidEntryPoint
internal class PanErrorStatesFragment :
    BaseFragment<FeatureLendingKycFragmentPanErrorStatesBinding>() {

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureLendingKycFragmentPanErrorStatesBinding
        get() = FeatureLendingKycFragmentPanErrorStatesBinding::inflate


    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    @Inject
    lateinit var prefs: PrefsApi

    @Inject
    lateinit var serializer: Serializer

    @Inject
    lateinit var remoteConfigApi: RemoteConfigApi

    private val args: PanErrorStatesFragmentArgs by navArgs()

    private var backPressCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                analyticsHandler.postEvent(
                    LendingKycEventKey.BackButtonClicked,
                    mapOf(
                        LendingKycEventKey.screen_name to LendingKycEventKey.PANErrorScreen
                    )
                )
                if (args.panErrorStatesArguments.fromScreen == LendingKycNavigationGenerator.CONTINUE_KYC_BOTTOM_SHEET)
                    EventBus.getDefault().post(
                        GoToHomeEvent(
                            PanErrorStatesFragment,
                            BaseConstants.HomeBottomNavigationScreen.PROFILE
                        )
                    )
                else
                    popBackStack()
            }
        }

    companion object {
        const val PanErrorStatesFragment = "PanErrorStatesFragment"
    }

    override fun setupAppBar() {
        EventBus.getDefault().post(ToolbarStepsVisibilityEvent(shouldShowSteps = false, Step.PAN))
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
        setupListener()
        registerBackPressDispatcher()
    }

    private fun setupUI() {
        analyticsHandler.postEvent(LendingKycEventKey.Lending_PANCreditReportNotFound)

        binding.tvTitle.text = args.panErrorStatesArguments.title
        binding.tvDescription.text = args.panErrorStatesArguments.description
        binding.lottieView.isVisible = args.panErrorStatesArguments.isLottie
        binding.ivIllustration.isVisible = args.panErrorStatesArguments.isLottie.not()
        if (args.panErrorStatesArguments.isLottie)
            binding.lottieView.playLottieWithUrlAndExceptionHandling(
                requireContext(),
                args.panErrorStatesArguments.assetUrl
            ) else
            Glide.with(this).load(args.panErrorStatesArguments.assetUrl)
                .into(binding.ivIllustration)

        binding.btnPrimaryAction.isVisible =
            args.panErrorStatesArguments.primaryAction.stringRes != null
        binding.btnSecondaryAction.isVisible =
            args.panErrorStatesArguments.secondaryAction.stringRes != null
        args.panErrorStatesArguments.primaryAction.stringRes?.let {
            binding.btnPrimaryAction.setText(getCustomString(it).toSpannable())
        }
        args.panErrorStatesArguments.secondaryAction.stringRes?.let {
            binding.btnSecondaryAction.text = getCustomString(it)
        }

        binding.btnSecondaryAction.paint.isUnderlineText = true

        if (args.panErrorStatesArguments.primaryAction == PanErrorScreenPrimaryButtonAction.RETAKE_PHOTO)
            analyticsHandler.postEvent(
                LendingKycEventKey.Shown_OCRPANErrorScreen,
                mapOf(
                    LendingKycEventKey.textDisplayed to args.panErrorStatesArguments.description
                )
            )
        else
            analyticsHandler.postEvent(
                LendingKycEventKey.Shown_PANVerificationFailureScreen,
                mapOf(
                    LendingKycEventKey.textDisplayed to args.panErrorStatesArguments.description
                )
            )
    }

    private fun setupListener() {
        binding.btnPrimaryAction.setDebounceClickListener {
            when (args.panErrorStatesArguments.primaryAction) {
                PanErrorScreenPrimaryButtonAction.GO_HOME -> {
                    EventBus.getDefault().post(GoToHomeEvent(args.panErrorStatesArguments.title, 2))
                }
                PanErrorScreenPrimaryButtonAction.ENTER_PAN_MANUALLY, PanErrorScreenPrimaryButtonAction.ENTER_PAN_AGAIN -> {
                    val screenArgs = encodeUrl(serializer.encodeToString(
                        ManualPanEntryScreenArguments(
                            if (args.panErrorStatesArguments.jarVerifiedPAN.orFalse())
                                CreditReportNotAvailableFragment.USER_HAS_DONE_PAN_VERIFICATION_ON_JAR_BEFORE
                            else
                                CreditReportNotAvailableFragment.USER_HAS_NOT_DONE_PAN_VERIFICATION_ON_JAR_BEFORE,
                            jarVerifiedPAN = args.panErrorStatesArguments.jarVerifiedPAN.orFalse(),
                            kycFeatureFlowType = args.panErrorStatesArguments.kycFeatureFlowType
                        )
                    ))
                    if (findNavController().isPresentInBackStack(R.id.enterPanManuallyFragment)){
                        popBackStack()
                    }else {
                        navigateTo(
                            FeatureLendingKycStepsNavigationDirections.actionToEnterPanManuallyStep(
                                screenArgs
                            ),
                            popUpTo = R.id.panErrorStatesFragment,
                            inclusive = true
                        )
                    }
                }
                PanErrorScreenPrimaryButtonAction.USE_PAN_SAVED_WITH_JAR -> {
                    navigateTo(
                        FeatureLendingKycStepsNavigationDirections.actionToPanFromJarLoadingDialog()
                    )
                }
                PanErrorScreenPrimaryButtonAction.RETAKE_PHOTO -> {
                    popBackStack(R.id.panCapturePhotoFragment, false)
                }
                else -> {}
            }

        }
        binding.btnSecondaryAction.setDebounceClickListener {
            when (args.panErrorStatesArguments.secondaryAction) {
                PanErrorScreenSecondaryButtonAction.CONTACT_SUPPORT -> {
                    val sendTo = remoteConfigApi.getWhatsappNumber()
                    val number = prefs.getUserPhoneNumber()
                    val name = prefs.getUserName()

                    val message = getCustomStringFormatted(
                        com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_contact_support_s_s_s,
                        args.panErrorStatesArguments.contactMessage.orEmpty(),
                        name.orEmpty(),
                        number.orEmpty()
                    )
                    requireContext().openWhatsapp(sendTo, message)
                }
                PanErrorScreenSecondaryButtonAction.ENTER_PAN_MANUALLY, PanErrorScreenSecondaryButtonAction.ENTER_PAN_AGAIN -> {
                    val screenArgs = encodeUrl(serializer.encodeToString(
                        ManualPanEntryScreenArguments(if (args.panErrorStatesArguments.jarVerifiedPAN.orFalse())
                            CreditReportNotAvailableFragment.USER_HAS_DONE_PAN_VERIFICATION_ON_JAR_BEFORE
                        else
                            CreditReportNotAvailableFragment.USER_HAS_NOT_DONE_PAN_VERIFICATION_ON_JAR_BEFORE,
                            jarVerifiedPAN = args.panErrorStatesArguments.jarVerifiedPAN.orFalse(),
                            kycFeatureFlowType = args.panErrorStatesArguments.kycFeatureFlowType
                        )))
                    if (findNavController().isPresentInBackStack(R.id.enterPanManuallyFragment)){
                        popBackStack()
                    }else{
                        navigateTo(
                            FeatureLendingKycStepsNavigationDirections.actionToEnterPanManuallyStep(
                                screenArgs
                            ),
                            popUpTo = R.id.panErrorStatesFragment,
                            inclusive = true
                        )
                    }
                }
                PanErrorScreenSecondaryButtonAction.NONE -> {}
                else -> {}
            }
        }
    }


    private fun registerBackPressDispatcher() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            backPressCallback
        )
    }

    override fun onDestroyView() {
        backPressCallback.isEnabled = false
        super.onDestroyView()
    }
}