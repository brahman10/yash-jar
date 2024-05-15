package com.jar.app.feature_health_insurance.shared.domain.use_cases.impl

import com.jar.app.feature_health_insurance.shared.data.models.manage_screen.ManageScreenData
import com.jar.app.feature_health_insurance.shared.domain.repository.HealthInsuranceRepository
import com.jar.app.feature_health_insurance.shared.domain.use_cases.FetchManageScreenDataUseCase
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

internal class FetchManageScreenDataUseCaseImpl(
    private val healthInsuranceRepository: HealthInsuranceRepository
) : FetchManageScreenDataUseCase {
    override suspend fun fetchManageScreenData(insuranceId: String): Flow<RestClientResult<ApiResponseWrapper<ManageScreenData?>>> =
        healthInsuranceRepository.fetchManageScreenData(insuranceId)
}