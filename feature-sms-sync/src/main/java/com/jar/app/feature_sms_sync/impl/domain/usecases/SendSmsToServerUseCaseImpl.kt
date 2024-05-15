package com.jar.app.feature_sms_sync.impl.domain.usecases

import com.jar.app.feature_sms_sync.impl.data.repository.ISmsSyncRepository
import com.jar.app.feature_sms_sync.impl.domain.model.SmsSyncRequest
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

internal class SendSmsToServerUseCaseImpl(
    private val repository: ISmsSyncRepository
) : ISendSmsToServerUseCase {
    override suspend fun sendSmsToServer(smsSyncRequest: SmsSyncRequest): Flow<RestClientResult<ApiResponseWrapper<Unit?>>> {
        return repository.postSmsToServer(smsSyncRequest)
    }
}