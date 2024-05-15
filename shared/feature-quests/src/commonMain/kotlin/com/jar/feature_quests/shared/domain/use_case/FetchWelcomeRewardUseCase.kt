package com.jar.feature_quests.shared.domain.use_case

import com.jar.feature_quests.shared.domain.model.WelcomeRewardData
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface FetchWelcomeRewardUseCase {
    suspend fun fetchWelcomeReward(): Flow<RestClientResult<ApiResponseWrapper<WelcomeRewardData?>>>
}


