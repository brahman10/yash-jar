package com.jar.app.feature_weekly_magic_common.shared.domain.usecase

import com.jar.app.feature_weekly_magic_common.shared.domain.model.WeeklyChallengeDetail
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface FetchWeeklyChallengeDetailUseCase {

    suspend fun fetchWeeklyChallengeDetailForToday(): Flow<RestClientResult<ApiResponseWrapper<WeeklyChallengeDetail?>>>

    suspend fun fetchWeeklyChallengeDetailById(challengeId: String): Flow<RestClientResult<ApiResponseWrapper<WeeklyChallengeDetail?>>>

}