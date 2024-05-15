package com.jar.app.feature_lending.shared.ui.realtime_flow_with_camps.pan.enter_pan

import com.jar.app.feature_kyc.shared.domain.model.KYCStatusDetails
import com.jar.app.feature_kyc.shared.domain.model.KycContext
import com.jar.app.feature_kyc.shared.domain.model.ManualKycRequest
import com.jar.app.feature_kyc.shared.domain.use_case.PostManualKycRequestUseCase
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class EnterPanNumberViewModel constructor(
    private val postManualKycRequestUseCase: PostManualKycRequestUseCase,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)


    private val _uiStateFlow =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<KYCStatusDetails?>>>()
    val uiStateFlow: SharedFlow<RestClientResult<ApiResponseWrapper<KYCStatusDetails?>>>
        get() = _uiStateFlow.asSharedFlow()


    fun manualFetchPANDetails(manualKycRequest: ManualKycRequest) {
        viewModelScope.launch {
            postManualKycRequestUseCase.postManualKycRequest(
                manualKycRequest = manualKycRequest,
                fetch = true,
                kycContext = KycContext.SELL_FLOW.name
            ).collectLatest { data ->
                _uiStateFlow.emit(data)
            }
        }
    }
}