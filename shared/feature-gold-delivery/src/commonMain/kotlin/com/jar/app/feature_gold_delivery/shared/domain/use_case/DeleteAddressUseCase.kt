package com.jar.app.feature_gold_delivery.shared.domain.use_case

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface DeleteAddressUseCase {

    suspend fun deleteAddress(id: String): Flow<RestClientResult<ApiResponseWrapper<Unit?>>>

}