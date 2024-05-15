package com.jar.app.feature_goal_based_saving.shared.domain.use_cases

import com.jar.app.feature_goal_based_saving.shared.data.model.GoalEndRequest
import com.jar.app.feature_goal_based_saving.shared.data.model.GoalEndResponse
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface EndGoalUseCase {
    suspend fun execute(goalEndRequest: GoalEndRequest): Flow<RestClientResult<ApiResponseWrapper<GoalEndResponse>>>
}