package com.jar.app.feature_goal_based_saving.shared.domain.use_cases.impl

import com.jar.app.feature_goal_based_saving.shared.data.model.DailyInvestmentStatus
import com.jar.app.feature_goal_based_saving.shared.data.repository.GoalBasedSavingRepository
import com.jar.app.feature_goal_based_saving.shared.domain.use_cases.UpdateGoalDailyRecurringAmountUseCase
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

internal class UpdateGoalDailyInvestmentStatusUseCaseImpl constructor(
    private val goalBasedSavingRepository: GoalBasedSavingRepository
): UpdateGoalDailyRecurringAmountUseCase {
    override suspend fun execute(amount: Float): Flow<RestClientResult<ApiResponseWrapper<DailyInvestmentStatus?>>> {
        return goalBasedSavingRepository.updateDailyGoalRecurringAmount(amount)
    }
}