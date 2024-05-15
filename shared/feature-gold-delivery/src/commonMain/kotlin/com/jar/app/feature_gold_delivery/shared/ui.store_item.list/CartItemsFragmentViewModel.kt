package com.jar.app.feature_gold_delivery.shared.ui.store_item.list

import com.jar.app.feature_gold_delivery.shared.domain.model.CartAPIData
import com.jar.app.feature_gold_delivery.shared.domain.use_case.DeleteItemToCartUseCase
import com.jar.app.feature_gold_delivery.shared.domain.use_case.FetchCartUseCase
import com.jar.internal.library.jar_core_kmm_flow.CStateFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonStateFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow

import kotlinx.coroutines.launch



class CartItemsFragmentViewModel   constructor(
    private val deleteItemUseCase: DeleteItemToCartUseCase,
    private val fetchCartUseCase: FetchCartUseCase,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)


    private val _deleteAddressLiveData =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<Unit?>>>(RestClientResult.none())
    val deleteAddressLiveData: CStateFlow<RestClientResult<ApiResponseWrapper<Unit?>>>
        get() = _deleteAddressLiveData.toCommonStateFlow()

    private val _fetchCartLiveData =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<CartAPIData?>>>(RestClientResult.none())
    val fetchCartLiveData: CStateFlow<RestClientResult<ApiResponseWrapper<CartAPIData?>>>
        get() = _fetchCartLiveData.toCommonStateFlow()

    fun deleteCartItem(id: String) {
        viewModelScope.launch {
            deleteItemUseCase.deleteItemFromCart(id).collect {
                if (it.status == RestClientResult.Status.SUCCESS) {
                    getCartItems()
                }
                _deleteAddressLiveData.emit(it)
            }
        }
    }

    fun getCartItems() {
        viewModelScope.launch {
            fetchCartUseCase.fetchCart().collect {
                _fetchCartLiveData.emit(it)
            }
        }
    }
}