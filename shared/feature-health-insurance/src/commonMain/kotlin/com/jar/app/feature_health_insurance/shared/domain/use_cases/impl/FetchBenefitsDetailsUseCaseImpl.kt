package com.jar.app.feature_health_insurance.shared.domain.use_cases.impl

import com.jar.app.feature_health_insurance.shared.data.models.benefits.BenefitsDetailsResponse
import com.jar.app.feature_health_insurance.shared.domain.repository.HealthInsuranceRepository
import com.jar.app.feature_health_insurance.shared.domain.use_cases.FetchBenefitsDetailsUseCase
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

internal class FetchBenefitsDetailsUseCaseImpl constructor(
    private val healthInsuranceRepository: HealthInsuranceRepository
) : FetchBenefitsDetailsUseCase {

    override suspend fun fetchBenefitsDetails(insuranceId: String?): Flow<RestClientResult<ApiResponseWrapper<BenefitsDetailsResponse>>> =
        healthInsuranceRepository.fetchBenefitsDetails(insuranceId)
}