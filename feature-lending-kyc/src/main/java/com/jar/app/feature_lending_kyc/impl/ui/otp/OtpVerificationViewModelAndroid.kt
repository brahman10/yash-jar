package com.jar.app.feature_lending_kyc.impl.ui.otp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_lending_kyc.shared.domain.use_case.FetchEmailDeliveryStatusUseCase
import com.jar.app.feature_lending_kyc.shared.domain.use_case.FetchExperianConsentUseCase
import com.jar.app.feature_lending_kyc.shared.domain.use_case.FetchExperianTermsAndConditionUseCase
import com.jar.app.feature_lending_kyc.shared.domain.use_case.RequestCreditReportOtpUseCase
import com.jar.app.feature_lending_kyc.shared.domain.use_case.RequestCreditReportOtpV2UseCase
import com.jar.app.feature_lending_kyc.shared.domain.use_case.RequestEmailOtpUseCase
import com.jar.app.feature_lending_kyc.shared.domain.use_case.VerifyAadhaarOtpUseCase
import com.jar.app.feature_lending_kyc.shared.domain.use_case.VerifyCreditReportOtpUseCase
import com.jar.app.feature_lending_kyc.shared.domain.use_case.VerifyCreditReportOtpV2UseCase
import com.jar.app.feature_lending_kyc.shared.domain.use_case.VerifyEmailOtpUseCase
import com.jar.app.feature_lending_kyc.shared.ui.otp.OtpVerificationViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class OtpVerificationViewModelAndroid @Inject constructor(
    private val verifyEmailOtpUseCase: VerifyEmailOtpUseCase,
    private val verifyCreditReportOtpUseCase: VerifyCreditReportOtpUseCase,
    private val fetchExperianTermsAndConditionUseCase: FetchExperianTermsAndConditionUseCase,
    private val requestEmailOtpUseCase: RequestEmailOtpUseCase,
    private val requestCreditReportOtpUseCase: RequestCreditReportOtpUseCase,
    private val requestCreditReportOtpV2UseCase: RequestCreditReportOtpV2UseCase,
    private val verifyCreditReportOtpV2UseCase: VerifyCreditReportOtpV2UseCase,
    private val verifyAadhaarOtpUseCase: VerifyAadhaarOtpUseCase,
    private val fetchExperianConsentUseCase: FetchExperianConsentUseCase,
    private val emailDeliveryStatusUseCase: FetchEmailDeliveryStatusUseCase
) : ViewModel() {

    private val viewModel by lazy {
        OtpVerificationViewModel(
            verifyEmailOtpUseCase = verifyEmailOtpUseCase,
            verifyCreditReportOtpUseCase = verifyCreditReportOtpUseCase,
            fetchExperianTermsAndConditionUseCase = fetchExperianTermsAndConditionUseCase,
            requestEmailOtpUseCase = requestEmailOtpUseCase,
            requestCreditReportOtpUseCase = requestCreditReportOtpUseCase,
            requestCreditReportOtpV2UseCase = requestCreditReportOtpV2UseCase,
            verifyCreditReportOtpV2UseCase = verifyCreditReportOtpV2UseCase,
            verifyAadhaarOtpUseCase = verifyAadhaarOtpUseCase,
            fetchExperianConsentUseCase = fetchExperianConsentUseCase,
            emailDeliveryStatusUseCase = emailDeliveryStatusUseCase,
            coroutineScope = viewModelScope
        )
    }

    fun getInstance() = viewModel
}