package com.jar.app.feature_sms_sync.impl.data.repository

import com.jar.app.feature_sms_sync.impl.domain.model.SmsSyncRequest
import com.jar.internal.library.jar_core_network.api.data.BaseRepository
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

internal interface ISmsSyncRepository : BaseRepository {
    suspend fun postSmsToServer(smsSyncRequest: SmsSyncRequest): Flow<RestClientResult<ApiResponseWrapper<Unit?>>>
}