package com.jar.app.feature_lending_kyc.shared.ui.pan.loading_screen

import com.jar.app.core_base.data.dto.KycFeatureFlowType
import com.jar.app.feature_lending_kyc.shared.domain.model.CreditReportPAN
import com.jar.app.feature_lending_kyc.shared.domain.use_case.VerifyPanDetailsUseCase
import com.jar.internal.library.jar_core_kmm_flow.CStateFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonStateFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class VerifyPanDetailsViewModel constructor(
    private val verifyPanDetailsUseCase: VerifyPanDetailsUseCase,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private val _verifyPanDetailsFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<Unit?>>>(RestClientResult.none())
    val verifyPanDetailsFlow: CStateFlow<RestClientResult<ApiResponseWrapper<Unit?>>>
        get() = _verifyPanDetailsFlow.toCommonStateFlow()

    fun verifyPanDetails(creditReportPAN: CreditReportPAN, kycFeatureFlowType: KycFeatureFlowType) {
        viewModelScope.launch {
            verifyPanDetailsUseCase.verifyPanDetails(creditReportPAN, kycFeatureFlowType).collect {
                _verifyPanDetailsFlow.emit(it)
            }
        }
    }
}