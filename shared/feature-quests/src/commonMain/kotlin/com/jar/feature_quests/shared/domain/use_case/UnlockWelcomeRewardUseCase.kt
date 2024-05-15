package com.jar.feature_quests.shared.domain.use_case

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface UnlockWelcomeRewardUseCase {
    suspend fun unlockWelcomeReward(): Flow<RestClientResult<ApiResponseWrapper<Unit?>>>
}