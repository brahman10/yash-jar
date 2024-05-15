package com.jar.app.feature_gold_delivery.shared.domain.use_case

import com.jar.app.feature_gold_delivery.shared.domain.model.AddCartItemRequest
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult

// UseCase for adding item to cart without Flow
interface AddItemToCartWithoutFlowUseCase {
    suspend fun addItemToCartWithoutFlow(addCartItemRequest: AddCartItemRequest): RestClientResult<ApiResponseWrapper<Unit?>>
}