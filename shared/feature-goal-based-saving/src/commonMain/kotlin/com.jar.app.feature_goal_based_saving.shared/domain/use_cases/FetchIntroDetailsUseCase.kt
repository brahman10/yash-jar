package com.jar.app.feature_goal_based_saving.shared.domain.use_cases

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_goal_based_saving.shared.data.model.GoalSavingsIntoPage
import com.jar.app.feature_goal_based_saving.shared.data.model.MergeGoalResponse
import kotlinx.coroutines.flow.Flow

interface FetchIntroDetailsUseCase {
    suspend fun execute(): Flow<RestClientResult<ApiResponseWrapper<GoalSavingsIntoPage>>>
}