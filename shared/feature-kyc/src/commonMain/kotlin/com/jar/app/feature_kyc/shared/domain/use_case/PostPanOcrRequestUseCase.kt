package com.jar.app.feature_kyc.shared.domain.use_case

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_kyc.shared.domain.model.KycPanOcrResponse
import kotlinx.coroutines.flow.Flow

interface PostPanOcrRequestUseCase {

    suspend fun postKycOcrRequest(byteArray: ByteArray,): Flow<RestClientResult<ApiResponseWrapper<KycPanOcrResponse?>>>

}