package com.jar.app.feature_gold_lease.shared.ui

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_gold_lease.shared.domain.model.GoldLeaseTermsAndConditions
import com.jar.app.feature_gold_lease.shared.domain.use_case.FetchGoldLeaseTermsAndConditionsUseCase
import com.jar.internal.library.jar_core_kmm_flow.CFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class GoldLeaseTNCViewModel constructor(
    private val fetchGoldLeaseTermsAndConditionsUseCase: FetchGoldLeaseTermsAndConditionsUseCase,
    coroutineScope: CoroutineScope?
){

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private val _goldLeaseTNCFlow = MutableSharedFlow<RestClientResult<ApiResponseWrapper<GoldLeaseTermsAndConditions?>>>()
    val goldLeaseTNCFlow: CFlow<RestClientResult<ApiResponseWrapper<GoldLeaseTermsAndConditions?>>>
        get() = _goldLeaseTNCFlow.toCommonFlow()

    fun fetchTermsAndConditions() {
        viewModelScope.launch {
            fetchGoldLeaseTermsAndConditionsUseCase.fetchGoldLeaseTermsAndConditions().collect {
                _goldLeaseTNCFlow.emit(it)
            }
        }
    }
}
