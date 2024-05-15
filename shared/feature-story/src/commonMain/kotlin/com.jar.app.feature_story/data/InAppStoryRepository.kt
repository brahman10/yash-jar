package com.jar.app.feature_story.data

import com.jar.app.feature_story.data.model.InAppStoryModel
import com.jar.internal.library.jar_core_network.api.data.BaseRepository
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

internal interface InAppStoryRepository: BaseRepository {
    suspend fun fetchStories(): Flow<RestClientResult<ApiResponseWrapper<InAppStoryModel>>>
    suspend fun updateUserAction(
        userActionType: String,
        action: Boolean,
        pageId: String,
        timeSpent: Long?
    ): Flow<RestClientResult<ApiResponseWrapper<Unit?>>>
    suspend fun fetchPageByPageId(pageId: String): Flow<RestClientResult<ApiResponseWrapper<InAppStoryModel>>>
}