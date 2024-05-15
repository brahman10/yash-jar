package com.jar.app.feature_onboarding.shared.ui.enter_otp

import com.jar.app.core_base.domain.mapper.toUserResponseData
import com.jar.app.core_base.domain.model.UserResponseData
import com.jar.app.core_base.util.DeviceUtils
import com.jar.app.core_base.util.countDownTimer
import com.jar.app.core_base.util.milliSecondsToCountDown
import com.jar.app.feature_onboarding.shared.domain.model.OtpStatusResponse
import com.jar.app.feature_onboarding.shared.domain.model.TruecallerLoginRequest
import com.jar.app.feature_onboarding.shared.domain.usecase.FetchOTPStatusUseCase
import com.jar.app.feature_onboarding.shared.domain.usecase.OtpLoginUseCase
import com.jar.app.feature_onboarding.shared.domain.usecase.RequestOtpUseCase
import com.jar.app.feature_onboarding.shared.domain.usecase.TruecallerLoginUseCase
import com.jar.app.feature_user_api.domain.model.DeviceDetails
import com.jar.app.feature_user_api.domain.model.OTPLoginRequest
import com.jar.app.feature_user_api.domain.model.RequestOtpData
import com.jar.internal.library.jar_core_kmm_flow.CFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.internal.library.jar_core_network.api.util.mapToDTO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch

class EnterOtpFragmentViewModel constructor(
    private val deviceUtils: DeviceUtils,
    private val otpLoginUseCase: OtpLoginUseCase,
    private val requestOtpUseCase: RequestOtpUseCase,
    private val fetchOTPStatusUseCase: FetchOTPStatusUseCase,
    private val truecallerLoginUseCase: TruecallerLoginUseCase,
    coroutineScope: CoroutineScope?
) {
    companion object {
        private const val RESEND_OTP_TIMER_IN_SECONDS = 60
    }

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private val _resendOtpTimerFlow = MutableStateFlow<Long>(-1L)
    val resendOtpTimerFlow: CFlow<Long>
        get() = _resendOtpTimerFlow.toCommonFlow()

    private var timerJob: Job? = null

    private val _otpLoginFlow =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<UserResponseData?>>>()
    val otpLoginFlow: CFlow<RestClientResult<ApiResponseWrapper<UserResponseData?>>>
        get() = _otpLoginFlow.toCommonFlow()

    private val _requestOtpFlow =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<RequestOtpData?>>>()
    val requestOtpFlow: CFlow<RestClientResult<ApiResponseWrapper<RequestOtpData?>>>
        get() = _requestOtpFlow.toCommonFlow()

    private val _fetchOtpStatusFlow =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<OtpStatusResponse>>>()
    val fetchOtpStatusFlow: CFlow<RestClientResult<ApiResponseWrapper<OtpStatusResponse>>>
        get() = _fetchOtpStatusFlow.toCommonFlow()

    private val _truecallerLoginFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<UserResponseData>>>(RestClientResult.none())
    val truecallerLoginFlow: CFlow<RestClientResult<ApiResponseWrapper<UserResponseData>>>
        get() = _truecallerLoginFlow.shareIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), 0
        ).toCommonFlow()

    var currentOtp: String = ""
    var trueCallerAuthDone = false
    var payload: String = ""
    var signature: String = ""
    var signatureAlgorithm: String = ""

    fun truecallerLoginSuccessful(
        payload: String,
        signature: String,
        signatureAlgorithm: String,
        logoutFromDevices: Boolean
    ) {
        this.payload = payload
        this.signature = signature
        this.signatureAlgorithm = signatureAlgorithm

        viewModelScope.launch {
            truecallerLoginUseCase.loginViaTruecaller(
                TruecallerLoginRequest(
                    payload,
                    signature,
                    signatureAlgorithm,
                    DeviceDetails(
                        advertisingId = deviceUtils.getAdvertisingId(),
                        deviceId = deviceUtils.getDeviceId(),
                        os = deviceUtils.getOsName()
                    ),
                    logoutFromOtherDevices = logoutFromDevices
                )
            ).collect {
                _truecallerLoginFlow.emit(it)
            }
        }
    }

    fun loginViaOtp(
        phoneNumber: String,
        countryCode: String,
        otp: String,
        reqId: String,
        logoutFromOtherDevices: Boolean = false
    ) {
        currentOtp = otp
        viewModelScope.launch {
            val otpLoginRequest = OTPLoginRequest(
                phoneNumber,
                countryCode,
                otp,
                reqId,
                DeviceDetails(
                    advertisingId = deviceUtils.getAdvertisingId(),
                    deviceId = deviceUtils.getDeviceId(),
                    os = deviceUtils.getOsName()
                ),
                logoutFromOtherDevices = logoutFromOtherDevices
            )
            otpLoginUseCase.loginViaOtp(otpLoginRequest)
                .mapToDTO {
                    it?.let {
                        it.toUserResponseData()
                    }
                }
                .collect {
                    _otpLoginFlow.emit(it)
                }
        }
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

    fun requestOtp(hasExperianConsent: Boolean, phoneNumber: String, countryCode: String) {
        viewModelScope.launch {
            requestOtpUseCase.requestOtp(hasExperianConsent, phoneNumber, countryCode).collect {
                _requestOtpFlow.emit(it)
            }
        }
        startResendTimer()
    }

    fun fetchOTPStatus(phoneNumber: String, countryCode: String) {
        viewModelScope.launch {
            fetchOTPStatusUseCase.fetchOTPStatus(phoneNumber, countryCode).collect {
                _fetchOtpStatusFlow.emit(it)
            }
        }
    }

    fun requestOtpViaCall(phoneNumber: String, countryCode: String) {
        viewModelScope.launch {
            requestOtpUseCase.requestOtpViaCall(phoneNumber, countryCode).collect {
                _requestOtpFlow.emit(it)
            }
        }
        startResendTimer()
    }

    fun milliSecondsToCountDown(remainingTimeInMillis: Int): String {
        return remainingTimeInMillis.milliSecondsToCountDown()
    }
}