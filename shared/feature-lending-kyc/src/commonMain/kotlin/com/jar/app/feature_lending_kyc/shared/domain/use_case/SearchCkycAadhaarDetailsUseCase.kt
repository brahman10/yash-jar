package com.jar.app.feature_lending_kyc.shared.domain.use_case

import com.jar.app.feature_lending_kyc.shared.domain.model.KycAadhaarRequest
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface SearchCkycAadhaarDetailsUseCase {

    suspend fun searchCKycAadhaarDetails(kycAadhaarRequest: KycAadhaarRequest): Flow<RestClientResult<ApiResponseWrapper<Unit?>>>
}