package com.jar.app.feature_lending.shared.domain.use_case

import com.jar.app.feature_lending.shared.domain.model.v2.RequestOtpResponseV2
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface RequestLendingOtpUseCase {

    suspend fun requestLendingOtp(
        loanId: String,
        type: String
    ): Flow<RestClientResult<ApiResponseWrapper<RequestOtpResponseV2?>>>

}