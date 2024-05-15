package com.jar.app.feature_lending.shared.domain.use_case

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_lending.shared.domain.model.v2.LoanDetailsV2
import kotlinx.coroutines.flow.Flow

interface FetchLoanDetailsV2UseCase {

    suspend fun getLoanDetails(
        loanId: String,
        checkPoint: String? = null
    ): Flow<RestClientResult<ApiResponseWrapper<LoanDetailsV2?>>>

}