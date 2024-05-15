package com.jar.app.feature_lending_kyc.shared.ui.email

import com.jar.app.core_base.data.dto.KycFeatureFlowType
import com.jar.app.feature_lending_kyc.shared.domain.model.EmailOtp
import com.jar.app.feature_lending_kyc.shared.domain.use_case.RequestEmailOtpUseCase
import com.jar.internal.library.jar_core_kmm_flow.CStateFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonStateFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class EmailVerificationViewModel constructor(
    private val requestEmailOtpUseCase: RequestEmailOtpUseCase,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)
    private val _requestEmailOtpFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<EmailOtp?>>>(RestClientResult.none())
    val requestEmailOtpFlow:
            CStateFlow<RestClientResult<ApiResponseWrapper<EmailOtp?>>>
        get() = _requestEmailOtpFlow.toCommonStateFlow()

    fun requestEmailOtp(email: String, featureFlowType: KycFeatureFlowType) {
        viewModelScope.launch {
            requestEmailOtpUseCase.requestEmailOtp(email, featureFlowType).collect {
                _requestEmailOtpFlow.emit(it)
            }
        }
    }
}