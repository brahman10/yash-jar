package com.jar.app.feature_gold_lease.shared.ui

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_gold_lease.shared.domain.model.GoldLeaseRiskFactor
import com.jar.app.feature_gold_lease.shared.domain.use_case.FetchGoldLeaseRiskFactorUseCase
import com.jar.internal.library.jar_core_kmm_flow.CFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class GoldLeaseRiskFactorViewModel constructor(
    private val fetchGoldLeaseRiskFactorUseCase: FetchGoldLeaseRiskFactorUseCase,
    coroutineScope: CoroutineScope?
){

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private val _goldLeaseRiskFactorFlow = MutableSharedFlow<RestClientResult<ApiResponseWrapper<GoldLeaseRiskFactor?>>>()
    val goldLeaseRiskFactorFlow: CFlow<RestClientResult<ApiResponseWrapper<GoldLeaseRiskFactor?>>>
        get() = _goldLeaseRiskFactorFlow.toCommonFlow()

    fun fetchTermsAndConditions() {
        viewModelScope.launch {
            fetchGoldLeaseRiskFactorUseCase.fetchGoldLeaseRiskFactors().collect {
                _goldLeaseRiskFactorFlow.emit(it)
            }
        }
    }

}