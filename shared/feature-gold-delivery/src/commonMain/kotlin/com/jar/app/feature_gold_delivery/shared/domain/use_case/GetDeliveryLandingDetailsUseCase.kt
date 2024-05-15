package com.jar.app.feature_gold_delivery.shared.domain.use_case

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_gold_delivery.shared.domain.model.DeliveryLandingData
import kotlinx.coroutines.flow.Flow

interface GetDeliveryLandingDetailsUseCase {
    suspend fun getDeliveryLandingScreenDetails(): Flow<RestClientResult<ApiResponseWrapper<DeliveryLandingData?>>>
}