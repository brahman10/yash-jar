package com.jar.app.feature_story.domain.use_cases.impl

import com.jar.app.feature_story.data.InAppStoryRepository
import com.jar.app.feature_story.domain.use_cases.UpdateUserActionUseCase
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

internal class UpdateUserActionUseCaseImpl constructor(
    private val inAppStoryRepository: InAppStoryRepository
): UpdateUserActionUseCase {
    override suspend fun updateUserActionUseCase(
        userActionType: String,
        action: Boolean,
        pageId: String,
        timeSpent: Long?
    ): Flow<RestClientResult<ApiResponseWrapper<Unit?>>> {
        return inAppStoryRepository.updateUserAction(userActionType, action, pageId, timeSpent)
    }
}