package com.jar.app.feature_health_insurance.shared.domain.use_cases

import com.jar.app.feature_health_insurance.shared.data.models.select_premium.SelectPremiumResponse
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface FetchInsurancePlansUseCase{

    suspend fun fetchInsurancePlans(orderId: String): Flow<RestClientResult<ApiResponseWrapper<SelectPremiumResponse>>>

}