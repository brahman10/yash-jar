package com.jar.app.feature_lending_kyc.impl.ui.otp.error_screens

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.jar.app.base.data.event.GoToHomeEvent
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.app.base.util.openWhatsapp
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_lending_kyc.R
import com.jar.app.feature_lending_kyc.databinding.FeatureLendingKycBottomSheetOtpLimitExceededBinding
import com.jar.app.feature_lending_kyc.shared.util.LendingKycEventKey
import com.jar.app.feature_lending_kyc.shared.util.LendingKycFlowType
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@AndroidEntryPoint
internal class OtpLimitExceededBottomSheet :
    BaseBottomSheetDialogFragment<FeatureLendingKycBottomSheetOtpLimitExceededBinding>() {

    private val args: OtpLimitExceededBottomSheetArgs by navArgs()

    @Inject
    lateinit var remoteConfigApi: RemoteConfigApi

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    @Inject
    lateinit var prefs: PrefsApi

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureLendingKycBottomSheetOtpLimitExceededBinding
        get() = FeatureLendingKycBottomSheetOtpLimitExceededBinding::inflate
    override val bottomSheetConfig: BottomSheetConfig
        get() = DEFAULT_CONFIG

    override fun setup() {
        binding.btnContactSupport.setDebounceClickListener {
            val number = remoteConfigApi.getWhatsappNumber()
            val message = when (args.flowType) {
                LendingKycFlowType.EMAIL -> {
                    analyticsHandler.postEvent(LendingKycEventKey.Shown_EmailOTPAttemptLimitExceededBottomSheet)
                    getCustomStringFormatted(
                        com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_email_otp_limit_exceeded_s_s,
                        prefs.getUserName().orEmpty(),
                        prefs.getUserPhoneNumber().orEmpty()
                    )
                }
                LendingKycFlowType.PAN -> getCustomStringFormatted(
                    com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_credit_report_otp_limit_exceeded_s_s,
                    prefs.getUserName().orEmpty(),
                    prefs.getUserPhoneNumber().orEmpty()
                )
                LendingKycFlowType.AADHAAR -> getCustomStringFormatted(
                    com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_aadhaar_photo_not_uploaded_contact_support_message_name_s_number_s,
                    prefs.getUserName().orEmpty(),
                    prefs.getUserPhoneNumber().orEmpty()
                )
                LendingKycFlowType.SELFIE -> ""
            }
            requireContext().openWhatsapp(number, message)
        }

        binding.btnComeBackLater.setDebounceClickListener {
            EventBus.getDefault().post(
                GoToHomeEvent(
                    "OTP_LIMIT_EXCEED",
                    0
                )
            )
            dismissAllowingStateLoss()
        }

        binding.ivCross.setDebounceClickListener {
            dismissAllowingStateLoss()
        }
    }
}