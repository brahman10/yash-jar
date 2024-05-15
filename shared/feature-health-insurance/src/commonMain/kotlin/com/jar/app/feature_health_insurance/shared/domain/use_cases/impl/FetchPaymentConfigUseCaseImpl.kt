package com.jar.app.feature_health_insurance.shared.domain.use_cases.impl

import com.jar.app.feature_health_insurance.shared.domain.repository.HealthInsuranceRepository
import com.jar.app.feature_health_insurance.shared.domain.use_cases.FetchPaymentConfigUseCase

internal class FetchPaymentConfigUseCaseImpl constructor(
    private val healthInsuranceRepository: HealthInsuranceRepository
): FetchPaymentConfigUseCase {
    override suspend fun fetchPaymentConfig(insuranceId: String) =
        healthInsuranceRepository.fetchPaymentConfig(insuranceId)
}