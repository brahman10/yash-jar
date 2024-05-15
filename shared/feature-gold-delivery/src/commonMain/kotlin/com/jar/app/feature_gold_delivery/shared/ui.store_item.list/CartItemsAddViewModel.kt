package com.jar.app.feature_gold_delivery.shared.ui.store_item.list

import com.jar.app.feature_gold_delivery.shared.domain.model.AddCartItemRequest
import com.jar.app.feature_gold_delivery.shared.domain.use_case.AddItemToCartWithFlowUseCase
import com.jar.internal.library.jar_core_kmm_flow.CStateFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonStateFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow

import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CartItemsAddViewModel constructor(
    private val updateCartUseCase: AddItemToCartWithFlowUseCase,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private val _currentCartLiveData =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<Unit?>>>(RestClientResult.none())
    val currentCartLiveData: CStateFlow<RestClientResult<ApiResponseWrapper<Unit?>>>
        get() = _currentCartLiveData.toCommonStateFlow()

    fun addToCart(addItemCartItemRequest: AddCartItemRequest) {
        viewModelScope.launch {
            updateCartUseCase.addItemToCart(addItemCartItemRequest).collectLatest {
                _currentCartLiveData.emit(it)
            }
        }
    }
}