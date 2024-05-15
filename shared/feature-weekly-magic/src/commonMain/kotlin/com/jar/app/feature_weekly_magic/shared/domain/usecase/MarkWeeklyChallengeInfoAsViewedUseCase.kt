package com.jar.app.feature_weekly_magic.shared.domain.usecase

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface MarkWeeklyChallengeInfoAsViewedUseCase {

    suspend fun markWeeklyChallengeInfoAsViewed(challengeId: String): Flow<RestClientResult<ApiResponseWrapper<Unit?>>>

}