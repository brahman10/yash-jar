package com.jar.app.feature_gold_delivery.shared.domain.use_case

import com.jar.app.feature_gold_delivery.shared.domain.model.OrderStatusAPIResponse
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface FetchOrderStatusUseCase {
    suspend fun fetchOrderStatus(orderId: String): Flow<RestClientResult<ApiResponseWrapper<OrderStatusAPIResponse?>>>
}