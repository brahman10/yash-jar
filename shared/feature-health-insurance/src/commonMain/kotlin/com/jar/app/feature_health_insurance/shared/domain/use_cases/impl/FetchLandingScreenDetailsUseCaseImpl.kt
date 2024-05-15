package com.jar.app.feature_health_insurance.shared.domain.use_cases.impl

import com.jar.app.feature_health_insurance.shared.data.models.landing1.LandingPageResponse1
import com.jar.app.feature_health_insurance.shared.domain.repository.HealthInsuranceRepository
import com.jar.app.feature_health_insurance.shared.domain.use_cases.FetchLandingScreenDetailsUseCase
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

internal class FetchLandingScreenDetailsUseCaseImpl(
    private val healthInsuranceRepository: HealthInsuranceRepository
): FetchLandingScreenDetailsUseCase {

    override suspend fun fetchLandingScreenDetails(): Flow<RestClientResult<ApiResponseWrapper<LandingPageResponse1>>> =
        healthInsuranceRepository.fetchLandingScreenDetails()

}