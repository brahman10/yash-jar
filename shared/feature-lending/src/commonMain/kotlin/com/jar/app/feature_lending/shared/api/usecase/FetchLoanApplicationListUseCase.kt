package com.jar.app.feature_lending.shared.api.usecase

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_lending.shared.domain.model.v2.LoanApplicationItemV2
import kotlinx.coroutines.flow.Flow

interface FetchLoanApplicationListUseCase {

    suspend fun fetchLoanApplicationList(): Flow<RestClientResult<ApiResponseWrapper<List<LoanApplicationItemV2>?>>>

}