package com.jar.app.feature_health_insurance.shared.domain.use_cases.impl

import com.jar.app.feature_health_insurance.shared.data.models.CreateProposalRequest
import com.jar.app.feature_health_insurance.shared.data.models.CreateProposalResponse
import com.jar.app.feature_health_insurance.shared.domain.repository.HealthInsuranceRepository
import com.jar.app.feature_health_insurance.shared.domain.use_cases.CreateProposalUseCase
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

internal class CreateProposalUseCaseImpl constructor(
    private val healthInsuranceRepository: HealthInsuranceRepository
): CreateProposalUseCase {
    override suspend fun createProposal(createProposalRequest: CreateProposalRequest): Flow<RestClientResult<ApiResponseWrapper<CreateProposalResponse?>>> {
        return healthInsuranceRepository.createProposal(createProposalRequest)
    }
}