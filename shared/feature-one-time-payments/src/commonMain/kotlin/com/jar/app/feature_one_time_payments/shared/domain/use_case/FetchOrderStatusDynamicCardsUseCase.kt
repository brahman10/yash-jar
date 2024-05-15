package com.jar.app.feature_one_time_payments.shared.domain.use_case

import com.jar.app.feature_one_time_payments.shared.data.model.DynamicCardsOrderType
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

/**
 * move to common place if possible
 */
interface FetchOrderStatusDynamicCardsUseCase {
    suspend fun fetchOrderStatusDynamicCards(
        orderType: DynamicCardsOrderType,
        orderId: String?
    ): Flow<RestClientResult<ApiResponseWrapper<Unit?>>>
}