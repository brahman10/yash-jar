package com.jar.app.feature_kyc.shared.domain.use_case

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_kyc.shared.domain.model.KycDocListResponse
import kotlinx.coroutines.flow.Flow

interface FetchKycDocumentsListUseCase {

    suspend fun fetchKycDocumentsList(): Flow<RestClientResult<ApiResponseWrapper<KycDocListResponse?>>>

}