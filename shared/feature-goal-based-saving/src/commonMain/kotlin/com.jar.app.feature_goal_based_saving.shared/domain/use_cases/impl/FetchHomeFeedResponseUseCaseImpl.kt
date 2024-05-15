package com.jar.app.feature_goal_based_saving.shared.domain.use_cases.impl

import com.jar.app.feature_goal_based_saving.shared.data.model.HomefeedGoalProgressReponse
import com.jar.app.feature_goal_based_saving.shared.data.repository.GoalBasedSavingRepository
import com.jar.app.feature_goal_based_saving.shared.domain.use_cases.FetchHomeFeedResponseUseCase
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

internal class FetchHomeFeedResponseUseCaseImpl(
    private val goalBasedSavingRepository: GoalBasedSavingRepository
): FetchHomeFeedResponseUseCase {
    override suspend fun fetchHomeFeedResponse(): Flow<RestClientResult<ApiResponseWrapper<HomefeedGoalProgressReponse>>> {
        return goalBasedSavingRepository.fetchHomefeedScreen()
    }
}