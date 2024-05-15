package com.jar.app.feature_gold_price.ui

import com.jar.app.feature_gold_price.shared.data.model.FetchCurrentGoldPriceResponse
import com.jar.app.feature_gold_price.shared.data.model.GoldPriceContext
import com.jar.app.feature_gold_price.shared.data.model.GoldPriceType
import com.jar.app.feature_gold_price.shared.domain.use_case.FetchCurrentGoldPriceUseCase
import com.jar.internal.library.jar_core_kmm_flow.CStateFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonStateFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class GoldPriceViewModel(
    private val fetchCurrentGoldPriceUseCase: FetchCurrentGoldPriceUseCase,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private val _fetchCurrentGoldPriceFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<FetchCurrentGoldPriceResponse>>>(
            RestClientResult.none()
        )
    val fetchCurrentGoldPriceFlow: CStateFlow<RestClientResult<ApiResponseWrapper<FetchCurrentGoldPriceResponse>>>
        get() = _fetchCurrentGoldPriceFlow.toCommonStateFlow()

    fun fetchCurrentGoldPrice(
        goldPriceType: GoldPriceType,
        goldPriceContext: GoldPriceContext?
    ) {
        viewModelScope.launch {
            fetchCurrentGoldPriceUseCase.fetchCurrentGoldPrice(
                goldPriceType = goldPriceType,
                goldPriceContext = goldPriceContext
            ).collect {
                _fetchCurrentGoldPriceFlow.emit(it)
            }
        }
    }
}