package com.jar.app.feature_lending.shared.domain.use_case

import com.jar.app.feature_lending.shared.domain.model.camps_flow.CamsSdkRedirectionData
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface FetchCamsSdkRedirectDataUseCase {

    suspend fun fetchCamsSdkRedirectData(fipId: String): Flow<RestClientResult<ApiResponseWrapper<CamsSdkRedirectionData?>>>

}