package com.jar.app.feature_gold_delivery.shared.domain.use_case

import com.jar.app.feature_gold_delivery.shared.domain.model.GetWishlistAPIResponse
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult

interface GetProductsFromWishlistUseCase {
    suspend fun getProductsFromWishlist(page: Int, size: Int): RestClientResult<ApiResponseWrapper<GetWishlistAPIResponse?>>
}