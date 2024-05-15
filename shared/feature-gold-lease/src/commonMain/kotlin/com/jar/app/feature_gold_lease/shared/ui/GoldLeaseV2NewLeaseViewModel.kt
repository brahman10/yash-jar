package com.jar.app.feature_gold_lease.shared.ui

import com.jar.app.feature_gold_lease.shared.domain.model.GoldLeaseLandingDetails
import com.jar.app.feature_gold_lease.shared.domain.use_case.FetchGoldLeaseLandingDetailsUseCase
import com.jar.internal.library.jar_core_kmm_flow.CStateFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonStateFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class GoldLeaseV2NewLeaseViewModel constructor(
    private val fetchGoldLeaseLandingDetailsUseCase: FetchGoldLeaseLandingDetailsUseCase,
    coroutineScope: CoroutineScope?
){

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private val _goldLeaseLandingDetailsFlow = MutableStateFlow<RestClientResult<ApiResponseWrapper<com.jar.app.feature_gold_lease.shared.domain.model.GoldLeaseLandingDetails?>>>(RestClientResult.none())
    val goldLeaseLandingDetailsFlow: CStateFlow<RestClientResult<ApiResponseWrapper<com.jar.app.feature_gold_lease.shared.domain.model.GoldLeaseLandingDetails?>>>
        get() = _goldLeaseLandingDetailsFlow.toCommonStateFlow()

    fun fetchLandingDetails() {
        viewModelScope.launch {
            fetchGoldLeaseLandingDetailsUseCase.fetchGoldLeaseLandingDetails().collect {
                _goldLeaseLandingDetailsFlow.emit(it)
            }
        }
    }
}