package com.jar.app.feature_gold_delivery.shared.domain.use_case

import com.jar.app.feature_gold_delivery.impl.util.GoldDeliveryConstants
import com.jar.app.feature_gold_delivery.shared.domain.model.GoldDeliveryFaq
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface GetStoreItemFaqUseCase {
    suspend fun getStoreItemFaq(type: String = GoldDeliveryConstants.GOLD_DELIVERY_FAQ): Flow<RestClientResult<ApiResponseWrapper<GoldDeliveryFaq?>>>
}