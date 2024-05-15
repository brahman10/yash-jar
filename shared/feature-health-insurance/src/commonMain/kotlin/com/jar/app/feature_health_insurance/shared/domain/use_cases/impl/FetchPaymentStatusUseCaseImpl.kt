package com.jar.app.feature_health_insurance.shared.domain.use_cases.impl

import com.jar.app.feature_health_insurance.shared.data.models.payment_status.PaymentStatusResponse
import com.jar.app.feature_health_insurance.shared.domain.repository.HealthInsuranceRepository
import com.jar.app.feature_health_insurance.shared.domain.use_cases.FetchPaymentStatusUseCase
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

internal class FetchPaymentStatusUseCaseImpl(
    private val healthInsuranceRepository: HealthInsuranceRepository
) :
    FetchPaymentStatusUseCase {
    override suspend fun fetchPaymentStatus(insuranceId: String): Flow<RestClientResult<ApiResponseWrapper<PaymentStatusResponse?>>> {
        return healthInsuranceRepository.fetchPaymentStatus(insuranceId)
    }
}