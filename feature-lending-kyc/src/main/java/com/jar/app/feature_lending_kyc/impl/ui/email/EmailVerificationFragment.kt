package com.jar.app.feature_lending_kyc.impl.ui.email

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.encodeUrl
import com.jar.app.core_base.data.dto.KycFeatureFlowType
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.orZero
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.widget.CustomEditTextWithErrorHandling
import com.jar.app.feature_lending_kyc.FeatureLendingKycStepsNavigationDirections
import com.jar.app.feature_lending_kyc.databinding.FeatureLendingKycFragmentEmailVerificationBinding
import com.jar.app.feature_lending_kyc.impl.data.Step
import com.jar.app.feature_lending_kyc.impl.domain.event.ToolbarStepsVisibilityEvent
import com.jar.app.feature_lending_kyc.impl.ui.otp.OtpSheetArguments
import com.jar.app.feature_lending_kyc.impl.ui.steps.LendingKycStepsViewModel
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
internal class EmailVerificationFragment :
    BaseFragment<FeatureLendingKycFragmentEmailVerificationBinding>() {
    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureLendingKycFragmentEmailVerificationBinding
        get() = FeatureLendingKycFragmentEmailVerificationBinding::inflate

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    @Inject
    lateinit var serializer: Serializer

    private val args: EmailVerificationFragmentArgs by navArgs()

    private val viewModelProvider: EmailVerificationViewModelAndroid by viewModels()
    private val viewModel by lazy { viewModelProvider.getInstance() }


    private val lendingKycStepsViewModel: LendingKycStepsViewModel by activityViewModels()

    companion object {
        const val VERIFY = "Verify"
        const val CLEAR_TEXT_CROSS_ICON = "Clear text cross icon"
        const val HELP_ICON = "Help icon"
    }

    private var backPressCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                lendingKycStepsViewModel.viewOnlyNavigationRedirectTo(
                    LendingKycFlowType.EMAIL,
                    false,
                    WeakReference(requireActivity())
                )
            }
        }

    override fun setupAppBar() {
        EventBus.getDefault().post(ToolbarStepsVisibilityEvent(shouldShowSteps = true, Step.EMAIL))
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
        setupListeners()
        observeFlow()
        registerBackPressDispatcher()
    }

    private fun setupUI() {
        analyticsHandler.postEvent(LendingKycEventKey.Shown_EmailVerificationScreen)
        binding.clEmailEditContainer.isVisible = args.isBackNavOrViewOnlyFlow.not()
        binding.clEmailViewOnlyContainer.isVisible = args.isBackNavOrViewOnlyFlow
        if (args.isBackNavOrViewOnlyFlow) {
            args.email?.let { binding.tvEmail.text = it }
        } else {
            binding.customEditText.setEditTextEnumType(
                CustomEditTextWithErrorHandling.EditTextType.EMAIL, uiScope
            )
            binding.btnVerify.isEnabled = false
            args.email?.let {
                binding.customEditText.setEditTextValue(it)
                binding.btnVerify.isEnabled = true
            }
            binding.customEditText.requestFocus()
        }
    }

    private fun setupListeners() {
        binding.customEditText.setIsValidatedListener { isValidated, value ->
            binding.btnVerify.isEnabled = isValidated
        }

        binding.customEditText.setOnClearTextClickedListener {
            analyticsHandler.postEvent(
                LendingKycEventKey.Clicked_Button_EmailVerificationScreen,
                mapOf(
                    LendingKycEventKey.optionChosen to CLEAR_TEXT_CROSS_ICON
                )
            )
        }

        binding.btnVerify.setDebounceClickListener {
            analyticsHandler.postEvent(
                LendingKycEventKey.Clicked_Button_EmailVerificationScreen,
                mapOf(
                    LendingKycEventKey.optionChosen to VERIFY
                )
            )
            viewModel.requestEmailOtp(
                binding.customEditText.getEditTextValue(),
                KycFeatureFlowType.UNKNOWN
            )
        }

        binding.btnNext.setDebounceClickListener {
            lendingKycStepsViewModel.viewOnlyNavigationRedirectTo(
                LendingKycFlowType.EMAIL,
                true,
                WeakReference(requireActivity())
            )
        }

        binding.ivQuestionMark.setDebounceClickListener {
            analyticsHandler.postEvent(
                LendingKycEventKey.Clicked_Button_EmailVerificationScreen,
                mapOf(
                    LendingKycEventKey.optionChosen to HELP_ICON
                )
            )
            navigateTo(
                FeatureLendingKycStepsNavigationDirections.actionToLendingKycFaqBottomSheet()
            )
        }
    }

    private fun observeFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.requestEmailOtpFlow.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        redirectToOtpVerification(
                            it?.validityInSeconds.orZero(),
                            it?.resentOTPInSeconds.orZero(),
                            it?.messageId
                        )
                    },
                    onError = { message, errorCode ->
                        dismissProgressBar()
                        if (errorCode == BaseConstants.ErrorCodesLendingKyc.Email.EMAIL_DOES_NOT_EXIST) {
                            analyticsHandler.postEvent(
                                LendingKycEventKey.Shown_EmailAddressEdgeCases,
                                mapOf(
                                    LendingKycEventKey.errorMessage to message
                                )
                            )
                            binding.customEditText.showError(message)
                        } else if (errorCode == BaseConstants.ErrorCodesLendingKyc.Email.OTP_ATTEMPT_LIMIT_EXCEEDED)
                            navigateTo(
                                FeatureLendingKycStepsNavigationDirections.actionToOtpLimitExceededBottomSheet(
                                    LendingKycFlowType.EMAIL
                                )
                            )
                    }
                )
            }
        }
    }

    private fun redirectToOtpVerification(
        expiresInTime: Long,
        resendTime: Long,
        messageId: String?
    ) {
        val email = binding.customEditText.getEditTextValue().ifEmpty { args.email }
        val args = encodeUrl(
            serializer.encodeToString(
                OtpSheetArguments(
                    flowType = LendingKycConstants.LendingKycOtpVerificationFlowType.EMAIL,
                    expiresInTime = expiresInTime,
                    resendTime = resendTime,
                    email = email,
                    emailMessageId = messageId,
                    fromScreen = LendingKycConstants.LendingKycOtpVerificationFlowType.EMAIL.name
                )
            )
        )
        navigateTo(
            FeatureLendingKycStepsNavigationDirections.actionToOtpVerificationFragment(args),
            true
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