package com.jar.app.feature_onboarding.shared.domain.usecase

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_onboarding.shared.domain.model.SavingGoalPostRequest
import kotlinx.coroutines.flow.Flow

interface PostSavingGoalsUseCase {
    suspend fun postSavingGoals(
        reasonForSavingsRequest: com.jar.app.feature_onboarding.shared.domain.model.SavingGoalPostRequest
    ): Flow<RestClientResult<ApiResponseWrapper<Unit?>>>
}