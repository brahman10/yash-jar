package com.jar.app.feature_weekly_magic_common.shared.domain.usecase

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface MarkWeeklyChallengeOnBoardedUseCase {
    suspend fun markWeeklyChallengeOnBoarded(): Flow<RestClientResult<ApiResponseWrapper<Unit?>>>
}