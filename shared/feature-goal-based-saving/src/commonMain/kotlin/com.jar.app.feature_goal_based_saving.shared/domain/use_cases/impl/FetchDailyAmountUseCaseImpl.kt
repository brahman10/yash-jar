package com.jar.app.feature_goal_based_saving.shared.domain.use_cases.impl

import com.jar.app.feature_goal_based_saving.shared.data.model.CalculateDailyAmountResponse
import com.jar.app.feature_goal_based_saving.shared.data.repository.GoalBasedSavingRepository
import com.jar.app.feature_goal_based_saving.shared.domain.use_cases.FetchDailyAmountUseCase
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

internal class FetchDailyAmountUseCaseImpl constructor(
    private val goalBasedSavingRepository: GoalBasedSavingRepository
): FetchDailyAmountUseCase {
    override suspend fun execute(
        amount: Int,
        duration: Int
    ): Flow<RestClientResult<ApiResponseWrapper<CalculateDailyAmountResponse>>> {
        return goalBasedSavingRepository.fetchDailyAmount(amount, duration)
    }

}