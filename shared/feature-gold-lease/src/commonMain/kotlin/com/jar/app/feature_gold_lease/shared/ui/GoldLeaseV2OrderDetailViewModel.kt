package com.jar.app.feature_gold_lease.shared.ui

import com.jar.app.core_base.util.orFalse
import com.jar.app.feature_gold_lease.shared.domain.model.GoldLeaseV2GoldOptions
import com.jar.app.feature_gold_lease.shared.domain.use_case.FetchGoldLeaseGoldOptionsUseCase
import com.jar.internal.library.jar_core_kmm_flow.CStateFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonStateFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch


 class GoldLeaseV2OrderDetailViewModel constructor(
    private val fetchGoldLeaseGoldOptionsUseCase: FetchGoldLeaseGoldOptionsUseCase,
    coroutineScope: CoroutineScope?
)  {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private val _goldLeaseGoldOptionsListFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<GoldLeaseV2GoldOptions?>>>(RestClientResult.none())
    val goldLeaseGoldOptionsListFlow: CStateFlow<RestClientResult<ApiResponseWrapper<GoldLeaseV2GoldOptions?>>>
        get() = _goldLeaseGoldOptionsListFlow.toCommonStateFlow()

    fun fetchGoldLeaseGoldOptions(planId: String) {
        viewModelScope.launch {
            fetchGoldLeaseGoldOptionsUseCase.fetchGoldLeaseGoldOptions(planId = planId).collect {
                it.data?.data?.leaseGoldOptionsAmountList?.find { suggestedOption -> suggestedOption.recommended.orFalse() }
                    ?.let { bestOption ->
                        bestOption.isBestTag = true
                    }
                it.data?.data?.leaseGoldOptionsVolumeList?.find { suggestedOption -> suggestedOption.recommended.orFalse() }
                    ?.let { bestOption ->
                        bestOption.isBestTag = true
                    }
                _goldLeaseGoldOptionsListFlow.emit(it)
            }
        }
    }
}