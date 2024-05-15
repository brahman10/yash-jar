package com.jar.app.feature_lending_kyc.shared.ui.pan.manual

import com.jar.app.feature_kyc.shared.domain.model.KYCStatusDetails
import com.jar.app.feature_kyc.shared.domain.model.ManualKycRequest
import com.jar.app.feature_kyc.shared.domain.use_case.PostManualKycRequestUseCase
import com.jar.internal.library.jar_core_kmm_flow.CFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class EnterPanManuallyViewModel(
    private val postManualKycRequestUseCase: PostManualKycRequestUseCase,
    coroutineScope: CoroutineScope?
) {
    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private val _manualKycRequestLiveData =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<KYCStatusDetails?>>>(RestClientResult.none())
    val manualKycRequestLiveData: CFlow<RestClientResult<ApiResponseWrapper<KYCStatusDetails?>>>
        get() = _manualKycRequestLiveData.toCommonFlow()

    fun manualEntryFetchPanDetails(manualKycRequest: ManualKycRequest) {
        viewModelScope.launch {
            postManualKycRequestUseCase.postManualKycRequest(manualKycRequest, true).collect {
                _manualKycRequestLiveData.emit(it)
            }
        }
    }
}