package com.jar.app.feature_gold_delivery.shared.domain.use_case

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_user_api.domain.model.Address
import kotlinx.coroutines.flow.Flow

interface AddDeliveryAddressUseCase {

    suspend fun addDeliveryAddress(address: Address): Flow<RestClientResult<ApiResponseWrapper<Address?>>>
}