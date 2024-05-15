package com.jar.app.feature_lending_kyc.shared.ui.pan.loading_screen

import com.jar.app.core_base.data.dto.KycFeatureFlowType
import com.jar.app.feature_lending_kyc.shared.domain.model.CreditReportPAN
import com.jar.app.feature_lending_kyc.shared.domain.use_case.FetchJarVerifiedUserPanUseCase
import com.jar.internal.library.jar_core_kmm_flow.CStateFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonStateFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class PanFromJarLoadingViewModel constructor(
    private val fetchJarVerifiedUserPanUseCase: FetchJarVerifiedUserPanUseCase,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private val _fetchJarVerifiedPanFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<CreditReportPAN?>>>(RestClientResult.none())
    val fetchJarVerifiedPanFlow: CStateFlow<RestClientResult<ApiResponseWrapper<CreditReportPAN?>>>
        get() = _fetchJarVerifiedPanFlow.toCommonStateFlow()

    fun fetchJarVerifiedPan(kycFeatureFlowType: KycFeatureFlowType) {
        viewModelScope.launch {
            fetchJarVerifiedUserPanUseCase.fetchJarVerifiedUserPan(kycFeatureFlowType).collect {
                _fetchJarVerifiedPanFlow.emit(it)
            }
        }
    }
}