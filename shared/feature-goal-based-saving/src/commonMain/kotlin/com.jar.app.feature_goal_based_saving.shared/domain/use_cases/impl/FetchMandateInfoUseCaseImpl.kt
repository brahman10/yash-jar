package com.jar.app.feature_goal_based_saving.shared.domain.use_cases.impl

import com.jar.app.feature_goal_based_saving.shared.data.model.MandateInfo
import com.jar.app.feature_goal_based_saving.shared.data.repository.GoalBasedSavingRepository
import com.jar.app.feature_goal_based_saving.shared.domain.use_cases.FetchMandateInfoUseCase
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

internal class FetchMandateInfoUseCaseImpl constructor(
    private val goalBasedSavingRepository: GoalBasedSavingRepository
): FetchMandateInfoUseCase {
    override suspend fun execute(amount: Int, savingsType: String): Flow<RestClientResult<ApiResponseWrapper<MandateInfo>>> {
        return goalBasedSavingRepository.fetchMandateResposnse(amount, savingsType)
    }

}