package com.jar.app.feature_gold_delivery.shared.ui.store_item.list

import com.jar.app.feature_gold_delivery.shared.domain.use_case.DeleteItemToCartUseCase
import com.jar.internal.library.jar_core_kmm_flow.CStateFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonStateFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow

import kotlinx.coroutines.launch

class CartItemsDeleteViewModel constructor(
    private val deleteItemFromCart: DeleteItemToCartUseCase,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private val _deleteAddressLiveData =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<Unit?>>>(RestClientResult.none())
    val deleteAddressLiveData: CStateFlow<RestClientResult<ApiResponseWrapper<Unit?>>>
        get() = _deleteAddressLiveData.toCommonStateFlow()

    fun deleteCartItem(id: String) {
        viewModelScope.launch {
            deleteItemFromCart.deleteItemFromCart(id).collect {
                _deleteAddressLiveData.emit(it)
            }
        }
    }
}