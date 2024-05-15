package com.jar.app.feature_weekly_magic_common.shared.domain.usecase

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface MarkWeeklyChallengeViewedUseCase {

    suspend fun markCurrentWeeklyChallengeViewed(challengeId:String): Flow<RestClientResult<ApiResponseWrapper<Unit?>>>

    suspend fun markPreviousWeeklyChallengeViewed(challengeId:String): Flow<RestClientResult<ApiResponseWrapper<Unit?>>>

    suspend fun markPreviousWeeklyChallengeStoryViewed(challengeId:String): Flow<RestClientResult<ApiResponseWrapper<Unit?>>>

}