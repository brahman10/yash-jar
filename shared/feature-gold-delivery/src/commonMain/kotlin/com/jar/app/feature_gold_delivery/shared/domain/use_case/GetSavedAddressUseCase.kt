package com.jar.app.feature_gold_delivery.shared.domain.use_case

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_user_api.domain.model.Addresses
import kotlinx.coroutines.flow.Flow

interface GetSavedAddressUseCase {

    suspend fun getSavedAddress(): Flow<RestClientResult<ApiResponseWrapper<Addresses?>>>
}