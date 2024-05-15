package com.jar.app.feature_lending_kyc.shared.api.use_case

import com.jar.app.core_base.data.dto.KycProgressResponseDTO
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface FetchKycProgressUseCase {

    suspend fun fetchKycProgress(): Flow<RestClientResult<ApiResponseWrapper<KycProgressResponseDTO?>>>

}