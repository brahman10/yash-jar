package com.jar.app.feature_gold_delivery.shared.domain.use_case

import com.jar.app.feature_gold_delivery.shared.domain.model.ProductsV2
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface GetAllStoreItemsUseCase {
    suspend fun getAllStoreItems(category: String?): Flow<RestClientResult<ApiResponseWrapper<ProductsV2?>>>
}