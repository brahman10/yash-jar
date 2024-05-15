package com.jar.app.feature_lending_kyc.impl.ui.aadhaar.confirmation

import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.toSpannable
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.core_base.data.dto.getKycFeatureFlowType
import com.jar.app.core_base.data.dto.isFromLending
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.feature_lending_kyc.FeatureLendingKycStepsNavigationDirections
import com.jar.app.feature_lending_kyc.databinding.FeatureLendingKycFragmentAadhaarConfirmationBinding
import com.jar.app.feature_lending_kyc.impl.data.Step
import com.jar.app.feature_lending_kyc.impl.domain.event.ToolbarStepsVisibilityEvent
import com.jar.app.feature_lending_kyc.impl.ui.aadhaar.captcha_bottomsheet.AadhaarCaptchaBottomSheet
import com.jar.app.feature_lending_kyc.impl.ui.steps.LendingKycStepsViewModel
import com.jar.app.feature_lending_kyc.shared.util.LendingKycEventKey
import com.jar.app.feature_lending_kyc.shared.util.LendingKycFlowType
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@AndroidEntryPoint
internal class AadhaarConfirmationFragment :
    BaseFragment<FeatureLendingKycFragmentAadhaarConfirmationBinding>() {

    private val args by navArgs<AadhaarConfirmationFragmentArgs>()

    private val viewModelProvider by viewModels<AadhaarConfirmationViewModelAndroid> { defaultViewModelProviderFactory }
    private val viewModel by lazy { viewModelProvider.getInstance() }


    private val lendingKycStepsViewModel: LendingKycStepsViewModel by activityViewModels()

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    companion object {
        const val FLOW_CKYC = 1
        const val FLOW_AADHAAR_UPLOAD = 2
        private const val HELP_ICON = "Help Icon"
        private const val YES_THIS_IS_MY_AADHAAR = "Yes, this is my Aadhaar"
        private const val NO_THIS_IS_NOT_MY_AADHAAR = "No, this is not my Aadhaar"
        private const val NO_RETAKE_PHOTO = "No, retake photo"
        private const val CKYC_AADHAAR_DETAILS_SCREEN = "CKYC Aadhaar Details Screen"
        private const val CKYC_RECORD_FOUND_SCREEN = "CKYC Record Found Screen"
        private const val AADHAAR_OCR_EXTRACTED_DETAILS_SCREEN =
            "Aadhaar OCR Extracted Details Screen"
    }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureLendingKycFragmentAadhaarConfirmationBinding
        get() = FeatureLendingKycFragmentAadhaarConfirmationBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(
            UpdateAppBarEvent(
                AppBarData(ToolbarNone)
            )
        )
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUi()
        setClickListener()
        observeFlow()
    }

    private fun observeFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.saveAadhaarDetailFlow.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        navigateToSuccessScreen()
                    },
                    onSuccessWithNullData = {
                        dismissProgressBar()
                        navigateToSuccessScreen()
                    },
                    onError = { errorMessage, _ ->
                        dismissProgressBar()
                        errorMessage.snackBar(binding.root)
                    }
                )
            }
        }
        lendingKycStepsViewModel.toolbarInteractionLiveData.observe(viewLifecycleOwner) {
            sendEventForClickButton(it)
        }
    }

    private fun navigateToSuccessScreen() {
        val fromScreen = when (args.flowType) {
            FLOW_CKYC -> {
                CKYC_AADHAAR_DETAILS_SCREEN
            }
            FLOW_AADHAAR_UPLOAD -> {
                AADHAAR_OCR_EXTRACTED_DETAILS_SCREEN
            }
            else -> {
                ""
            }
        }
        analyticsHandler.postEvent(
            if (  getKycFeatureFlowType(args.kycFeatureFlowType).isFromLending()) LendingKycEventKey.Lending_AadharManualVerificationSuccessful
             else LendingKycEventKey.Shown_AadhaarVerificationSuccessfulScreen,
            mapOf(LendingKycEventKey.fromScreen to fromScreen)
        )
        navigateTo(
            AadhaarConfirmationFragmentDirections.actionToSuccessStepDialog(
                flowType = LendingKycFlowType.AADHAAR,
                fromScreen= fromScreen,
                lenderName= args.lenderName,
                kycFeatureFlowType = args.kycFeatureFlowType
            )
        )
    }

    private fun setClickListener() {
        binding.ivHelp.setDebounceClickListener {
            navigateTo(
                FeatureLendingKycStepsNavigationDirections.actionToLendingKycFaqBottomSheet()
            )
            sendEventForClickButton(HELP_ICON)
        }

        binding.btnConfirmMyAadhaar.setDebounceClickListener {
            sendEventForClickButton(YES_THIS_IS_MY_AADHAAR)
            when (args.flowType) {
                FLOW_CKYC -> {  //open Aadhaar verification successful
                    viewModel.saveAadhaarDetail(getKycFeatureFlowType(args.kycFeatureFlowType))
                }
                FLOW_AADHAAR_UPLOAD -> {  //open Aadhaar captcha enter bottom sheet
                    if (args.aadhaarDetail.aadhaarNumber != null && args.aadhaarDetail.name != null) {
                        args.aadhaarDetail.aadhaarNumber?.let {
                            navigateTo(
                                AadhaarConfirmationFragmentDirections.actionToAadhaarEnterCaptchaBottomSheet(
                                    it,
                                    AadhaarCaptchaBottomSheet.AADHAAR_OCR_EXTRACTED_DETAILS,
                                    args.lenderName,
                                    args.kycFeatureFlowType
                                )
                            )
                        } ?: run {
                            getString(com.jar.app.core_ui.R.string.something_went_wrong).snackBar(binding.root)
                        }
                    } else {
                        getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_make_sure_aadhaar_clear_and_visible).snackBar(
                            binding.root
                        )
                    }
                }
            }
        }

        binding.btnNotMyAadhaar.setDebounceClickListener {
            //Since Aadhaar number not belong to user start manual entry flow.
            when (args.flowType) {
                FLOW_CKYC -> {
                    sendEventForClickButton(NO_THIS_IS_NOT_MY_AADHAAR)
                    navigateTo(
                        AadhaarConfirmationFragmentDirections.actionToAadhaarManualEntryConsentPromptFragment(
                            CKYC_AADHAAR_DETAILS_SCREEN
                        ),
                        true
                    )
                }
                FLOW_AADHAAR_UPLOAD -> {
                    sendEventForClickButton(NO_RETAKE_PHOTO)
                    popBackStack()
                }
            }
        }
    }

    private fun sendEventForClickButton(optionChosen: String) {
        analyticsHandler.postEvent(
            LendingKycEventKey.Clicked_Button_AadhaarDetailsScreen,
            mapOf(
                LendingKycEventKey.optionChosen to optionChosen,
                LendingKycEventKey.isFromLendingFlow to getKycFeatureFlowType(args.kycFeatureFlowType).isFromLending()
            )
        )
    }

    private fun setupUi() {
        EventBus.getDefault().post(
            ToolbarStepsVisibilityEvent(
                shouldShowSteps = true,
                Step.AADHAAR
            )
        )
        binding.icvAadhaar.setIdentityHeading(getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_aadhaar_number))
        binding.icvAadhaar.setIdentity(args.aadhaarDetail.maskAadhaarNumber())
        binding.icvAadhaar.setName(args.aadhaarDetail.name.orEmpty())
        binding.icvAadhaar.setDob(args.aadhaarDetail.dob.orEmpty())
        when (args.flowType) {
            FLOW_CKYC -> {
                binding.btnConfirmMyAadhaar.setText(getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_yes_this_is_my_aadhaar).toSpannable())
                binding.btnNotMyAadhaar.text =
                    getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_no_this_is_not_my_aadhaar)
            }
            FLOW_AADHAAR_UPLOAD -> {
                binding.btnConfirmMyAadhaar.setText(getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_yes_details_are_correct).toSpannable())
                binding.btnNotMyAadhaar.text =
                    getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_no_retake_photo)
            }
        }
        analyticsHandler.postEvent(
            LendingKycEventKey.Shown_AadhaarDetailsScreen,
            mapOf(
                LendingKycEventKey.fromScreen to getFromScreen(),
                LendingKycEventKey.isFromLendingFlow to getKycFeatureFlowType(args.kycFeatureFlowType).isFromLending()
            )
        )
        binding.btnNotMyAadhaar.paintFlags =
            binding.btnNotMyAadhaar.paintFlags or Paint.UNDERLINE_TEXT_FLAG
    }


    private fun getFromScreen(): String {
        return if (args.flowType == FLOW_CKYC) CKYC_RECORD_FOUND_SCREEN
        else AADHAAR_OCR_EXTRACTED_DETAILS_SCREEN
    }
}