package com.jar.app.feature_kyc.shared.api.use_case

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_kyc.shared.domain.model.KycOcrResponse
import kotlinx.coroutines.flow.Flow

interface PostKycOcrRequestUseCase {

    suspend fun postKycOcrRequest(
        docType: String,
        byteArray: ByteArray,
        isKyc: Boolean = false
    ): Flow<RestClientResult<ApiResponseWrapper<KycOcrResponse?>>>

}