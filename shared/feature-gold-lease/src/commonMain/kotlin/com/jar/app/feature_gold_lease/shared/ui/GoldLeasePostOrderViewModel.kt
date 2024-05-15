package com.jar.app.feature_gold_lease.shared.ui

import com.jar.app.feature_gold_lease.shared.domain.model.GoldLeaseV2StatusResponse
import com.jar.app.feature_gold_lease.shared.domain.use_case.FetchGoldLeaseStatusUseCase
import com.jar.internal.library.jar_core_kmm_flow.CFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class GoldLeasePostOrderViewModel constructor(
    private val fetchGoldLeaseStatusUseCase: FetchGoldLeaseStatusUseCase,
    coroutineScope: CoroutineScope?
){

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private val _goldLeasStatusFlow =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<GoldLeaseV2StatusResponse?>>>()
    val goldLeasStatusFlow: CFlow<RestClientResult<ApiResponseWrapper<GoldLeaseV2StatusResponse?>>>
        get() = _goldLeasStatusFlow.toCommonFlow()

    fun fetchGoldLeaseStatus(leaseId: String) {
        viewModelScope.launch {
            fetchGoldLeaseStatusUseCase.fetchGoldLeaseStatus(leaseId).collect {
                _goldLeasStatusFlow.emit(it)
            }
        }
    }
}