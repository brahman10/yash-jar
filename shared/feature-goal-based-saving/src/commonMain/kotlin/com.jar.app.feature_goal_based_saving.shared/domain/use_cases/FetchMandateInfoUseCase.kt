package com.jar.app.feature_goal_based_saving.shared.domain.use_cases

import com.jar.app.feature_goal_based_saving.shared.data.model.MandateInfo
import com.jar.app.feature_goal_based_saving.shared.data.model.MergeGoalResponse
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface FetchMandateInfoUseCase {
    suspend fun execute(amount: Int, savingsType: String): Flow<RestClientResult<ApiResponseWrapper<MandateInfo>>>
}