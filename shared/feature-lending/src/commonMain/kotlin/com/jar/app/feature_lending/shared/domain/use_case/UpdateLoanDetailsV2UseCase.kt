package com.jar.app.feature_lending.shared.domain.use_case

import com.jar.app.feature_lending.shared.domain.model.v2.LoanApplicationUpdateResponseV2
import com.jar.app.feature_lending.shared.domain.model.v2.UpdateLoanDetailsBodyV2
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface UpdateLoanDetailsV2UseCase {

    suspend fun updateLoanDetails(
        updateLoanDetailsBody: UpdateLoanDetailsBodyV2,
        checkPoint: String
    ): Flow<RestClientResult<ApiResponseWrapper<LoanApplicationUpdateResponseV2?>>>

}