package com.jar.app.feature_lending.shared.api.usecase

import com.jar.app.feature_lending.shared.domain.model.LendingFlowStatusResponse
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface FetchLoanProgressStatusV2UseCase {

    suspend fun getLoanProgressStatus(loanId: String): Flow<RestClientResult<ApiResponseWrapper<LendingFlowStatusResponse?>>>

}