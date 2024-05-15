package com.jar.app.feature_health_insurance.shared.domain.use_cases.impl

import com.jar.app.feature_health_insurance.shared.data.models.add_details.AddDetailsScreenStaticDataResponse
import com.jar.app.feature_health_insurance.shared.domain.repository.HealthInsuranceRepository
import com.jar.app.feature_health_insurance.shared.domain.use_cases.FetchAddDetailsScreenStaticDataUseCase
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

internal class FetchAddDetailsScreenStaticDataUseCaseImpl constructor(
    private val healthInsuranceRepository: HealthInsuranceRepository
): FetchAddDetailsScreenStaticDataUseCase {

    override suspend fun fetchAddDetailsScreenStaticData(): Flow<RestClientResult<ApiResponseWrapper<AddDetailsScreenStaticDataResponse>>> {
        return healthInsuranceRepository.fetchAddDetailsScreenStaticData()
    }
}