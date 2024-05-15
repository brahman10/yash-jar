package com.jar.app.feature_gold_lease.shared.ui

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_gold_lease.shared.domain.model.GoldLeaseFaq
import com.jar.app.feature_gold_lease.shared.domain.use_case.FetchGoldLeaseFaqsUseCase
import com.jar.internal.library.jar_core_kmm_flow.CFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class GoldLeaseFaqViewModel constructor(
    private val fetchGoldLeaseFaqsUseCase: FetchGoldLeaseFaqsUseCase,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private val _goldLeaseFaqsFlow =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<GoldLeaseFaq?>>>()
    val goldLeaseFaqsFlow: CFlow<RestClientResult<ApiResponseWrapper<GoldLeaseFaq?>>>
        get() = _goldLeaseFaqsFlow.toCommonFlow()

    fun fetchFaqs() {
        viewModelScope.launch {
            fetchGoldLeaseFaqsUseCase.fetchGoldLeaseFaqs().collect {
                _goldLeaseFaqsFlow.emit(it)
            }
        }
    }
}