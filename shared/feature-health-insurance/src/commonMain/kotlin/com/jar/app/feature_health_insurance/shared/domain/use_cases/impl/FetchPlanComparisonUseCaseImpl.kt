package com.jar.app.feature_health_insurance.shared.domain.use_cases.impl

import com.jar.app.feature_health_insurance.shared.data.models.plan_comparison.PlanComparisonResponse
import com.jar.app.feature_health_insurance.shared.domain.repository.HealthInsuranceRepository
import com.jar.app.feature_health_insurance.shared.domain.use_cases.FetchPlanComparisonUseCase
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

internal class FetchPlanComparisonUseCaseImpl(
    private val healthInsuranceRepository: HealthInsuranceRepository
) : FetchPlanComparisonUseCase {
    override suspend fun fetchPlanComparisons(providerId: String): Flow<RestClientResult<ApiResponseWrapper<PlanComparisonResponse>>> {
        return healthInsuranceRepository.fetchPlanComparisons(providerId)
    }
}