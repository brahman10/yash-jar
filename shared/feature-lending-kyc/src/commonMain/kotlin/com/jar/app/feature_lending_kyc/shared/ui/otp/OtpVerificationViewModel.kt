package com.jar.app.feature_lending_kyc.shared.ui.otp


import com.jar.app.core_base.data.dto.KycFeatureFlowType
import com.jar.app.core_base.util.countDownTimer
import com.jar.app.core_base.util.milliSecondsToCountDown
import com.jar.app.feature_lending_kyc.shared.domain.model.CreditReportOtp
import com.jar.app.feature_lending_kyc.shared.domain.model.EmailOtp
import com.jar.app.feature_lending_kyc.shared.domain.model.ExperianConsent
import com.jar.app.feature_lending_kyc.shared.domain.model.ExperianTnCResponse
import com.jar.app.feature_lending_kyc.shared.domain.model.VerifyAadhaarOtpRequest
import com.jar.app.feature_lending_kyc.shared.domain.model.VerifyOtpResponseV2
import com.jar.app.feature_lending_kyc.shared.domain.model.VerifyOtpV2RequestData
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
import com.jar.internal.library.jar_core_kmm_flow.CFlow
import com.jar.internal.library.jar_core_kmm_flow.CStateFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonStateFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class OtpVerificationViewModel constructor(
    private val verifyEmailOtpUseCase: VerifyEmailOtpUseCase,
    private val verifyCreditReportOtpUseCase: VerifyCreditReportOtpUseCase,
    private val fetchExperianTermsAndConditionUseCase: FetchExperianTermsAndConditionUseCase,
    private val requestEmailOtpUseCase: RequestEmailOtpUseCase,
    private val requestCreditReportOtpUseCase: RequestCreditReportOtpUseCase,
    private val requestCreditReportOtpV2UseCase: RequestCreditReportOtpV2UseCase,
    private val verifyCreditReportOtpV2UseCase: VerifyCreditReportOtpV2UseCase,
    private val verifyAadhaarOtpUseCase: VerifyAadhaarOtpUseCase,
    private val fetchExperianConsentUseCase: FetchExperianConsentUseCase,
    private val emailDeliveryStatusUseCase: FetchEmailDeliveryStatusUseCase,
    coroutineScope: CoroutineScope?
) {
    private var timerJob: Job? = null

    private val _resendOtpTimerFlow = MutableStateFlow<Long>(-1L)
    val resendOtpTimerFlow: CFlow<Long>
        get() = _resendOtpTimerFlow.toCommonFlow()


    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)
    private val _verifyEmailOtpFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<EmailOtp?>>>(RestClientResult.none())
    val verifyEmailOtpFlow:
            CStateFlow<RestClientResult<ApiResponseWrapper<EmailOtp?>>>
        get() = _verifyEmailOtpFlow.toCommonStateFlow()

    private val _verifyCreditReportOtpFlow =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<CreditReportOtp?>>>()
    val verifyCreditReportOtpFlow:
            CFlow<RestClientResult<ApiResponseWrapper<CreditReportOtp?>>>
        get() = _verifyCreditReportOtpFlow.toCommonFlow()

    private val _verifyCreditReportOtpV2Flow =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<VerifyOtpResponseV2?>>>()
    val verifyCreditReportOtpV2Flow:
            CFlow<RestClientResult<ApiResponseWrapper<VerifyOtpResponseV2?>>>
        get() = _verifyCreditReportOtpV2Flow.toCommonFlow()

    private val _fetchExperianTnCFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<ExperianTnCResponse?>>>(
            RestClientResult.none()
        )
    val fetchExperianTnCFlow:
            CStateFlow<RestClientResult<ApiResponseWrapper<ExperianTnCResponse?>>>
        get() = _fetchExperianTnCFlow.toCommonStateFlow()

    private val _fetchExperianConsentFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<ExperianConsent?>>>(RestClientResult.none())
    val fetchExperianConsentFlow:
            CStateFlow<RestClientResult<ApiResponseWrapper<ExperianConsent?>>>
        get() = _fetchExperianConsentFlow.toCommonStateFlow()

    private val _requestEmailOtpFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<EmailOtp?>>>(RestClientResult.none())
    val requestEmailOtpFlow:
            CStateFlow<RestClientResult<ApiResponseWrapper<EmailOtp?>>>
        get() = _requestEmailOtpFlow.toCommonStateFlow()

    private val _requestCreditReportOtpFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<CreditReportOtp?>>>(RestClientResult.none())
    val requestCreditReportOtpFlow:
            CStateFlow<RestClientResult<ApiResponseWrapper<CreditReportOtp?>>>
        get() = _requestCreditReportOtpFlow.toCommonStateFlow()

    private val _verifyAadhaarOtpFlow =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<Unit?>>>()
    val verifyAadhaarOtpFlow: CFlow<RestClientResult<ApiResponseWrapper<Unit?>>>
        get() = _verifyAadhaarOtpFlow.toCommonFlow()

    private val _emailDeliveryStatusFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<EmailOtp?>>>(RestClientResult.none())
    val emailDeliveryStatusFlow: CStateFlow<RestClientResult<ApiResponseWrapper<EmailOtp?>>>
        get() = _emailDeliveryStatusFlow.toCommonStateFlow()

    fun requestCreditReportOtp(kycFeatureFlowType: KycFeatureFlowType) {
        viewModelScope.launch {
            requestCreditReportOtpUseCase.requestCreditReportOtp(kycFeatureFlowType).collect {
                _requestCreditReportOtpFlow.emit(it)
            }
        }
    }

    fun requestEmailOtp(email: String, kycFeatureFlowType: KycFeatureFlowType) {
        viewModelScope.launch {
            requestEmailOtpUseCase.requestEmailOtp(email, kycFeatureFlowType).collect {
                _requestEmailOtpFlow.emit(it)
            }
        }
    }

    fun verifyEmailOtp(email: String, otp: String, kycFeatureFlowType: KycFeatureFlowType) {
        viewModelScope.launch {
            verifyEmailOtpUseCase.verifyEmailOtp(email, otp, kycFeatureFlowType).collect {
                _verifyEmailOtpFlow.emit(it)
            }
        }
    }

    fun verifyCreditReportOtp(otp: String, kycFeatureFlowType: KycFeatureFlowType) {
        viewModelScope.launch {
            verifyCreditReportOtpUseCase.verifyCreditReportOtp(otp, kycFeatureFlowType).collect {
                _verifyCreditReportOtpFlow.emit(it)
            }
        }
    }

    fun verifyCreditReportOtpV2(otp: String, kycFeatureFlowType: KycFeatureFlowType, name: String? = null, panNumber: String? = null) {
        viewModelScope.launch {
            verifyCreditReportOtpV2UseCase.verifyCreditReportOtp(
                VerifyOtpV2RequestData(
                    name = name,
                    otp = otp,
                    panNumber = panNumber
                ),
                kycFeatureFlowType
            ).collect {
                _verifyCreditReportOtpV2Flow.emit(it)
            }
        }
    }

    fun fetchExperianTermsAndCondition(kycFeatureFlowType: KycFeatureFlowType) {
        viewModelScope.launch {
            fetchExperianTermsAndConditionUseCase.fetchExperianTermsAndConditionUseCase(kycFeatureFlowType).collect {
                _fetchExperianTnCFlow.emit(it)
            }
        }
    }

    fun fetchExperianConsent(kycFeatureFlowType: KycFeatureFlowType) {
        viewModelScope.launch {
            fetchExperianConsentUseCase.fetchExperianConsent(kycFeatureFlowType).collect {
                _fetchExperianConsentFlow.emit(it)
            }
        }
    }

    fun getEmailDeliveryStatus(emailId: String, messageId: String, kycFeatureFlowType: KycFeatureFlowType) {
        viewModelScope.launch {
            emailDeliveryStatusUseCase.fetchEmailDeliveryStatus(emailId, messageId, kycFeatureFlowType).collect {
                _emailDeliveryStatusFlow.emit(it)
            }
        }
    }

    fun verifyAadhaarOtp(otp: String, sessionId: String, kycFeatureFlowType: KycFeatureFlowType) {
        viewModelScope.launch {
            verifyAadhaarOtpUseCase.verifyAadhaarOtp(VerifyAadhaarOtpRequest(otp, sessionId),kycFeatureFlowType)
                .collect {
                    _verifyAadhaarOtpFlow.emit(it)
                }
        }
    }
    fun startOtpResendTimer(timeInSeconds: Int) {
        timerJob?.cancel()
        timerJob = viewModelScope.countDownTimer(
            totalMillis = timeInSeconds * 1000L,
            onInterval = {
                _resendOtpTimerFlow.emit(it)
            },
            onFinished = {
                _resendOtpTimerFlow.emit(0L)
            }
        )
    }

    fun milliSecondsToCountDown(remainingTimeInMillis: Int): String {
        return remainingTimeInMillis.milliSecondsToCountDown()
    }
}