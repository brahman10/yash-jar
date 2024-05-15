package com.jar.app.feature_gold_delivery.shared.domain.use_case

import com.jar.app.feature_gold_delivery.shared.domain.model.AddCartItemRequest
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

// UseCase for adding item to cart with Flow
interface AddItemToCartWithFlowUseCase {
    suspend fun addItemToCart(addCartItemRequest: AddCartItemRequest): Flow<RestClientResult<ApiResponseWrapper<Unit?>>>
}