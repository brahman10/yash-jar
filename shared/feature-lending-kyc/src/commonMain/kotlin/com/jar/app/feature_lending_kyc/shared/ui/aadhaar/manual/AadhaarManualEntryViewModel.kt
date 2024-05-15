package com.jar.app.feature_lending_kyc.shared.ui.aadhaar.manual

import com.jar.app.core_base.data.dto.KycFeatureFlowType
import com.jar.app.core_base.util.countDownTimer
import com.jar.app.core_base.util.milliSecondsToCountDown
import com.jar.app.feature_lending_kyc.shared.domain.model.AadhaarCaptcha
import com.jar.app.feature_lending_kyc.shared.domain.model.AadhaarOtpRequest
import com.jar.app.feature_lending_kyc.shared.domain.use_case.FetchAadhaarCaptchaUseCase
import com.jar.app.feature_lending_kyc.shared.domain.use_case.RequestAadhaarOtpUseCase
import com.jar.internal.library.jar_core_kmm_flow.CFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class AadhaarManualEntryViewModel(
    private val fetchAadhaarCaptchaUseCase: FetchAadhaarCaptchaUseCase,
    private val requestAadhaarOtpUseCase: RequestAadhaarOtpUseCase,
    coroutineScope: CoroutineScope?
) {
    companion object {
        private const val RESEND_OTP_TIMER_IN_SECONDS = 60
    }
    private var timerJob: Job? = null

    private val _resendOtpTimerFlow = MutableStateFlow<Long>(-1L)
    val resendOtpTimerFlow: CFlow<Long>
        get() = _resendOtpTimerFlow.toCommonFlow()

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)


    private val _captchaResponseFlow =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<AadhaarCaptcha?>>>()
    val captchaResponseFlow: CFlow<RestClientResult<ApiResponseWrapper<AadhaarCaptcha?>>>
        get() = _captchaResponseFlow.toCommonFlow()

    private val _aadhaarOtpResponseFlow =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<Unit?>>>()
    val aadhaarOtpResponseFlow: CFlow<RestClientResult<ApiResponseWrapper<Unit?>>>
        get() = _aadhaarOtpResponseFlow.toCommonFlow()

    fun fetchAadhaarCaptcha(kycFeatureFlowType: KycFeatureFlowType) {
        viewModelScope.launch {
            fetchAadhaarCaptchaUseCase.fetchAadhaarCaptcha(kycFeatureFlowType).collect {
                _captchaResponseFlow.emit(it)
            }
        }
    }

    fun requestAadhaarOtp(
        aadhaarNumber: String,
        captcha: String,
        sessionId: String,
        kycFeatureFlowType: KycFeatureFlowType
    ) {
        val aadhaarOtpRequest =
            AadhaarOtpRequest(aadhaarNumber, captcha, sessionId)
        viewModelScope.launch {
            requestAadhaarOtpUseCase.requestAadhaarOtp(aadhaarOtpRequest, kycFeatureFlowType).collect {
                _aadhaarOtpResponseFlow.emit(it)
            }
        }
        startResendTimer()
    }
    private fun startResendTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.countDownTimer(
            totalMillis = RESEND_OTP_TIMER_IN_SECONDS * 1000L,
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