package com.jar.app.feature_lending_kyc.impl.ui.email.email_delivery

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.jar.app.base.ui.fragment.BaseDialogFragment
import com.jar.app.base.util.encodeUrl
import com.jar.app.core_base.data.dto.KycFeatureFlowType
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.orZero
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_lending_kyc.FeatureLendingKycStepsNavigationDirections
import com.jar.app.feature_lending_kyc.R
import com.jar.app.feature_lending_kyc.databinding.FeatureLendingKycDialogEmailDeliveryStatusBinding
import com.jar.app.feature_lending_kyc.impl.ui.otp.OtpSheetArguments
import com.jar.app.feature_lending_kyc.shared.util.LendingKycConstants
import com.jar.app.feature_lending_kyc.shared.util.LendingKycFlowType
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.internal.library.jar_core_network.api.util.collect
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
internal class EmailDeliveryStatusDialog :
    BaseDialogFragment<FeatureLendingKycDialogEmailDeliveryStatusBinding>() {
    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureLendingKycDialogEmailDeliveryStatusBinding
        get() = FeatureLendingKycDialogEmailDeliveryStatusBinding::inflate
    override val dialogConfig: DialogFragmentConfig
        get() = DEFAULT_CONFIG

    @Inject
    lateinit var serializer: Serializer

    private val viewModelProvider: EmailDeliveryStatusViewModelAndroid by viewModels()
    private val viewModel by lazy { viewModelProvider.getInstance() }


    private val args: EmailDeliveryStatusDialogArgs by navArgs()

    override fun setup() {
        setupUI()
        setupListener()
        observeFlow()
    }

    private fun setupUI() {
        Glide.with(this)
            .load(BaseConstants.CDN_BASE_URL + LendingKycConstants.IllustrationUrls.EMAIL_NOT_SENT_URL)
            .into(binding.ivIllustration)
        binding.btnEditEmail.paint.isUnderlineText = true
    }

    private fun setupListener() {
        binding.btnEditEmail.setDebounceClickListener {
            dismiss()
        }
        binding.btnResendOtp.setDebounceClickListener {
            viewModel.requestEmailOtp(args.email, KycFeatureFlowType.UNKNOWN)
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
                            it?.validityInSeconds.orZero(), it?.resentOTPInSeconds.orZero()
                        )
                    },
                    onError = { message, errorCode ->
                        dismissProgressBar()
                        if (errorCode == BaseConstants.ErrorCodesLendingKyc.Email.OTP_ATTEMPT_LIMIT_EXCEEDED)
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

    private fun redirectToOtpVerification(expiresInTime: Long, resendTime: Long) {
        val args = encodeUrl(
            serializer.encodeToString(
                OtpSheetArguments(
                    flowType = LendingKycConstants.LendingKycOtpVerificationFlowType.EMAIL,
                    expiresInTime = expiresInTime,
                    resendTime = resendTime,
                    email = args.email,
                    fromScreen = LendingKycConstants.LendingKycOtpVerificationFlowType.EMAIL.name
                )
            )
        )
        navigateTo(
            FeatureLendingKycStepsNavigationDirections.actionToOtpVerificationFragment(args),
            true,
            popUpTo = R.id.emailDeliveryStatusDialog,
            inclusive = true
        )
    }
}