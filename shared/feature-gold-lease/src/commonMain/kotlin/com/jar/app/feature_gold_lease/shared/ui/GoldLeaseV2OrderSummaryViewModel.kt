package com.jar.app.feature_gold_lease.shared.ui

import com.jar.app.feature_gold_lease.shared.domain.model.*
import com.jar.app.feature_gold_lease.shared.domain.use_case.FetchGoldLeaseOrderSummaryUseCase
import com.jar.app.feature_gold_lease.shared.domain.use_case.FetchGoldLeaseRetryDataUseCase
import com.jar.app.feature_gold_lease.shared.domain.use_case.InitiateGoldLeaseV2UseCase
import com.jar.internal.library.jar_core_kmm_flow.CFlow
import com.jar.internal.library.jar_core_kmm_flow.CStateFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonStateFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class GoldLeaseV2OrderSummaryViewModel constructor(
    private val fetchGoldLeaseOrderSummaryUseCase: FetchGoldLeaseOrderSummaryUseCase,
    private val initiateGoldLeaseV2UseCase: InitiateGoldLeaseV2UseCase,
    private val fetchGoldLeaseRetryDataUseCase: FetchGoldLeaseRetryDataUseCase,
    coroutineScope: CoroutineScope?
){

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private val _goldLeaseOrderSummaryFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<GoldLeaseV2OrderSummary?>>>(RestClientResult.none())
    val goldLeaseOrderSummaryFlow: CStateFlow<RestClientResult<ApiResponseWrapper<GoldLeaseV2OrderSummary?>>>
        get() = _goldLeaseOrderSummaryFlow.toCommonStateFlow()

    private val _goldLeaseInitiateFlow =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<GoldLeaseV2InitiateResponse?>>>()
    val goldLeaseInitiateFlow: CFlow<RestClientResult<ApiResponseWrapper<GoldLeaseV2InitiateResponse?>>>
        get() = _goldLeaseInitiateFlow.toCommonFlow()

    private val _goldLeaseRetryDataFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<GoldLeaseV2OrderSummaryScreenData?>>>(RestClientResult.none())
    val goldLeaseRetryDataFlow: CStateFlow<RestClientResult<ApiResponseWrapper<GoldLeaseV2OrderSummaryScreenData?>>>
        get() = _goldLeaseRetryDataFlow.toCommonStateFlow()

    var amountToPay = 0.0f

    var goldLeaseV2OrderSummary: GoldLeaseV2OrderSummary? = null

    var goldLeaseRetryData: GoldLeaseV2OrderSummaryScreenData? = null

    var isKycVerified: Boolean? = null

    var isInitiateFlow = false

    fun initiateGoldLeasePayment(goldLeaseV2InitiateRequest: GoldLeaseV2InitiateRequest) {
        viewModelScope.launch {
            initiateGoldLeaseV2UseCase.initiateGoldLeaseV2(goldLeaseV2InitiateRequest)
                .collect {
                    _goldLeaseInitiateFlow.emit(it)
                }
        }
    }

    fun fetchGoldLeaseOrderSummary(assetLeaseConfigId: String) {
        viewModelScope.launch {
            fetchGoldLeaseOrderSummaryUseCase.
            fetchGoldLeaseOrderSummary(assetLeaseConfigId).collect {
                goldLeaseV2OrderSummary = it.data?.data
                _goldLeaseOrderSummaryFlow.emit(it)
            }
        }
    }

    fun fetchGoldLeaseRetryData(leaseId: String) {
        viewModelScope.launch {
            fetchGoldLeaseRetryDataUseCase.fetchGoldLeaseRetryData(leaseId).collect {
                goldLeaseRetryData = it.data?.data
                _goldLeaseRetryDataFlow.emit(it)
            }
        }
    }
}