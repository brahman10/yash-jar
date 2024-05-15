package com.jar.app.feature_homepage.shared.domain.use_case

import com.jar.app.feature_homepage.shared.domain.model.FeatureRedirectionResp
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface FetchFeatureRedirectionUseCase {

    suspend fun fetchFeatureRedirectionData(): Flow<RestClientResult<ApiResponseWrapper<FeatureRedirectionResp?>>>
}