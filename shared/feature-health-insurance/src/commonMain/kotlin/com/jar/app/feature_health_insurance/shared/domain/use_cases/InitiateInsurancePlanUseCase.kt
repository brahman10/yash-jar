package com.jar.app.feature_health_insurance.shared.domain.use_cases

import com.jar.app.feature_health_insurance.shared.data.models.InitiateInsurancePlanResponse
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface InitiateInsurancePlanUseCase {
    suspend fun initiateInsurancePlan(maxCnt: Int, adultCnt: Int, kidCnt: Int): Flow<RestClientResult<ApiResponseWrapper<InitiateInsurancePlanResponse>>>
}