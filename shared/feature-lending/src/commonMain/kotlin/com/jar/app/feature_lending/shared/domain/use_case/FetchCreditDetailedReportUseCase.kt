package com.jar.app.feature_lending.shared.domain.use_case

import com.jar.app.feature_lending.shared.domain.model.creditReport.CreditDetailedReportResponse
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface FetchCreditDetailedReportUseCase {

    suspend fun fetchCreditDetailedReport(type: String): Flow<RestClientResult<ApiResponseWrapper<CreditDetailedReportResponse?>>>

}