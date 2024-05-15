package com.jar.app.feature_gold_delivery.shared.ui.store_item.list

import com.jar.app.feature_gold_delivery.shared.domain.model.AddCartItemRequest
import com.jar.app.feature_gold_delivery.shared.domain.model.ProductsV2
import com.jar.app.feature_gold_delivery.shared.domain.use_case.AddItemToCartWithFlowUseCase
import com.jar.app.feature_gold_delivery.shared.domain.use_case.FetchOrderDetailUseCase
import com.jar.app.feature_gold_delivery.shared.domain.use_case.GetAllStoreItemsUseCase
import com.jar.app.feature_transaction.shared.domain.model.NewTransactionDetails
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



class CartOrderDetailFragmentViewModel   constructor(
    private val updateCartUseCase: AddItemToCartWithFlowUseCase,
    private val getAllStoreItemsUseCase: GetAllStoreItemsUseCase,
    private val fetchCartUseCase: FetchOrderDetailUseCase,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)


    private val _storeItemsLiveData =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<ProductsV2?>>>(RestClientResult.none())
    val storeItemsLiveData: CStateFlow<RestClientResult<ApiResponseWrapper<ProductsV2?>>>
        get() = _storeItemsLiveData.toCommonStateFlow()

    private val _currentCartLiveData =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<NewTransactionDetails?>>>(RestClientResult.none())
    val currentCartLiveData: CStateFlow<RestClientResult<ApiResponseWrapper<NewTransactionDetails?>>>
        get() = _currentCartLiveData.toCommonStateFlow()

    private val _addToCartLiveData =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<Unit?>>>(RestClientResult.none())
    val addToCartLiveData: CStateFlow<RestClientResult<ApiResponseWrapper<Unit?>>>
        get() = _addToCartLiveData.toCommonStateFlow()

    fun fetchOrderDetails(orderId: String, assetSourceType: String, assetTxnId: String) {
        viewModelScope.launch {
            fetchCartUseCase.fetchOrderDetail(orderId, assetSourceType, assetTxnId).collectLatest {
                _currentCartLiveData.emit(it)
            }
        }
    }

    fun addToCart(addItemCartItemRequest: AddCartItemRequest) {
        viewModelScope.launch {
            updateCartUseCase.addItemToCart(addItemCartItemRequest).collectLatest {
                _addToCartLiveData.value = it
            }
        }
    }

    fun fetchProducts() {
        viewModelScope.launch {
            getAllStoreItemsUseCase.getAllStoreItems(null).collect {
                _storeItemsLiveData.emit(it)
            }
        }
    }
}