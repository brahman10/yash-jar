package com.jar.app.feature_story.domain.use_cases

import com.jar.app.feature_story.data.model.InAppStoryModel
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface FetchInAppStoriesUseCase {
    suspend fun fetchInAppStories(): Flow<RestClientResult<ApiResponseWrapper<InAppStoryModel>>>
}