package com.jar.app.feature_jar_duo.shared.domain.use_case

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface RenameGroupUseCase {
    suspend fun renameGroup(groupId: String, groupName: String): Flow<RestClientResult<ApiResponseWrapper<Unit?>>>
}