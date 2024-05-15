package com.jar.app.feature_health_insurance.shared.domain.use_cases.impl

import com.jar.app.feature_health_insurance.shared.data.models.InitiateInsurancePlanResponse
import com.jar.app.feature_health_insurance.shared.domain.repository.HealthInsuranceRepository
import com.jar.app.feature_health_insurance.shared.domain.use_cases.InitiateInsurancePlanUseCase
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

internal class InitiateInsurancePlanUseCaseImpl constructor(
    private val healthInsuranceRepository: HealthInsuranceRepository
): InitiateInsurancePlanUseCase {

    override suspend fun initiateInsurancePlan(maxAge: Int, adultCnt: Int, kidCnt: Int): Flow<RestClientResult<ApiResponseWrapper<InitiateInsurancePlanResponse>>> {
        return healthInsuranceRepository.initiateInsurancePlanResponse(maxAge, adultCnt, kidCnt)
    }

}