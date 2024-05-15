package com.jar.app.feature_kyc.shared.api.use_case

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_kyc.shared.domain.model.KYCStatusDetails
import kotlinx.coroutines.flow.Flow

interface PostFaceMatchRequestUseCase {

    suspend fun postFaceMatchRequest(docType: String, byteArray: ByteArray,): Flow<RestClientResult<ApiResponseWrapper<KYCStatusDetails?>>>

}