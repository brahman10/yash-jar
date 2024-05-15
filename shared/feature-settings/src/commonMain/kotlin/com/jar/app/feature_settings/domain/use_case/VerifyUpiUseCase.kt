package com.jar.app.feature_settings.domain.use_case

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_settings.domain.model.VerifyUpiResponse
import kotlinx.coroutines.flow.Flow

interface VerifyUpiUseCase {

    suspend fun verifyUpiAddress(upiAddress: String): Flow<RestClientResult<ApiResponseWrapper<VerifyUpiResponse>>>

}