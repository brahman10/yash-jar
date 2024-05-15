package com.jar.app.feature_story.domain.use_cases

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface UpdateUserActionUseCase {
    suspend fun updateUserActionUseCase(
        userActionType: String,
        action: Boolean,
        pageId: String,
        timeSpent: Long?
    ): Flow<RestClientResult<ApiResponseWrapper<Unit?>>>
}