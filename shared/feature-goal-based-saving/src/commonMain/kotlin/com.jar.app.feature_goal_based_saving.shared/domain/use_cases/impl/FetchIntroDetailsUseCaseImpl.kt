package com.jar.app.feature_goal_based_saving.shared.domain.use_cases.impl

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_goal_based_saving.shared.data.model.GoalSavingsIntoPage
import com.jar.app.feature_goal_based_saving.shared.data.repository.GoalBasedSavingRepository
import com.jar.app.feature_goal_based_saving.shared.domain.use_cases.FetchIntroDetailsUseCase
import kotlinx.coroutines.flow.Flow

internal class FetchIntroDetailsUseCaseImpl constructor(
    private val goalBasedSavingRepository: GoalBasedSavingRepository
): FetchIntroDetailsUseCase {
    override suspend fun execute(): Flow<RestClientResult<ApiResponseWrapper<GoalSavingsIntoPage>>> {
        return goalBasedSavingRepository.fetchGoalIntroPageStaticResponse()
    }
}