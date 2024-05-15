package com.jar.app.feature.home.domain.usecase

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface UpdateFcmTokenUseCase {

    suspend fun updateFcmToken(
        fcmToken: String,
        instanceId: String?
    ): Flow<RestClientResult<ApiResponseWrapper<Unit?>>>
}