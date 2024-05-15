package com.jar.app.feature_gold_delivery.shared.ui.store_item.list

import com.jar.app.feature_gold_delivery.shared.domain.use_case.CartOrderUseCase
import com.jar.app.feature_one_time_payments.shared.data.model.DynamicCardsOrderType
import com.jar.app.feature_one_time_payments.shared.domain.use_case.FetchOrderStatusDynamicCardsUseCase
import com.jar.internal.library.jar_core_kmm_flow.CStateFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonStateFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow

import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CartSuccessFragmentViewModel constructor(
    private val cartOrderUseCase: CartOrderUseCase,
    private val fetchOrderStatusDynamicCardsUseCase: FetchOrderStatusDynamicCardsUseCase,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)


    private val _submitFeedbackFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<Unit?>>>(RestClientResult.none())
    val submitFeedbackFlow: CStateFlow<RestClientResult<ApiResponseWrapper<Unit?>>>
        get() = _submitFeedbackFlow.toCommonStateFlow()

    private val _dynamicCardsFlow = MutableStateFlow<RestClientResult<ApiResponseWrapper<Unit?>>>(RestClientResult.none())
    val dynamicCardsFlow: CStateFlow<RestClientResult<ApiResponseWrapper<Unit?>>>
        get() = _dynamicCardsFlow.toCommonStateFlow()

    fun submitFeedback(orderId: String, feedback: Int) {
        viewModelScope.launch {
            cartOrderUseCase.submitFeedback(orderId, feedback).collectLatest {
                _submitFeedbackFlow.emit(it)
            }
        }
    }

    fun fetchOrderStatusDynamicCards(orderId: String) {
        viewModelScope.launch {
            fetchOrderStatusDynamicCardsUseCase.fetchOrderStatusDynamicCards(
                DynamicCardsOrderType.DELIVERY_ORDER,
                orderId
            ).collectLatest {
                _dynamicCardsFlow.value = it
            }
        }
    }
}