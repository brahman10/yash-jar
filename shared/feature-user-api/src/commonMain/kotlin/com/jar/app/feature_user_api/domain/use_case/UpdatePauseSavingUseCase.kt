package com.jar.app.feature_user_api.domain.use_case

import com.jar.app.feature_user_api.domain.model.PauseSavingResponse
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface UpdatePauseSavingUseCase {
    suspend fun updatePauseSavingValue(
        shouldPause: Boolean,
        pauseDuration: String? = null,
        pauseType: String
    ): Flow<RestClientResult<ApiResponseWrapper<PauseSavingResponse>>>
}