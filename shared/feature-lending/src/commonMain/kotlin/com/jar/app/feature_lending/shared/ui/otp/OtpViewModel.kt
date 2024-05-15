package com.jar.app.feature_lending.shared.ui.otp

import com.jar.app.feature_lending.shared.domain.model.OtpVerifyRequestData
import com.jar.app.feature_lending.shared.domain.model.v2.ReadyCashVerifyOtpResponse
import com.jar.app.feature_lending.shared.domain.model.v2.RequestOtpResponseV2
import com.jar.app.feature_lending.shared.domain.use_case.RequestLendingOtpUseCase
import com.jar.app.feature_lending.shared.domain.use_case.VerifyLendingOtpUseCase
import com.jar.app.feature_lending.shared.util.LendingConstants
import com.jar.internal.library.jar_core_kmm_flow.CFlow
import com.jar.internal.library.jar_core_kmm_flow.CStateFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonStateFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class OtpViewModel constructor(
    private val requestLendingOtpUseCase: RequestLendingOtpUseCase,
    private val verifyLendingOtpUseCase: VerifyLendingOtpUseCase,
    private val localIPAddress: String?,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)


    private val _otpFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<RequestOtpResponseV2?>>>(
            RestClientResult.none()
        )
    val otpFlow: CStateFlow<RestClientResult<ApiResponseWrapper<RequestOtpResponseV2?>>>
        get() = _otpFlow.toCommonStateFlow()

    private val _verifyOtpFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<ReadyCashVerifyOtpResponse?>>>(
            RestClientResult.none()
        )
    val verifyOtpFlow: CStateFlow<RestClientResult<ApiResponseWrapper<ReadyCashVerifyOtpResponse?>>>
        get() = _verifyOtpFlow.toCommonStateFlow()

    var otpTimeLeft = 20_000

    var currentValidationState =
        LendingConstants.OtpVerificationRequest.OTP_VERIFICATION_REQUEST_CANCELLED

    fun sendOtp(
        loanId: String,
        type: String = LendingConstants.LendingApplicationCheckpoints.LOAN_AGREEMENT
    ) {
        viewModelScope.launch {
            requestLendingOtpUseCase.requestLendingOtp(loanId, type).collect {
                _otpFlow.emit(it)
            }
        }
    }

    fun verifyOtp(
        loanId: String,
        otp: String,
        type: String = LendingConstants.LendingApplicationCheckpoints.LOAN_AGREEMENT
    ) {
        viewModelScope.launch {
            verifyLendingOtpUseCase.verifyLendingOtp(
                OtpVerifyRequestData(
                    applicationId = loanId,
                    otp = otp,
                    type = type,
                    ipAddress = localIPAddress
                )
            ).collect {
                _verifyOtpFlow.emit(it)
            }
        }
    }
}