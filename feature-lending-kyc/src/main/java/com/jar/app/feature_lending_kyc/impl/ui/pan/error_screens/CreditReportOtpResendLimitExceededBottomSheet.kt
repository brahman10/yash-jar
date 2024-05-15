package com.jar.app.feature_lending_kyc.impl.ui.pan.error_screens

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.toSpannable
import androidx.core.view.isVisible
import androidx.navigation.fragment.navArgs
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.app.base.util.encodeUrl
import com.jar.app.base.util.openWhatsapp
import com.jar.app.core_base.data.dto.getKycFeatureFlowType
import com.jar.app.core_base.util.orFalse
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_lending_kyc.FeatureLendingKycStepsNavigationDirections
import com.jar.app.feature_lending_kyc.R
import com.jar.app.feature_lending_kyc.databinding.FeatureLendingKycBottomSheetCreditReportOtpResendLimitExceededBinding
import com.jar.app.feature_lending_kyc.impl.ui.pan.report_not_fetched.CreditReportNotAvailableFragment
import com.jar.app.feature_lending_kyc.shared.domain.model.ManualPanEntryScreenArguments
import com.jar.internal.library.jar_core_network.api.util.Serializer
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
internal class CreditReportOtpResendLimitExceededBottomSheet :
    BaseBottomSheetDialogFragment<FeatureLendingKycBottomSheetCreditReportOtpResendLimitExceededBinding>() {

    @Inject
    lateinit var remoteConfigApi: RemoteConfigApi

    @Inject
    lateinit var serializer: Serializer

    private val args: CreditReportOtpResendLimitExceededBottomSheetArgs by navArgs()

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureLendingKycBottomSheetCreditReportOtpResendLimitExceededBinding
        get() = FeatureLendingKycBottomSheetCreditReportOtpResendLimitExceededBinding::inflate
    override val bottomSheetConfig: BottomSheetConfig
        get() = DEFAULT_CONFIG

    override fun setup() {
        setupUI()
        setupListeners()
    }

    private fun setupUI() {
        binding.tvOtpAttemptLimitExceededTitle.text = args.title
        binding.tvLimitExceededDesc.text = args.desc
        binding.tvSecondaryAction.paintFlags =
            binding.tvSecondaryAction.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        if (args.isComeBackLaterFlow) {
            binding.btnAction.setText(getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_come_back_later).toSpannable())
            binding.tvSecondaryAction.isVisible = true
            binding.tvSecondaryAction.text =
                getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_contact_support)
        } else if (args.jarVerifiedPAN) {
            binding.btnAction.setText(getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_use_my_pan_saved_with_jar).toSpannable())
            binding.tvSecondaryAction.isVisible = true
            binding.tvSecondaryAction.text =
                getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_enter_pan_manually)
        } else {
            binding.btnAction.setText(getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_enter_pan_manually).toSpannable())
            binding.tvSecondaryAction.isVisible = false
        }
    }

    private fun setupListeners() {
        binding.btnAction.setDebounceClickListener {
            if (args.isComeBackLaterFlow) {
                popBackStack(R.id.lendingKycOnboardingFragment, true)
            } else if (args.jarVerifiedPAN) {
                navigateTo(
                    FeatureLendingKycStepsNavigationDirections.actionToPanFromJarLoadingDialog(),
                    true
                )
            } else {
                val screenArgs = encodeUrl(
                    serializer.encodeToString(
                        ManualPanEntryScreenArguments(
                            if (args.jarVerifiedPAN.orFalse())
                                CreditReportNotAvailableFragment.USER_HAS_DONE_PAN_VERIFICATION_ON_JAR_BEFORE
                            else
                                CreditReportNotAvailableFragment.USER_HAS_NOT_DONE_PAN_VERIFICATION_ON_JAR_BEFORE,
                            jarVerifiedPAN = args.jarVerifiedPAN.orFalse(),
                            kycFeatureFlowType = getKycFeatureFlowType(args.kycFeatureFlowType)
                        )
                    )
                )
                navigateTo(
                    FeatureLendingKycStepsNavigationDirections.actionToEnterPanManuallyStep(
                       screenArgs
                    )
                )
            }
        }

        binding.tvSecondaryAction.setDebounceClickListener {
            if (args.isComeBackLaterFlow) {
                val number = remoteConfigApi.getWhatsappNumber()
                val message = getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_otp_limit_exceeded_help)
                requireContext().openWhatsapp(number, message)
            } else if (args.jarVerifiedPAN) {
                val screenArgs = encodeUrl(
                    serializer.encodeToString(
                        ManualPanEntryScreenArguments(
                            if (args.jarVerifiedPAN.orFalse())
                                CreditReportNotAvailableFragment.USER_HAS_DONE_PAN_VERIFICATION_ON_JAR_BEFORE
                            else
                                CreditReportNotAvailableFragment.USER_HAS_NOT_DONE_PAN_VERIFICATION_ON_JAR_BEFORE,
                            jarVerifiedPAN = args.jarVerifiedPAN.orFalse()
                        )
                    ))
                navigateTo(
                    FeatureLendingKycStepsNavigationDirections.actionToEnterPanManuallyStep(
                        screenArgs
                    ),
                    true,
                    popUpTo = R.id.creditReportOtpResendLimitExceededBottomSheet,
                    inclusive = true
                )
            } else {
                popBackStack(R.id.creditReportOtpResendLimitExceededBottomSheet, true)
            }
        }

        binding.ivCross.setDebounceClickListener {
            dismiss()
        }
    }
}