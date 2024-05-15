package com.jar.app.feature_savings_common.shared.domain.use_case

import com.jar.app.feature_savings_common.shared.domain.model.GoalBasedSavingDetails
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface FetchGoalBasedSavingSettingUseCase {
    suspend fun fetchGoalBasedSavingSettingScreenData(): Flow<RestClientResult<ApiResponseWrapper<GoalBasedSavingDetails>>>
}