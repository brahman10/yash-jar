package com.jar.app.feature_health_insurance.shared.domain.use_cases

import com.jar.app.feature_health_insurance.shared.data.models.landing1.LandingPageResponse1
import com.jar.app.feature_one_time_payments.shared.data.model.base.InitiatePaymentResponse
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface FetchPaymentConfigUseCase {
    suspend fun fetchPaymentConfig(insuranceId: String): Flow<RestClientResult<ApiResponseWrapper<InitiatePaymentResponse?>>>
}