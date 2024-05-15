package com.jar.app.feature_goal_based_saving.shared.domain.use_cases.impl

import com.jar.app.feature_goal_based_saving.shared.data.model.GoalStatusResponse
import com.jar.app.feature_goal_based_saving.shared.data.repository.GoalBasedSavingRepository
import com.jar.app.feature_goal_based_saving.shared.domain.use_cases.GetGoalTransactionScreenResponseUseCase
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

internal class GetGoalTransactionScreenResponseUseCaseImpl constructor(
    private val goalBasedSavingRepository: GoalBasedSavingRepository
): GetGoalTransactionScreenResponseUseCase {
    override suspend fun execute(goalId: String): Flow<RestClientResult<ApiResponseWrapper<GoalStatusResponse>>> {
        return goalBasedSavingRepository.getGoalTransactionResponse(
            goalId
        )
    }
}