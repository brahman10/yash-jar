package com.jar.app.feature_lending_kyc.impl.ui.pan.manual

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.jar.app.base.data.event.GoToHomeEvent
import com.jar.app.base.data.event.LendingBackPressEvent
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.decodeUrl
import com.jar.app.base.util.encodeUrl
import com.jar.app.base.util.openWhatsapp
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.orFalse
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.core_ui.widget.CustomEditTextWithErrorHandling
import com.jar.app.feature_kyc.shared.domain.model.ManualKycRequest
import com.jar.app.feature_lending_kyc.FeatureLendingKycStepsNavigationDirections
import com.jar.app.feature_lending_kyc.R
import com.jar.app.feature_lending_kyc.databinding.FeatureLendingKycFragmentEnterPanManuallyBinding
import com.jar.app.feature_lending_kyc.impl.data.Step
import com.jar.app.feature_lending_kyc.impl.domain.event.ToolbarStepsVisibilityEvent
import com.jar.app.feature_lending_kyc.shared.domain.model.PanErrorScreenPrimaryButtonAction
import com.jar.app.feature_lending_kyc.shared.domain.model.PanErrorScreenSecondaryButtonAction
import com.jar.app.feature_lending_kyc.impl.ui.loading.GenericLendingKycLoadingViewModel
import com.jar.app.feature_lending_kyc.impl.ui.loading.GenericLoadingArguments
import com.jar.app.feature_lending_kyc.impl.ui.pan.error_screens.PanErrorStatesArguments
import com.jar.app.feature_lending_kyc.shared.domain.arguments.CreditReportScreenArguments
import com.jar.app.feature_lending_kyc.impl.ui.steps.LendingKycNavigationGenerator
import com.jar.app.feature_lending_kyc.impl.ui.steps.LendingKycStepsViewModel
import com.jar.app.feature_lending_kyc.shared.util.LendingKycConstants
import com.jar.app.feature_lending_kyc.shared.util.LendingKycFlowType
import com.jar.app.feature_lending_kyc.impl.util.PanTextFormatter
import com.jar.app.core_base.data.dto.isFromLending
import com.jar.app.core_base.data.dto.isFromP2POrLending
import com.jar.app.feature_lending_kyc.shared.domain.model.CreditReportPAN
import com.jar.app.feature_lending_kyc.shared.domain.model.ManualPanEntryScreenArguments
import com.jar.app.feature_lending_kyc.shared.util.LendingKycEventKey
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
internal class EnterPanManuallyFragment :
    BaseFragment<FeatureLendingKycFragmentEnterPanManuallyBinding>() {

    @Inject
    lateinit var prefs: PrefsApi

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    @Inject
    lateinit var remoteConfigApi: RemoteConfigApi

    @Inject
    lateinit var serializer: Serializer

    private val viewModelProvider: EnterPanManuallyViewModelAndroid by viewModels()
    private val viewModel by lazy { viewModelProvider.getInstance() }
    private val loadingViewModel by navGraphViewModels<GenericLendingKycLoadingViewModel>(R.id.feature_lending_kyc_steps_navigation)

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureLendingKycFragmentEnterPanManuallyBinding
        get() = FeatureLendingKycFragmentEnterPanManuallyBinding::inflate

    private val stringArgs: EnterPanManuallyFragmentArgs by navArgs()

    private val args by lazy {
        serializer.decodeFromString<ManualPanEntryScreenArguments>(
            decodeUrl(stringArgs.screenArgs)
        )
    }

    private val lendingKycStepsViewModel: LendingKycStepsViewModel by activityViewModels()

    private var backPressCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                handleBackNavigation()
            }
        }

    companion object {
        private const val BACK_ARROW = "Back Arrow"
        private const val FROM_SCREEN = "EnterPanManuallyFragment"
        private const val FETCHING_DETAILS_FROM_NSDL_SCREEN = "Fetching Details From NSDL Screen"
    }

    private var creditReportPAN: CreditReportPAN? = null

    override fun setupAppBar() {
        EventBus.getDefault()
            .post(ToolbarStepsVisibilityEvent(shouldShowSteps = false, Step.PAN, true))
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
        setupListener()
        observeLiveData()
        registerBackPressDispatcher()
    }

    private fun setupUI() {
        binding.toolbar.tvTitle.text = getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_pan_details)
        binding.toolbar.separator.isVisible = true
        binding.customEditText.setEditTextEnumType(
            CustomEditTextWithErrorHandling.EditTextType.PAN, uiScope
        )
        binding.customEditText.setTextWatcher(
            PanTextFormatter(
                textColor = ContextCompat.getColor(
                    requireContext(),
                    com.jar.app.core_ui.R.color.white
                )
            )
        )
        analyticsHandler.postEvent(
            LendingKycEventKey.Shown_EnterPANNumberScreen,
            mapOf(LendingKycEventKey.fromScreen to args.fromScreen)
        )
        binding.captureGroup.isVisible = args.kycFeatureFlowType.isFromP2POrLending().not()
        if (args.kycFeatureFlowType.isFromLending())
            analyticsHandler.postEvent(
                LendingKycEventKey.Lending_PanManualEntryScreenLauched,
                mapOf(LendingKycEventKey.fromScreen to args.fromScreen)
            )
        binding.toolbar.tvTitle.setText(getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_pan_card_details))
    }

    private fun setupListener() {
        binding.toolbar.btnNeedHelp.setDebounceClickListener{
            openNeedHelp()
        }
        binding.toolbar.btnBack.setDebounceClickListener {
            handleBackNavigation()
        }

        binding.customEditText.setIsValidatedListener { isValidated, value ->
            binding.btnProceed.setDisabled(!isValidated)
            if (isValidated && args.kycFeatureFlowType.isFromLending()) {
                analyticsHandler.postEvent(
                    LendingKycEventKey.Lending_PanNumberManuallyEntered,
                    mapOf(
                        LendingKycEventKey.panNumber to binding.customEditText.getRawText()
                    )
                )
            }
        }

        binding.btnProceed.setDebounceClickListener {
            analyticsHandler.postEvent(
                LendingKycEventKey.Clicked_Button_EnterPANNumberScreen,
                mapOf(
                    LendingKycEventKey.optionChosen to binding.btnProceed.getText()
                )
            )
            if (args.kycFeatureFlowType.isFromLending())
                analyticsHandler.postEvent(
                    LendingKycEventKey.Lending_PanNumberProceedClicked,
                    mapOf(
                        LendingKycEventKey.panNumber to binding.customEditText.getRawText()
                    )
                )
            viewModel.manualEntryFetchPanDetails(
                ManualKycRequest(
                    binding.customEditText.getRawText(), null, null
                )
            )
        }

        binding.clTakePhoto.setDebounceClickListener {
            analyticsHandler.postEvent(
                LendingKycEventKey.Clicked_Button_EnterPANNumberScreen,
                mapOf(
                    LendingKycEventKey.optionChosen to binding.clTakePhoto.getText()
                )
            )
            navigateTo(
                FeatureLendingKycStepsNavigationDirections.actionToCapturePanPhotoFragment(
                    shouldInitiateCameraDirectly = false
                )
            )
        }
    }

    private fun handleBackNavigation() {
        if (args.isPanAadhaarMismatch) {
            lendingKycStepsViewModel.viewOnlyNavigationRedirectTo(
                LendingKycFlowType.PAN,
                false,
                WeakReference(requireActivity())
            )
            popBackStack()
        } else if (args.fromScreen == LendingKycNavigationGenerator.CONTINUE_KYC_BOTTOM_SHEET) {
            EventBus.getDefault().post(
                GoToHomeEvent(
                    FROM_SCREEN,
                    BaseConstants.HomeBottomNavigationScreen.PROFILE
                )
            )
            popBackStack()
        } else if (args.kycFeatureFlowType.isFromLending()) {
            analyticsHandler.postEvent(
                event = LendingKycEventKey.Lending_BackButtonClicked,
                values = mapOf(
                    LendingKycEventKey.screen_name to LendingKycEventKey.PAN_MANUAL_ENTRY_SCREEN
                )
            )
            EventBus.getDefault().post(
                LendingBackPressEvent(
                    screenName = LendingKycEventKey.PAN_MANUAL_ENTRY_SCREEN,
                    logAnalyticsEvent = false,
                    shouldNavigateBack = true,
                    popupId = R.id.enterPanManuallyFragment
                )
            )

        }
    }

    private fun observeLiveData() {
       viewLifecycleOwner.lifecycleScope.launch {
           repeatOnLifecycle(Lifecycle.State.STARTED){
               viewModel.manualKycRequestLiveData.collect(
                   onLoading = {
                       navigateTo(
                           FeatureLendingKycStepsNavigationDirections.actionToGenericLoadingDialog(
                               GenericLoadingArguments(
                                   title = getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_fetching_pan_details),
                                   description = getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_checking_for_pan_from_nsdl_db),
                                   assetsUrl = BaseConstants.CDN_BASE_URL + LendingKycConstants.LottieUrls.PAPER_STACK,
                                   illustrationResourceId = null,
                                   isIllustrationUrl = false
                               )
                           )
                       )
                   },
                   onSuccess = {
                       it?.panData?.let {
                           val name = it.name.split(" ")
                           val firstName = if (name.isNotEmpty()) name[0] else ""
                           val lastName = if (name.size > 1) name.filterIndexed { index, s -> index != 0 }
                               .joinToString(" ") else ""

                           creditReportPAN =
                               CreditReportPAN(it.panNumber, firstName, lastName, it.dob)

                           loadingViewModel.updateGenericLoadingTitle(getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_fetching_pan_details))
                           loadingViewModel.dismissGenericLoadingAfterMillis(2000L, true, FROM_SCREEN)
                       }
                   },
                   onError = { message, errorCode ->
                       loadingViewModel.dismissGenericLoadingAfterMillis(500L, false, FROM_SCREEN)
                       analyticsHandler.postEvent(
                           LendingKycEventKey.Shown_PANManualEntryErrorScreen, mapOf(
                               LendingKycEventKey.textDisplayed to message
                           )
                       )
                       when (errorCode) {
                           BaseConstants.ErrorCodesLendingKyc.PAN.INVALID_PAN_CARD -> {
                               navigateTo(
                                   FeatureLendingKycStepsNavigationDirections.actionToPanErrorStatesFragment(
                                       PanErrorStatesArguments(
                                           getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_uh_oh_exclamation),
                                           getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_invalid_pan_entered_please_try_again),
                                           BaseConstants.CDN_BASE_URL + LendingKycConstants.IllustrationUrls.INVALID_PAN_URL,
                                           primaryAction = if (args.jarVerifiedPAN.orFalse()) PanErrorScreenPrimaryButtonAction.USE_PAN_SAVED_WITH_JAR else PanErrorScreenPrimaryButtonAction.ENTER_PAN_AGAIN,
                                           secondaryAction = if (args.jarVerifiedPAN.orFalse()) PanErrorScreenSecondaryButtonAction.ENTER_PAN_AGAIN else PanErrorScreenSecondaryButtonAction.NONE,
                                           isLottie = false,
                                           kycFeatureFlowType = args.kycFeatureFlowType
                                       )
                                   )
                               )
                           }

                           BaseConstants.ErrorCodesLendingKyc.PAN.UNABLE_TO_EXTRACT_DATA_FROM_FILE -> {
                               navigateTo(
                                   FeatureLendingKycStepsNavigationDirections.actionToPanErrorStatesFragment(
                                       PanErrorStatesArguments(
                                           getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_no_pan_card_detected),
                                           getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_make_sure_the_photo_is_clear_and_visible),
                                           BaseConstants.CDN_BASE_URL + LendingKycConstants.IllustrationUrls.INVALID_PAN_URL,
                                           PanErrorScreenPrimaryButtonAction.RETAKE_PHOTO,
                                           PanErrorScreenSecondaryButtonAction.NONE,
                                           isLottie = false,
                                           kycFeatureFlowType = args.kycFeatureFlowType
                                       )
                                   )
                               )
                           }

                           BaseConstants.ErrorCodesLendingKyc.PAN.PAN_ENTRY_LIMIT_EXCEEDED -> {
                               navigateTo(
                                   FeatureLendingKycStepsNavigationDirections.actionToPanErrorStatesFragment(
                                       PanErrorStatesArguments(
                                           getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_pan_entry_attempt_limit_exceeded),
                                           getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_please_try_again_tomorrow),
                                           BaseConstants.CDN_BASE_URL + LendingKycConstants.LottieUrls.GENERIC_ERROR,
                                           PanErrorScreenPrimaryButtonAction.GO_HOME,
                                           PanErrorScreenSecondaryButtonAction.CONTACT_SUPPORT,
                                           contactMessage = getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_pan_entry_limit_exceeded),
                                           kycFeatureFlowType = args.kycFeatureFlowType
                                       )
                                   )
                               )
                           }
                           else->{
                               message.snackBar(binding.root)
                           }
                       }
                   }
               )
           }
       }

        loadingViewModel.genericLoadingOnAutoDismissViewModel.observe(viewLifecycleOwner) {
            if (it.isDismissingAfterSuccess && it.fromScreen == FROM_SCREEN) {
                // loading dialog dismissed go to next
                creditReportPAN?.let {
                    val args = encodeUrl(
                        serializer.encodeToString(
                            CreditReportScreenArguments(
                                it,
                                false,
                                LendingKycConstants.PanFlowType.MANUAL,
                                isBackNavOrViewOnlyFlow = false,
                                PanErrorScreenPrimaryButtonAction.YES_DETAILS_ARE_CORRECT,
                                PanErrorScreenSecondaryButtonAction.NO_ENTER_DETAILS_MANUALLY,
                                fromScreen = FETCHING_DETAILS_FROM_NSDL_SCREEN,
                                description = getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_following_pan_is_associated_with_your_credit_report),
                                isPanAadhaarMismatch = args.isPanAadhaarMismatch,
                                kycFeatureFlowType = args.kycFeatureFlowType
                            )
                        )
                    )
                    navigateTo(
                        FeatureLendingKycStepsNavigationDirections.actionToCreditReportFetchedStep(
                            args
                        )
                    )
                }
            }
        }
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

    override fun onResume() {
        val isValidated =
            binding.customEditText.getRawText().length == CustomEditTextWithErrorHandling.PAN_LENGTH
        binding.customEditText.setCharacterCount(
            binding.customEditText.getRawText().length,
            CustomEditTextWithErrorHandling.PAN_LENGTH
        )
        binding.btnProceed.setDisabled(!isValidated)
        super.onResume()
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