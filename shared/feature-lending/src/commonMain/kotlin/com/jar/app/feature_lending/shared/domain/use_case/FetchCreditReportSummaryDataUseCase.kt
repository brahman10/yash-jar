package com.jar.app.feature_lending.shared.domain.use_case

import com.jar.app.feature_lending.shared.domain.model.creditReport.CreditSummaryDataResponse
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface FetchCreditReportSummaryDataUseCase {

    suspend fun fetchCreditReportSummary(): Flow<RestClientResult<ApiResponseWrapper<CreditSummaryDataResponse?>>>

}