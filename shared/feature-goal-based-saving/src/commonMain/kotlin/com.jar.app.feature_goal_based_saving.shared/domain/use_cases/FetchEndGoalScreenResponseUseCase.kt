package com.jar.app.feature_goal_based_saving.shared.domain.use_cases

import com.jar.app.feature_goal_based_saving.shared.data.model.EndScreenResponse
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface FetchEndGoalScreenResponseUseCase {
    suspend fun execute(): Flow<RestClientResult<ApiResponseWrapper<EndScreenResponse>>>
}