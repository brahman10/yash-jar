package com.jar.app.feature_kyc.shared.domain.use_case

import com.jar.app.feature_kyc.shared.domain.model.KYCStatusDetails
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface FetchKycDetailsUseCase {

    suspend fun fetchKycDetails(kycContext: String? = null): Flow<RestClientResult<ApiResponseWrapper<KYCStatusDetails?>>>

}