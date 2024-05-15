package com.jar.app.feature_story.domain.repository

import com.jar.app.feature_story.data.InAppStoryRepository
import com.jar.app.feature_story.data.model.InAppStoryModel
import com.jar.app.feature_story.data.network.InAppStoryDataSource
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

internal class InAppStoryRepositoryImpl constructor(
    private val inAppStoryDataSource: InAppStoryDataSource
): InAppStoryRepository {
    override suspend fun fetchStories(): Flow<RestClientResult<ApiResponseWrapper<InAppStoryModel>>> {
        return getFlowResult { inAppStoryDataSource.getInAppStories() }
    }

    override suspend fun updateUserAction(
        userActionType: String,
        action: Boolean,
        pageId: String,
        timeSpent: Long?
    ): Flow<RestClientResult<ApiResponseWrapper<Unit?>>> {
        return getFlowResult {
            inAppStoryDataSource.updateUserAction(
            userActionType,
            action,
            pageId,
            timeSpent
        ) }
    }

    override suspend fun fetchPageByPageId(pageId: String): Flow<RestClientResult<ApiResponseWrapper<InAppStoryModel>>> {
        return getFlowResult { inAppStoryDataSource.fetchPageByStoryId(
            pageId
        )}
    }

}