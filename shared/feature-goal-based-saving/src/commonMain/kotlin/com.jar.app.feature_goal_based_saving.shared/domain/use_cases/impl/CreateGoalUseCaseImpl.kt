package com.jar.app.feature_goal_based_saving.shared.domain.use_cases.impl

import com.jar.app.feature_goal_based_saving.shared.data.model.CreateGoalRequest
import com.jar.app.feature_goal_based_saving.shared.data.model.CreateGoalResponse
import com.jar.app.feature_goal_based_saving.shared.data.repository.GoalBasedSavingRepository
import com.jar.app.feature_goal_based_saving.shared.domain.use_cases.CreateGoalUseCase
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

internal class CreateGoalUseCaseImpl constructor(
    private val goalBasedSavingRepository: GoalBasedSavingRepository
): CreateGoalUseCase {
    override suspend fun execute(createGoalRequest: CreateGoalRequest): Flow<RestClientResult<ApiResponseWrapper<CreateGoalResponse>>> {
        return goalBasedSavingRepository.createGoal(createGoalRequest)
    }
}