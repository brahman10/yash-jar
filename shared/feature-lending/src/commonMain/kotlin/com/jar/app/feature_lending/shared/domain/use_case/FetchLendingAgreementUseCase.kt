package com.jar.app.feature_lending.shared.domain.use_case

import com.jar.app.feature_lending.shared.domain.model.temp.LendingAgreementResponse
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface FetchLendingAgreementUseCase {

    suspend fun fetchLendingAgreement(loanId: String): Flow<RestClientResult<ApiResponseWrapper<LendingAgreementResponse?>>>

}