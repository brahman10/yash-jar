package com.jar.app.feature_gold_delivery.shared.ui.store_item.list

import com.jar.app.feature_gold_delivery.shared.domain.model.GoldDeliveryPlaceOrderDataRequest
import com.jar.app.feature_gold_delivery.shared.domain.model.OrderStatusAPIResponse
import com.jar.app.feature_gold_delivery.shared.domain.use_case.FetchOrderStatusUseCase
import com.jar.app.feature_gold_delivery.shared.domain.use_case.PostOrderUseCase
import com.jar.app.feature_one_time_payments.shared.data.model.base.InitiatePaymentResponse
import com.jar.internal.library.jar_core_kmm_flow.CFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class CompletePaymentFragmentViewModel   constructor(
    private val goldDeliveryPaymentUseCase: FetchOrderStatusUseCase,
    private val goldDeliveryPostOrder: PostOrderUseCase,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)


    private val _orderStatusLiveData =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<OrderStatusAPIResponse?>>>(RestClientResult.none())
    val orderStatusLiveData: CFlow<RestClientResult<ApiResponseWrapper<OrderStatusAPIResponse?>>>
        get() = _orderStatusLiveData.toCommonFlow()

    private val _placeOrderAPILiveData =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<InitiatePaymentResponse?>>>()
    val placeOrderAPILiveData: CFlow<RestClientResult<ApiResponseWrapper<InitiatePaymentResponse?>>>
        get() = _placeOrderAPILiveData.toCommonFlow()

    fun fetchOrderStatus(orderId: String) {
        viewModelScope.launch {
            goldDeliveryPaymentUseCase.fetchOrderStatus(orderId).collect {
                _orderStatusLiveData.emit(it)
            }
        }
    }

    fun placeOrder(placeOrderRequest: GoldDeliveryPlaceOrderDataRequest?) {
        placeOrderRequest ?: return
        viewModelScope.launch {
            goldDeliveryPostOrder.postOrder(placeOrderRequest).collectLatest {
                _placeOrderAPILiveData.emit(it)
            }
        }
    }

    var pollingCounter = 0

}