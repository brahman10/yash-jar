package com.jar.app.feature_savings_common.shared.domain.use_case.impl

import com.jar.app.feature_savings_common.shared.data.repository.SavingsCommonRepository
import com.jar.app.feature_savings_common.shared.domain.use_case.FetchGoalBasedSavingSettingUseCase
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

internal class FetchGoalBasedSavingSettingUseCaseImpl constructor(
    private val savingsCommonRepository: SavingsCommonRepository
): FetchGoalBasedSavingSettingUseCase {
    override suspend fun fetchGoalBasedSavingSettingScreenData(): Flow<RestClientResult<ApiResponseWrapper<com.jar.app.feature_savings_common.shared.domain.model.GoalBasedSavingDetails>>> {
        return savingsCommonRepository.fetchGoalBasedSavingSettings()
    }
}