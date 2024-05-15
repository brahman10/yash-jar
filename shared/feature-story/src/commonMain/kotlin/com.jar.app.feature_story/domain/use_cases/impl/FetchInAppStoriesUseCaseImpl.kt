package com.jar.app.feature_story.domain.use_cases.impl

import com.jar.app.feature_story.data.InAppStoryRepository
import com.jar.app.feature_story.data.model.InAppStoryModel
import com.jar.app.feature_story.domain.use_cases.FetchInAppStoriesUseCase
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

internal class FetchInAppStoriesUseCaseImpl constructor(
    private val inAppStoryRepository: InAppStoryRepository
): FetchInAppStoriesUseCase {
    override suspend fun fetchInAppStories(): Flow<RestClientResult<ApiResponseWrapper<InAppStoryModel>>> {
        return inAppStoryRepository.fetchStories()
    }

}