package com.jar.app.feature_health_insurance.shared.domain.use_cases

import com.jar.app.feature_health_insurance.shared.data.models.benefits.BenefitsDetailsResponse
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface FetchBenefitsDetailsUseCase {

    suspend fun fetchBenefitsDetails(insuranceId: String?): Flow<RestClientResult<ApiResponseWrapper<BenefitsDetailsResponse>>>

}