package com.jar.app.feature_lending_kyc.shared.ui.aadhaar.confirmation

import com.jar.app.core_base.data.dto.KycFeatureFlowType
import com.jar.app.feature_lending_kyc.shared.domain.use_case.SaveAadhaarDetailsUseCase
import com.jar.internal.library.jar_core_kmm_flow.CStateFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonStateFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class AadhaarConfirmationViewModel constructor(
    private val saveAadhaarDetailsUseCase: SaveAadhaarDetailsUseCase,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)
    private val _saveAadhaarDetailFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<Unit?>>>(RestClientResult.none())
    val saveAadhaarDetailFlow: CStateFlow<RestClientResult<ApiResponseWrapper<Unit?>>>
        get() = _saveAadhaarDetailFlow.toCommonStateFlow()

    fun saveAadhaarDetail(kycFeatureFlowType: KycFeatureFlowType) {
        viewModelScope.launch {
            saveAadhaarDetailsUseCase.saveAadhaarDetails(kycFeatureFlowType).collect {
                _saveAadhaarDetailFlow.emit(it)
            }
        }
    }
}