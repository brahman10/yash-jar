package com.jar.app.feature_gold_lease.shared.ui

import com.jar.app.feature_gold_lease.shared.domain.model.GoldLeaseV2Details
import com.jar.app.feature_gold_lease.shared.domain.use_case.FetchGoldLeaseV2TransactionsUseCase
import com.jar.app.feature_gold_lease.shared.domain.use_case.FetchUserLeaseDetailsUseCase
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

class GoldLeaseV2UserLeaseDetailsViewModel constructor(
    private val fetchUserLeaseDetailsUseCase: FetchUserLeaseDetailsUseCase,
    private val fetchGoldLeaseV2TransactionsUseCase: FetchGoldLeaseV2TransactionsUseCase,
    coroutineScope: CoroutineScope?
) {
    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private val _goldLeaseDetailsFlow = MutableStateFlow<RestClientResult<ApiResponseWrapper<GoldLeaseV2Details?>>>(RestClientResult.none())
    val goldLeaseDetailsFlow: CStateFlow<RestClientResult<ApiResponseWrapper<GoldLeaseV2Details?>>>
        get() = _goldLeaseDetailsFlow.toCommonStateFlow()

    private val _goldLeaseTransactionsFlow = MutableStateFlow<RestClientResult<ApiResponseWrapper<List<com.jar.app.feature_gold_lease.shared.domain.model.GoldLeaseTransaction>?>>>(RestClientResult.none())
    val goldLeaseTransactionsFlow: CStateFlow<RestClientResult<ApiResponseWrapper<List<com.jar.app.feature_gold_lease.shared.domain.model.GoldLeaseTransaction>?>>>
        get() = _goldLeaseTransactionsFlow.toCommonStateFlow()

    fun fetchLandingDetails(leaseId: String) {
        viewModelScope.launch {
            fetchUserLeaseDetailsUseCase.fetchUserLeaseDetails(leaseId).collect {
                _goldLeaseDetailsFlow.emit(it)
            }
        }
    }

    fun fetchTransactions(leaseId: String) {
        viewModelScope.launch {
            fetchGoldLeaseV2TransactionsUseCase.fetchGoldLeaseV2Transactions(leaseId).collect {
                _goldLeaseTransactionsFlow.emit(it)
            }
        }
    }
}