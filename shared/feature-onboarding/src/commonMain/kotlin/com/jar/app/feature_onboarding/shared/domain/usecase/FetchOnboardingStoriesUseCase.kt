package com.jar.app.feature_onboarding.shared.domain.usecase

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface FetchOnboardingStoriesUseCase {

    suspend fun fetchOnboardingStories(): Flow<RestClientResult<ApiResponseWrapper<com.jar.app.feature_onboarding.shared.domain.model.OnboardingStoryData>>>

}