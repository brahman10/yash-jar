package com.jar.app.feature_contact_sync_common.shared.domain.usecases

import com.jar.app.feature_contact_sync_common.shared.domain.model.ProcessInviteRequest
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface ProcessInviteUseCase {
    suspend fun processInvite(processInviteRequest: ProcessInviteRequest): Flow<RestClientResult<ApiResponseWrapper<Unit?>>>
}