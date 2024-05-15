package com.jar.app.feature_lending_kyc.shared.domain.use_case

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_lending_kyc.shared.domain.model.KycAadhaar
import kotlinx.coroutines.flow.Flow

interface FetchKycAadhaarDetailsUseCase {

    suspend fun fetchKycAadhaarDetails(): Flow<RestClientResult<ApiResponseWrapper<KycAadhaar?>>>

}