package com.jar.app.feature_kyc.shared.ui.enter_pan_manually

import com.jar.app.feature_kyc.shared.domain.model.KYCStatusDetails
import com.jar.app.feature_kyc.shared.domain.model.KycContext
import com.jar.app.feature_kyc.shared.domain.model.ManualKycRequest
import com.jar.app.feature_kyc.shared.domain.use_case.PostManualKycRequestUseCase
import com.jar.internal.library.jar_core_kmm_flow.CStateFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonStateFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class EnterPanDetailsManuallyViewModel constructor(
    private val postManualKycRequestUseCase: PostManualKycRequestUseCase,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)


    private val _manualKycRequestFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<KYCStatusDetails?>>>(RestClientResult.none())
    val manualKycRequestFlow: CStateFlow<RestClientResult<ApiResponseWrapper<KYCStatusDetails?>>>
        get() = _manualKycRequestFlow.toCommonStateFlow()

    fun postManualKycRequest(manualKycRequest: ManualKycRequest) {
        viewModelScope.launch {
            postManualKycRequestUseCase.postManualKycRequest(
                manualKycRequest = manualKycRequest,
                kycContext = KycContext.SELL_FLOW.name
            ).collect {
                _manualKycRequestFlow.emit(it)
            }
        }
    }

}