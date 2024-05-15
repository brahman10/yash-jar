package com.jar.app.feature_lending_kyc.impl.ui.pan.report_not_fetched

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.text.toSpannable
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.app.base.util.encodeUrl
import com.jar.app.base.util.openWhatsapp
import com.jar.app.core_base.util.orFalse
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_lending_kyc.FeatureLendingKycStepsNavigationDirections
import com.jar.app.feature_lending_kyc.databinding.FeatureLendingKycFragmentCreditReportNotAvailableBinding
import com.jar.app.feature_lending_kyc.impl.data.Step
import com.jar.app.feature_lending_kyc.impl.domain.event.ToolbarStepsVisibilityEvent
import com.jar.app.feature_lending_kyc.shared.domain.model.PanErrorScreenPrimaryButtonAction
import com.jar.app.feature_lending_kyc.shared.domain.model.PanErrorScreenSecondaryButtonAction
import com.jar.app.feature_lending_kyc.impl.ui.steps.LendingKycStepsViewModel
import com.jar.app.core_base.data.dto.isFromP2POrLending
import com.jar.app.feature_lending_kyc.shared.domain.model.ManualPanEntryScreenArguments
import com.jar.app.feature_lending_kyc.shared.util.LendingKycEventKey
import com.jar.app.feature_lending_kyc.shared.util.LendingKycFlowType
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
internal class CreditReportNotAvailableFragment :
    BaseFragment<FeatureLendingKycFragmentCreditReportNotAvailableBinding>() {
    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureLendingKycFragmentCreditReportNotAvailableBinding
        get() = FeatureLendingKycFragmentCreditReportNotAvailableBinding::inflate

    @Inject
    lateinit var serializer: Serializer

    @Inject
    lateinit var prefs: PrefsApi

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    @Inject
    lateinit var remoteConfigApi: RemoteConfigApi

    private val args: CreditReportNotAvailableFragmentArgs by navArgs()

    private val lendingKycStepsViewModel: LendingKycStepsViewModel by activityViewModels()

    companion object {
        const val USER_HAS_NOT_DONE_PAN_VERIFICATION_ON_JAR_BEFORE =
            "User has not done PAN verification with Jar before"
        const val USER_HAS_DONE_PAN_VERIFICATION_ON_JAR_BEFORE =
            "User has done PAN verification with Jar before but the user had not provided Personal PAN details"
    }

    private var backPressCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (args.creditReportNotFetchedArguments.kycFeatureFlowType.isFromP2POrLending()) {
                    popBackStack()
                } else {
                    lendingKycStepsViewModel.viewOnlyNavigationRedirectTo(
                        LendingKycFlowType.PAN,
                        false,
                        WeakReference(requireActivity())
                    )
                }
            }
        }

    override fun setupAppBar() {
        EventBus.getDefault().post(ToolbarStepsVisibilityEvent(shouldShowSteps = true, Step.PAN))
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupToolbar()
        setupUI()
        setupListener()
        registerBackPressDispatcher()
    }

    private fun setupToolbar() {
        binding.toolbar.root.isVisible = args.creditReportNotFetchedArguments.kycFeatureFlowType.isFromP2POrLending()
        binding.toolbar.tvTitle.text = getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_pan_details)
        binding.toolbar.separator.isVisible = true
    }

    private fun setupUI() {
        binding.btnPrimaryAction.isVisible =
            args.creditReportNotFetchedArguments.primaryAction.stringRes != null
        binding.btnSecondaryAction.isVisible =
            args.creditReportNotFetchedArguments.secondaryAction.stringRes != null

        args.creditReportNotFetchedArguments.primaryAction.stringRes?.let {
            binding.btnPrimaryAction.setText(getCustomString(it).toSpannable())
        }

        args.creditReportNotFetchedArguments.secondaryAction.stringRes?.let {
            binding.btnSecondaryAction.text =
                getCustomString(it)
        }

        binding.tvCreditReportTitle.text = args.creditReportNotFetchedArguments.title
        binding.tvDescription.text = args.creditReportNotFetchedArguments.description
        Glide.with(requireContext())
            .load(args.creditReportNotFetchedArguments.assetUrl)
            .into(binding.ivIllustration)

        analyticsHandler.postEvent(
            LendingKycEventKey.Shown_CreditReportNotFoundScreen,
            mapOf(
                LendingKycEventKey.fromScreen to args.creditReportNotFetchedArguments.fromScreen,
                LendingKycEventKey.textDisplayed to args.creditReportNotFetchedArguments.description
            )
        )
        binding.ivQuestionMark.isGone = args.creditReportNotFetchedArguments.kycFeatureFlowType.isFromP2POrLending()
    }
    private fun openNeedHelp() {
        val sendTo = remoteConfigApi.getWhatsappNumber()
        val number = prefs.getUserPhoneNumber()
        val name = prefs.getUserName()
        val message = getString(
            com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_contact_support_manual_pan_s_s.resourceId,
            name,
            number
        )
        requireContext().openWhatsapp(sendTo, message)
    }

    private fun setupListener() {
        binding.toolbar.btnNeedHelp.setDebounceClickListener{
            openNeedHelp()
        }

        binding.btnPrimaryAction.setDebounceClickListener {
            when (args.creditReportNotFetchedArguments.primaryAction) {
                PanErrorScreenPrimaryButtonAction.GO_HOME -> {}
                PanErrorScreenPrimaryButtonAction.ENTER_PAN_MANUALLY -> {
                    val screenArgs = encodeUrl(
                        serializer.encodeToString(
                            ManualPanEntryScreenArguments(
                                if (args.creditReportNotFetchedArguments.jarVerifiedPAN)
                                    USER_HAS_DONE_PAN_VERIFICATION_ON_JAR_BEFORE
                                else
                                    USER_HAS_NOT_DONE_PAN_VERIFICATION_ON_JAR_BEFORE,
                                jarVerifiedPAN = args.creditReportNotFetchedArguments.jarVerifiedPAN.orFalse(),
                                kycFeatureFlowType = args.creditReportNotFetchedArguments.kycFeatureFlowType
                            )
                        )
                    )
                    navigateTo(
                        FeatureLendingKycStepsNavigationDirections.actionToEnterPanManuallyStep(
                            screenArgs
                        )
                    )
                }
                PanErrorScreenPrimaryButtonAction.USE_PAN_SAVED_WITH_JAR -> {
                    navigateTo(
                        FeatureLendingKycStepsNavigationDirections.actionToPanFromJarLoadingDialog(
                            args.creditReportNotFetchedArguments.kycFeatureFlowType.name
                        )
                    )
                }
                PanErrorScreenPrimaryButtonAction.RETAKE_PHOTO -> {}
                else -> {}
            }

        }
        binding.btnSecondaryAction.setDebounceClickListener {
            when (args.creditReportNotFetchedArguments.secondaryAction) {
                PanErrorScreenSecondaryButtonAction.CONTACT_SUPPORT -> {}
                PanErrorScreenSecondaryButtonAction.ENTER_PAN_MANUALLY -> {
                    val screenArgs = encodeUrl(
                        serializer.encodeToString(
                            ManualPanEntryScreenArguments(
                                if (args.creditReportNotFetchedArguments.jarVerifiedPAN)
                                    USER_HAS_DONE_PAN_VERIFICATION_ON_JAR_BEFORE
                                else
                                    USER_HAS_NOT_DONE_PAN_VERIFICATION_ON_JAR_BEFORE,
                                jarVerifiedPAN = args.creditReportNotFetchedArguments.jarVerifiedPAN.orFalse(),
                                kycFeatureFlowType = args.creditReportNotFetchedArguments.kycFeatureFlowType
                            )
                        )
                    )
                    navigateTo(
                        FeatureLendingKycStepsNavigationDirections.actionToEnterPanManuallyStep(
                            screenArgs
                        )
                    )
                }
                PanErrorScreenSecondaryButtonAction.NONE -> {}
                else -> {}
            }
        }
        binding.ivQuestionMark.setDebounceClickListener {
            navigateTo(
                FeatureLendingKycStepsNavigationDirections.actionToLendingKycFaqBottomSheet()
            )
        }
        binding.toolbar.btnBack.setDebounceClickListener {
            popBackStack()
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