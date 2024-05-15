package com.jar.app.feature_gold_lease.shared.ui

import com.jar.app.feature_gold_lease.shared.domain.model.GoldLeaseV2JewellerDetails
import com.jar.app.feature_gold_lease.shared.domain.use_case.FetchGoldLeaseJewellerDetailsUseCase
import com.jar.internal.library.jar_core_kmm_flow.CFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch


class GoldLeaseV2JewellerDetailsViewModel constructor(
    private val fetchGoldLeaseJewellerDetailsUseCase: FetchGoldLeaseJewellerDetailsUseCase,
    coroutineScope: CoroutineScope?
){

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private val _goldLeaseJewellerDetailsFlow =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<GoldLeaseV2JewellerDetails?>>>()
    val goldLeaseJewellerDetailsFlow: CFlow<RestClientResult<ApiResponseWrapper<GoldLeaseV2JewellerDetails?>>>
        get() = _goldLeaseJewellerDetailsFlow.toCommonFlow()

    fun fetchJewellerDetails(jewellerId: String) {
        viewModelScope.launch {
            fetchGoldLeaseJewellerDetailsUseCase.fetchJewellerDetails(jewellerId).collect {
                _goldLeaseJewellerDetailsFlow.emit(it)
            }
        }
    }
}