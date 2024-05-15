package com.jar.app.feature_onboarding.shared.domain.usecase

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_onboarding.shared.domain.model.SavingGoalsResponse
import com.jar.app.feature_onboarding.shared.domain.model.SavingGoalsV2Response
import kotlinx.coroutines.flow.Flow

interface FetchSavingGoalsV2UseCase {
    suspend fun fetchSavingGoals(): Flow<RestClientResult<ApiResponseWrapper<SavingGoalsV2Response>>>
}