package com.jar.app.feature_lending.shared.domain.use_case

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_lending.shared.domain.model.v2.LandingScreenContentResponse
import kotlinx.coroutines.flow.Flow

interface FetchReadyCashLandingScreenContentUseCase {
    suspend fun fetchReadyCashLandingScreenContent(): Flow<RestClientResult<ApiResponseWrapper<LandingScreenContentResponse?>>>

}