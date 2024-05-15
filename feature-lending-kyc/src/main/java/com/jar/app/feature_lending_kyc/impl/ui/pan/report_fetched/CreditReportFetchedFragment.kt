package com.jar.app.feature_lending_kyc.impl.ui.pan.report_fetched

import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.text.toSpannable
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.jar.app.base.data.event.LendingBackPressEvent
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.decodeUrl
import com.jar.app.base.util.encodeUrl
import com.jar.app.base.util.getMaskedString
import com.jar.app.base.util.openWhatsapp
import com.jar.app.core_base.data.dto.isFromLending
import com.jar.app.core_base.data.dto.isFromP2POrLending
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.orFalse
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_lending_kyc.FeatureLendingKycStepsNavigationDirections
import com.jar.app.feature_lending_kyc.R
import com.jar.app.feature_lending_kyc.databinding.FeatureLendingKycFragmentCreditReportFetchedBinding
import com.jar.app.feature_lending_kyc.impl.data.Step
import com.jar.app.feature_lending_kyc.impl.domain.event.ToolbarStepsVisibilityEvent
import com.jar.app.feature_lending_kyc.impl.ui.aadhaar.manual_entry.AadhaarManualEntryFragmentDirections
import com.jar.app.feature_lending_kyc.impl.ui.loading.GenericLendingKycLoadingViewModel
import com.jar.app.feature_lending_kyc.impl.ui.loading.GenericLoadingArguments
import com.jar.app.feature_lending_kyc.impl.ui.pan.report_not_fetched.CreditReportNotAvailableFragment
import com.jar.app.feature_lending_kyc.impl.ui.steps.LendingKycStepsViewModel
import com.jar.app.feature_lending_kyc.shared.domain.arguments.CreditReportScreenArguments
import com.jar.app.feature_lending_kyc.shared.domain.model.AadhaarActionPromptArgs
import com.jar.app.feature_lending_kyc.shared.domain.model.AadhaarErrorScreenPrimaryButtonAction
import com.jar.app.feature_lending_kyc.shared.domain.model.AadhaarErrorScreenSecondaryButtonAction
import com.jar.app.feature_lending_kyc.shared.domain.model.CreditReportPAN
import com.jar.app.feature_lending_kyc.shared.domain.model.ManualPanEntryScreenArguments
import com.jar.app.feature_lending_kyc.shared.domain.model.PanErrorScreenPrimaryButtonAction
import com.jar.app.feature_lending_kyc.shared.domain.model.PanErrorScreenSecondaryButtonAction
import com.jar.app.feature_lending_kyc.shared.util.LendingKycConstants
import com.jar.app.feature_lending_kyc.shared.util.LendingKycEventKey
import com.jar.app.feature_lending_kyc.shared.util.LendingKycFlowType
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
internal class CreditReportFetchedFragment :
    BaseFragment<FeatureLendingKycFragmentCreditReportFetchedBinding>() {
    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureLendingKycFragmentCreditReportFetchedBinding
        get() = FeatureLendingKycFragmentCreditReportFetchedBinding::inflate

    @Inject
    lateinit var prefs: PrefsApi

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    @Inject
    lateinit var remoteConfigApi: RemoteConfigApi

    @Inject
    lateinit var serializer: Serializer

    private val args by navArgs<CreditReportFetchedFragmentArgs>()

    private val creditReportScreenArguments by lazy {
        serializer.decodeFromString<CreditReportScreenArguments>(
            decodeUrl(args.creditReportArguments)
        )
    }

    private val viewModelProvider: CreditReportFetchedViewModelAndroid by viewModels()
    private val viewModel by lazy { viewModelProvider.getInstance() }

    private val lendingKycStepsViewModel: LendingKycStepsViewModel by activityViewModels()
    private val progressViewModel by navGraphViewModels<GenericLendingKycLoadingViewModel>(R.id.feature_lending_kyc_steps_navigation)

    private var backPressCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onBackPress()
            }
        }

    companion object {
        const val CREDIT_REPORT_FETCHED_FRAGMENT = "CreditReportFetchedFragment"
        const val HELP_ICON = "Help Icon"
        const val CREDIT_REPORT_PAN_DETAILS_SCREEN = "Credit Report PAN Details Screen"
        const val PAN_OCR_DETAILS_SCREEN = "PAN OCR Details Screen"
        const val PAN_VERIFYING_DETAILS_SCREEN = "PAN Verifying Details Screen"
        const val NSDL_FETCHED_PAN_DETAILS_SCREEN = "NSDL Fetched PAN Details Screen"
        const val JAR_VERIFIED_PAN_SCREEN = "JAR Verified PAN Screen"
        const val PAN_AADHAAR_MISMATCH = "PAN Aadhaar Mismatch"
    }

    override fun setupAppBar() {
        EventBus.getDefault().post(ToolbarStepsVisibilityEvent(shouldShowSteps = true, Step.PAN))
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
        setupListener()
        observeFlow()
        registerBackPressDispatcher()
    }

    private fun setupUI() {
        if ( creditReportScreenArguments.kycFeatureFlowType.isFromLending()) {
            analyticsHandler.postEvent(
                event = LendingKycEventKey.Lending_PANOtpFlow,
                values = mapOf(
                    LendingKycEventKey.action to getFromScreen()
                )
            )
        }
        setupToolbar()
        binding.clPanDetailsEditContainer.isVisible =
            creditReportScreenArguments.isBackNavOrViewOnlyFlow.not()
        binding.clPanViewOnlyContainer.isVisible =
            creditReportScreenArguments.isBackNavOrViewOnlyFlow

        binding.tvDescription.text = creditReportScreenArguments.description

        binding.btnPrimaryAction.isVisible =
            creditReportScreenArguments.primaryAction.stringRes != null
        binding.btnSecondaryAction.isVisible =
            creditReportScreenArguments.secondaryAction.stringRes != null

        creditReportScreenArguments.primaryAction.stringRes?.let {
            binding.btnPrimaryAction.setText(getCustomString(it).toSpannable())
        }

        creditReportScreenArguments.secondaryAction.stringRes?.let {
            binding.btnSecondaryAction.text = getCustomString(it)
        }

        binding.tvVerifiedWithJar.isVisible =
            (creditReportScreenArguments.jarVerifiedPAN && creditReportScreenArguments.primaryAction == PanErrorScreenPrimaryButtonAction.YES_USE_THIS_PAN)

        creditReportScreenArguments.creditReportPan?.let {
            if (creditReportScreenArguments.isBackNavOrViewOnlyFlow)
                setViewOnlyPanDetails(it)
            else
                setPanDetails(it)
        }

        binding.btnSecondaryAction.paintFlags =
            binding.btnSecondaryAction.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        analyticsHandler.postEvent(
            LendingKycEventKey.Shown_ConfirmYourPANScreen, mapOf(
                LendingKycEventKey.fromScreen to creditReportScreenArguments.fromScreen,
                LendingKycEventKey.textDisplayed to creditReportScreenArguments.description
            )
        )
        binding.ivQuestionMark.isGone =  creditReportScreenArguments.kycFeatureFlowType.isFromP2POrLending()
        analyticsHandler.postEvent(
            LendingKycEventKey.Lending_PanCardFetched,
            mapOf(
                LendingKycEventKey.fromScreen to getFromScreen()
            )
        )
        binding.toolbar.tvTitle.setText(getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_pan_card_details))
    }

    private fun getFromScreen()= if (creditReportScreenArguments.jarVerifiedPAN) LendingKycEventKey.JAR_RECORDS else LendingKycEventKey.MANUAL_ENTRY
    private fun setupToolbar() {
        binding.toolbar.root.isVisible =  creditReportScreenArguments.kycFeatureFlowType.isFromP2POrLending()
        binding.toolbar.tvTitle.text = getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_loan_offer)
        binding.toolbar.separator.isVisible =   creditReportScreenArguments.kycFeatureFlowType.isFromP2POrLending()
    }

    private fun setPanDetails(creditReportPAN: CreditReportPAN) {
        binding.identityView.setDob(creditReportPAN.dob)
        binding.identityView.setIdentityHeading(getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_permanent_account_number))
        binding.identityView.setIdentity(creditReportPAN.panNumber)
        binding.identityView.setName(creditReportPAN.firstName + " " + creditReportPAN.lastName)
    }

    private fun setViewOnlyPanDetails(creditReportPAN: CreditReportPAN) {
        binding.identityViewOnlyState.setDob(creditReportPAN.dob)
        binding.identityViewOnlyState.setIdentityHeading(getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_permanent_account_number))
        binding.identityViewOnlyState.setIdentity(creditReportPAN.panNumber.getMaskedString(1,8)) // I********H
        binding.identityViewOnlyState.setName(creditReportPAN.firstName + " " + creditReportPAN.lastName)
    }

    private fun setupListener() {
        binding.toolbar.btnNeedHelp.setDebounceClickListener{
            openNeedHelp()
        }

        binding.btnPrimaryAction.setDebounceClickListener {
            creditReportScreenArguments.primaryAction.stringRes?.let {
                analyticsHandler.postEvent(
                    LendingKycEventKey.Clicked_Button_ConfirmYourPANScreen,
                    mapOf(
                        LendingKycEventKey.optionChosen to getCustomString(it),
                        LendingKycEventKey.textDisplayed to creditReportScreenArguments.description
                    )
                )
            }
            analyticsHandler.postEvent(LendingKycEventKey.Lending_FetchedPanCardConfirmed,
                mapOf(
                    LendingKycEventKey.fromScreen to getFromScreen()
                )
            )
            when (creditReportScreenArguments.primaryAction) {
                PanErrorScreenPrimaryButtonAction.ENTER_PAN_MANUALLY -> {
                    navigateToManualPanEntry()
                }
                PanErrorScreenPrimaryButtonAction.USE_PAN_SAVED_WITH_JAR -> {
                    navigateTo(
                        FeatureLendingKycStepsNavigationDirections.actionToPanFromJarLoadingDialog(
                            creditReportScreenArguments.kycFeatureFlowType.name
                        )
                    )
                }
                //Direct lending Flow
                PanErrorScreenPrimaryButtonAction.YES_THIS_IS_MY_PAN -> {
                    creditReportScreenArguments.creditReportPan?.let {
                        viewModel.savePanDetails(it, creditReportScreenArguments.kycFeatureFlowType)
                    }
                }
                //Jar Verified Flow
                PanErrorScreenPrimaryButtonAction.YES_USE_THIS_PAN -> {
                    creditReportScreenArguments.creditReportPan?.let {
                        viewModel.savePanDetails(it, creditReportScreenArguments.kycFeatureFlowType)
                    }
                }
                PanErrorScreenPrimaryButtonAction.YES_DETAILS_ARE_CORRECT -> {
                    creditReportScreenArguments.creditReportPan?.let {
                        //Manual Flow
                        if (creditReportScreenArguments.panFlowType == LendingKycConstants.PanFlowType.MANUAL)
                            viewModel.savePanDetails(it, creditReportScreenArguments.kycFeatureFlowType)
                        else
                        //Image Flow
                            navigateTo(
                                FeatureLendingKycStepsNavigationDirections.actionToVerifyPanDetailsDialog(
                                    creditReportPan = it,
                                    jarVerifiedPAN = creditReportScreenArguments.jarVerifiedPAN,
                                    fromScreen = PAN_OCR_DETAILS_SCREEN,
                                    kycFeatureFlowType = creditReportScreenArguments.kycFeatureFlowType.name
                                )
                            )
                    }
                }
                else -> {}
            }
        }
        binding.btnSecondaryAction.setDebounceClickListener {
            when (creditReportScreenArguments.secondaryAction) {
                PanErrorScreenSecondaryButtonAction.ENTER_PAN_MANUALLY -> {
                    navigateToManualPanEntry()
                }
                PanErrorScreenSecondaryButtonAction.NO_THIS_IS_NOT_MY_PAN -> {
                    analyticsHandler.postEvent(LendingKycEventKey.Lending_FetchedPanCardDenied)
                    if (creditReportScreenArguments.jarVerifiedPAN)
                        navigateTo(
                            FeatureLendingKycStepsNavigationDirections.actionToPanFromJarLoadingDialog(
                                creditReportScreenArguments.kycFeatureFlowType.name
                            )
                        )
                    else
                        navigateToManualPanEntry()
                }
                PanErrorScreenSecondaryButtonAction.NO_ENTER_DETAILS_MANUALLY -> {
                    navigateToManualPanEntry()
                }
                else -> {}
            }
        }
        binding.btnNext.setDebounceClickListener {
            lendingKycStepsViewModel.viewOnlyNavigationRedirectTo(
                LendingKycFlowType.PAN, true, WeakReference(requireActivity())
            )
        }
        binding.ivQuestionMark.setDebounceClickListener {
            analyticsHandler.postEvent(
                LendingKycEventKey.Clicked_Button_ConfirmYourPANScreen,
                mapOf(
                    LendingKycEventKey.optionChosen to HELP_ICON,
                    LendingKycEventKey.textDisplayed to creditReportScreenArguments.description
                )
            )
            navigateTo(
                FeatureLendingKycStepsNavigationDirections.actionToLendingKycFaqBottomSheet()
            )
        }
        binding.toolbar.btnBack.setDebounceClickListener {
            onBackPress()
        }
    }

    private fun onBackPress() {
        if (creditReportScreenArguments.kycFeatureFlowType.isFromP2POrLending()) {
            EventBus.getDefault()
                .post(LendingBackPressEvent(
                    LendingKycEventKey.PAN_CARD_FETCHED_SCREEN,
                    true,
                R.id.creditReportFetchedFragment)
                )
        } else {
            lendingKycStepsViewModel.viewOnlyNavigationRedirectTo(
                LendingKycFlowType.PAN,
                false,
                WeakReference(requireActivity())
            )
        }
    }
    private fun navigateToManualPanEntry() {
        sendCtaClickEvent(LendingKycEventKey.manualEntry)
        val screenArgs = encodeUrl(serializer.encodeToString(
            ManualPanEntryScreenArguments(
                if (creditReportScreenArguments.jarVerifiedPAN)
                    CreditReportNotAvailableFragment.USER_HAS_DONE_PAN_VERIFICATION_ON_JAR_BEFORE
                else
                    CreditReportNotAvailableFragment.USER_HAS_NOT_DONE_PAN_VERIFICATION_ON_JAR_BEFORE,
                jarVerifiedPAN = creditReportScreenArguments.jarVerifiedPAN.orFalse(),
                kycFeatureFlowType = creditReportScreenArguments.kycFeatureFlowType
            )
        ))
        navigateTo(
            FeatureLendingKycStepsNavigationDirections.actionToEnterPanManuallyStep(
                screenArgs
            )
        )
    }

    private fun sendCtaClickEvent(source:String){
        analyticsHandler.postEvent(
            LendingKycEventKey.Lending_FetchedPanCardDenied,
            mapOf(LendingKycEventKey.source to source)
            )
    }
    private fun observeFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.savePanDetailsFlow.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onError = { errorMessage, _ ->
                        dismissProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        redirectAfterSavePan()
                    },
                    onSuccessWithNullData = {
                        dismissProgressBar()
                       redirectAfterSavePan()
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.aadhaarPanLinkageFlow.collect(
                    onLoading = {
                        navigateTo(
                            AadhaarManualEntryFragmentDirections.actionToGenericLoadingDialog(
                                GenericLoadingArguments(
                                    getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_verifying_details),
                                    getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_empty),
                                    null,
                                    R.drawable.feature_lending_kyc_ic_otp_message
                                )
                            )
                        )
                        progressViewModel.updateGenericLoadingTitle(getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_verifying_details))
                        progressViewModel.updateAssetUrl(
                            BaseConstants.CDN_BASE_URL + LendingKycConstants.LottieUrls.VERIFYING,
                            false
                        )
                    },
                    onSuccess = {
                        progressViewModel.dismissGenericLoadingAfterMillis(
                            1000L, true, CREDIT_REPORT_FETCHED_FRAGMENT
                        )
                    },
                    onSuccessWithNullData = {
                        progressViewModel.dismissGenericLoadingAfterMillis(
                            1000L, true, CREDIT_REPORT_FETCHED_FRAGMENT
                        )
                    },
                    onError = { _, errorCode ->
                        progressViewModel.dismissGenericLoadingAfterMillis(
                            100L, false, CREDIT_REPORT_FETCHED_FRAGMENT
                        )
                        when (errorCode) {
                            BaseConstants.ErrorCodesLendingKyc.Aadhaar.AADHAAR_PAN_MISMATCH -> {
                                navigateTo(
                                    AadhaarManualEntryFragmentDirections.actionToAadhaarActionPromptFragment(
                                        AadhaarActionPromptArgs(
                                            BaseConstants.CDN_BASE_URL + LendingKycConstants.LottieUrls.GENERIC_ERROR,
                                            getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_uh_oh_exclamation),
                                            getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_your_pan_aadhaar_details_dont_match),
                                            getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_re_enter_aadhaar_details),
                                            getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_re_enter_pan_details),
                                            AadhaarErrorScreenPrimaryButtonAction.EDIT_AADHAAR,
                                            AadhaarErrorScreenSecondaryButtonAction.EDIT_PAN,
                                            contactMessage = getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_my_pan_and_aadhaar_details_are_not_matching)
                                        )
                                    ),
                                    popUpTo = R.id.creditReportFetchedFragment,
                                    inclusive = true
                                )
                            }

                            BaseConstants.ErrorCodesLendingKyc.Aadhaar.AADHAAR_PAN_MISMATCH_SUPPORT -> {
                                navigateTo(
                                    AadhaarManualEntryFragmentDirections.actionToAadhaarActionPromptFragment(
                                        AadhaarActionPromptArgs(
                                            BaseConstants.CDN_BASE_URL + LendingKycConstants.LottieUrls.GENERIC_ERROR,
                                            getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_uh_oh_exclamation),
                                            getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_your_pan_aadhaar_details_dont_match_try_contacting_support),
                                            getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_back_to_home),
                                            getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_contact_support),
                                            AadhaarErrorScreenPrimaryButtonAction.GO_HOME,
                                            AadhaarErrorScreenSecondaryButtonAction.CONTACT_SUPPORT,
                                            contactMessage = getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_my_pan_and_aadhaar_details_are_not_matching)
                                        )
                                    ),
                                    popUpTo = R.id.creditReportFetchedFragment,
                                    inclusive = true
                                )
                            }

                            BaseConstants.ErrorCodesLendingKyc.COMPLETE_PRE_REQUISITE_FOR_AADHAAR_AND_PAN_LINKAGE -> {
                                navigateTo(
                                    AadhaarManualEntryFragmentDirections.actionToAadhaarActionPromptFragment(
                                        AadhaarActionPromptArgs(
                                            BaseConstants.CDN_BASE_URL + LendingKycConstants.LottieUrls.GENERIC_ERROR,
                                            getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_uh_oh_exclamation),
                                            getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_your_pan_aadhaar_details_dont_match_try_contacting_support),
                                            getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_back_to_home),
                                            getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_contact_support),
                                            AadhaarErrorScreenPrimaryButtonAction.GO_HOME,
                                            AadhaarErrorScreenSecondaryButtonAction.CONTACT_SUPPORT,
                                            contactMessage = getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_my_pan_and_aadhaar_details_are_not_matching)
                                        )
                                    ),
                                    popUpTo = R.id.creditReportFetchedFragment,
                                    inclusive = true
                                )
                            }
                        }
                    }
                )
            }
        }
        progressViewModel.genericLoadingOnAutoDismissViewModel.observe(viewLifecycleOwner) {
            if (it.isDismissingAfterSuccess && it.fromScreen == CREDIT_REPORT_FETCHED_FRAGMENT) {
                redirectToSuccessStepDialog(PAN_AADHAAR_MISMATCH, LendingKycFlowType.AADHAAR)
            }
        }
    }

    private fun redirectAfterSavePan() {
        val fromScreen =
            if (creditReportScreenArguments.primaryAction == PanErrorScreenPrimaryButtonAction.YES_USE_THIS_PAN)
                JAR_VERIFIED_PAN_SCREEN
            else if (creditReportScreenArguments.primaryAction == PanErrorScreenPrimaryButtonAction.YES_DETAILS_ARE_CORRECT && creditReportScreenArguments.panFlowType == LendingKycConstants.PanFlowType.MANUAL)
                PAN_VERIFYING_DETAILS_SCREEN
            else
                NSDL_FETCHED_PAN_DETAILS_SCREEN
        if (creditReportScreenArguments.isPanAadhaarMismatch)
            viewModel.verifyAadhaarPanLinkage(creditReportScreenArguments.kycFeatureFlowType)
        else
            redirectToSuccessStepDialog(fromScreen, LendingKycFlowType.PAN)
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

    private fun redirectToSuccessStepDialog(
        fromScreen: String,
        lendingKycFlowType: LendingKycFlowType
    ) {
        navigateTo(
            FeatureLendingKycStepsNavigationDirections.actionToSuccessStepDialog(
                flowType = lendingKycFlowType,
                fromScreen = fromScreen,
                lenderName = creditReportScreenArguments.lenderName,
                kycFeatureFlowType = creditReportScreenArguments.kycFeatureFlowType.name
            ),
            shouldAnimate = true
        )
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