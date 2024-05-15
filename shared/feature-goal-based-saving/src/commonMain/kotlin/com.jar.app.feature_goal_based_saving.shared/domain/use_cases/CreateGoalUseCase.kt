package com.jar.app.feature_goal_based_saving.shared.domain.use_cases

import com.jar.app.feature_goal_based_saving.shared.data.model.AbandonedScreenResponse
import com.jar.app.feature_goal_based_saving.shared.data.model.CreateGoalRequest
import com.jar.app.feature_goal_based_saving.shared.data.model.CreateGoalResponse
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface CreateGoalUseCase {
    suspend fun execute(createGoalRequest: CreateGoalRequest): Flow<RestClientResult<ApiResponseWrapper<CreateGoalResponse>>>
}