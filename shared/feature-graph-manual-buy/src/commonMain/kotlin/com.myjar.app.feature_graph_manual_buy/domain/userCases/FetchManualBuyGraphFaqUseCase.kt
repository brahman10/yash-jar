package com.myjar.app.feature_graph_manual_buy.domain.userCases

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.myjar.app.feature_graph_manual_buy.data.model.FaqsResponse
import kotlinx.coroutines.flow.Flow

interface FetchManualBuyGraphFaqUseCase {
    suspend fun fetchFaqs(): Flow<RestClientResult<ApiResponseWrapper<FaqsResponse>>>
}