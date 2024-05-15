package com.jar.app.feature_health_insurance.shared.domain.use_cases.impl

import com.jar.app.feature_health_insurance.shared.data.models.IncompleteProposal
import com.jar.app.feature_health_insurance.shared.domain.repository.HealthInsuranceRepository
import com.jar.app.feature_health_insurance.shared.domain.use_cases.FetchIncompleteProposalUseCase
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

internal class FetchIncompleteProposalUseCaseImpl constructor(
    private val healthInsuranceRepository: HealthInsuranceRepository
) : FetchIncompleteProposalUseCase{

    override suspend fun fetchIncompleteProposal(): Flow<RestClientResult<ApiResponseWrapper<IncompleteProposal>>> =
        healthInsuranceRepository.fetchIncompleteProposal()

}