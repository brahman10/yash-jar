package com.jar.app.feature_lending_kyc.shared.ui.pan.report_fetched.loading

import com.jar.app.core_base.data.dto.KycFeatureFlowType
import com.jar.app.feature_lending_kyc.shared.domain.model.CreditReportOtp
import com.jar.app.feature_lending_kyc.shared.domain.use_case.RequestCreditReportOtpUseCase
import com.jar.internal.library.jar_core_kmm_flow.CStateFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonStateFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class CreditReportOtpVerificationLoadingViewModel constructor(
    private val requestCreditReportOtpUseCase: RequestCreditReportOtpUseCase,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)
    private val _requestCreditReportOtpFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<CreditReportOtp?>>>(RestClientResult.none())
    val requestCreditReportOtpFlow:
            CStateFlow<RestClientResult<ApiResponseWrapper<CreditReportOtp?>>>
        get() = _requestCreditReportOtpFlow.toCommonStateFlow()

    fun requestCreditReportOtp() {
        viewModelScope.launch {
            requestCreditReportOtpUseCase.requestCreditReportOtp(KycFeatureFlowType.UNKNOWN).collect {
                _requestCreditReportOtpFlow.emit(it)
            }
        }
    }
}