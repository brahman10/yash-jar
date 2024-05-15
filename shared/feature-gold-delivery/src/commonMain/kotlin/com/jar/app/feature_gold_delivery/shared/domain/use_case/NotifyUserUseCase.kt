package com.jar.app.feature_gold_delivery.shared.domain.use_case

import com.jar.app.feature_gold_delivery.shared.domain.model.AddCartItemRequest
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface NotifyUserUseCase {
    suspend fun notifyUser(addCartItemRequest: AddCartItemRequest): Flow<RestClientResult<ApiResponseWrapper<Unit?>>>
}