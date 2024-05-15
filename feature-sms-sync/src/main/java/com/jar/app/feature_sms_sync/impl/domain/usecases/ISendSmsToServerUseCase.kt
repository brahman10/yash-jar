package com.jar.app.feature_sms_sync.impl.domain.usecases

import com.jar.app.feature_sms_sync.impl.domain.model.SmsSyncRequest
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

internal interface ISendSmsToServerUseCase {
    suspend fun sendSmsToServer(smsSyncRequest: SmsSyncRequest): Flow<RestClientResult<ApiResponseWrapper<Unit?>>>
}