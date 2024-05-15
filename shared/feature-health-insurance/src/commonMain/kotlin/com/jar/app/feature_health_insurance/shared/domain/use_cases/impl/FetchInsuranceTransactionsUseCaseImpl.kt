package com.jar.app.feature_health_insurance.shared.domain.use_cases.impl

import com.jar.app.feature_health_insurance.shared.data.models.manage_screen.InsuranceTransactionsData
import com.jar.app.feature_health_insurance.shared.domain.repository.HealthInsuranceRepository
import com.jar.app.feature_health_insurance.shared.domain.use_cases.FetchInsuranceTransactionsUseCase
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult

internal class FetchInsuranceTransactionsUseCaseImpl(private val healthInsuranceRepository: HealthInsuranceRepository) :
    FetchInsuranceTransactionsUseCase {
    override suspend fun fetchInsuranceTransactions(
        insuranceId: String,
        page: Int,
        size: Int
    ): RestClientResult<ApiResponseWrapper<InsuranceTransactionsData?>> =
        healthInsuranceRepository.fetchInsuranceTransactions(insuranceId, page, size)
}