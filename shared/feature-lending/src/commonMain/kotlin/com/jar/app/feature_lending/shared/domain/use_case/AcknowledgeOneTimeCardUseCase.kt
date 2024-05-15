package com.jar.app.feature_lending.shared.domain.use_case

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface AcknowledgeOneTimeCardUseCase {

    suspend fun acknowledgeOneTimeCard(cardType: String): Flow<RestClientResult<ApiResponseWrapper<Boolean?>>>
}