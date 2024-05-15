package com.jar.app.feature_goal_based_saving.shared.domain.use_cases.impl

import com.jar.app.feature_goal_based_saving.shared.data.model.GoalDurationResponse
import com.jar.app.feature_goal_based_saving.shared.data.repository.GoalBasedSavingRepository
import com.jar.app.feature_goal_based_saving.shared.domain.use_cases.FetchGoalDurationUseCase
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

internal class FetchGoalDurationUseCaseImpl constructor(
    private val goalBasedSavingRepository: GoalBasedSavingRepository
): FetchGoalDurationUseCase {
    override suspend fun getGoalDurationData(amount: Int): Flow<RestClientResult<ApiResponseWrapper<GoalDurationResponse>>> {
        return goalBasedSavingRepository.fetchGoalDurationScreenDetails(amount)
    }
}