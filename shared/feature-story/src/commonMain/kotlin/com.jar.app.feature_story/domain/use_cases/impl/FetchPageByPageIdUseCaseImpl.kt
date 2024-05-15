package com.jar.app.feature_story.domain.use_cases.impl

import com.jar.app.feature_story.data.InAppStoryRepository
import com.jar.app.feature_story.data.model.InAppStoryModel
import com.jar.app.feature_story.domain.use_cases.FetchInAppStoriesUseCase
import com.jar.app.feature_story.domain.use_cases.FetchPageByPageIdUseCase
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

internal class FetchPageByPageIdUseCaseImpl constructor(
    private val inAppStoryRepository: InAppStoryRepository
): FetchPageByPageIdUseCase {
    override suspend fun fetchStoryById(pageId: String): Flow<RestClientResult<ApiResponseWrapper<InAppStoryModel>>> {
        return inAppStoryRepository.fetchPageByPageId(pageId)
    }

}