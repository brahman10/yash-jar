package com.jar.app.feature_buy_gold_v2.shared.domain.use_case

import com.jar.app.feature_buy_gold_v2.shared.domain.model.ContextBannerResponse
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface FetchContextBannerUseCase {
    suspend fun fetchContextBanner(flowContext: String): Flow<RestClientResult<ApiResponseWrapper<ContextBannerResponse?>>>
}