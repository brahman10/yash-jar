package com.jar.app.feature_gold_delivery.shared.domain.use_case

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_gold_delivery.shared.domain.model.DeliverProductRequest
import com.jar.app.feature_one_time_payments_common.shared.DeliverProductResponse
import kotlinx.coroutines.flow.Flow

interface DeliverProductUseCase {

    suspend fun deliverProduct(deliverProductRequest: DeliverProductRequest): Flow<RestClientResult<ApiResponseWrapper<DeliverProductResponse?>>>

}