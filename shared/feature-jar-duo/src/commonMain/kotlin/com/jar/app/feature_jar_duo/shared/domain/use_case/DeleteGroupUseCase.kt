package com.jar.app.feature_jar_duo.shared.domain.use_case

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface DeleteGroupUseCase {
    suspend fun deleteGroup(groupId: String): Flow<RestClientResult<ApiResponseWrapper<Unit?>>>
}