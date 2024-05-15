package com.jar.app.feature_health_insurance.shared.domain.use_cases.impl

import com.jar.app.feature_health_insurance.shared.data.models.transaction_details.InsuranceTransactionDetails
import com.jar.app.feature_health_insurance.shared.domain.repository.HealthInsuranceRepository
import com.jar.app.feature_health_insurance.shared.domain.use_cases.FetchInsuranceTransactionDetailsUseCase
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

internal class FetchInsuranceTransactionDetailsUseCaseImpl(private val healthInsuranceRepository: HealthInsuranceRepository) :
    FetchInsuranceTransactionDetailsUseCase {
    override suspend fun fetchInsuranceTransactionDetails(transactionId: String): Flow<RestClientResult<ApiResponseWrapper<InsuranceTransactionDetails>>> =
        healthInsuranceRepository.fetchInsuranceTransactionDetails(transactionId)
}