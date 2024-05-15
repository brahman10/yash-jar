package com.jar.app.feature_health_insurance.shared.domain.use_cases

import com.jar.app.feature_health_insurance.shared.data.models.CreateProposalRequest
import com.jar.app.feature_health_insurance.shared.data.models.CreateProposalResponse
import com.jar.app.feature_health_insurance.shared.data.models.plan_comparison.PlanComparisonResponse
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface CreateProposalUseCase {
    suspend fun createProposal(createProposalRequest: CreateProposalRequest): Flow<RestClientResult<ApiResponseWrapper<CreateProposalResponse?>>>
}