package com.jar.app.feature_health_insurance.shared.domain.use_cases

import com.jar.app.feature_health_insurance.shared.data.models.manage_screen.InsuranceTransactionsData
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult

interface FetchInsuranceTransactionsUseCase {
    suspend fun fetchInsuranceTransactions(
        insuranceId: String,
        page: Int,
        size: Int
    ): RestClientResult<ApiResponseWrapper<InsuranceTransactionsData?>>
}