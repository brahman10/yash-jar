package com.jar.app.feature_lending.shared.domain.use_case

import com.jar.app.feature_lending.shared.domain.model.OtpVerifyRequestData
import com.jar.app.feature_lending.shared.domain.model.v2.ReadyCashVerifyOtpResponse
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface VerifyLendingOtpUseCase {

    suspend fun verifyLendingOtp(data: OtpVerifyRequestData): Flow<RestClientResult<ApiResponseWrapper<ReadyCashVerifyOtpResponse?>>>

}