package com.jar.app.feature_health_insurance.shared.domain.use_cases.impl

import com.jar.app.feature_health_insurance.shared.data.models.select_premium.SelectPremiumResponse
import com.jar.app.feature_health_insurance.shared.domain.repository.HealthInsuranceRepository
import com.jar.app.feature_health_insurance.shared.domain.use_cases.FetchInsurancePlansUseCase
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

internal class FetchInsurancePlansUseCaseImpl constructor(
    private val healthInsuranceRepository: HealthInsuranceRepository
): FetchInsurancePlansUseCase {

    override suspend fun fetchInsurancePlans(orderId: String): Flow<RestClientResult<ApiResponseWrapper<SelectPremiumResponse>>> =
        healthInsuranceRepository.fetchInsurancePlans(orderId)

}