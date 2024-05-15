package com.jar.app.feature_kyc.shared.domain.use_case

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_kyc.shared.domain.model.KycFaqResponse
import kotlinx.coroutines.flow.Flow

interface FetchKycFaqUseCase {

    suspend fun fetchKycFaq(param: String): Flow<RestClientResult<ApiResponseWrapper<KycFaqResponse?>>>

}