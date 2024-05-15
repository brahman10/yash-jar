package com.myjar.app.feature_graph_manual_buy.domain.userCases.impl

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.myjar.app.feature_graph_manual_buy.data.model.FaqsResponse
import com.myjar.app.feature_graph_manual_buy.data.repository.GraphManualBuyRepository
import com.myjar.app.feature_graph_manual_buy.domain.userCases.FetchManualBuyGraphFaqUseCase
import kotlinx.coroutines.flow.Flow

internal class FetchManualBuyGraphFaqUseCaseImpl constructor(
    private val graphManualBuyRepository: GraphManualBuyRepository
): FetchManualBuyGraphFaqUseCase {
    override suspend fun fetchFaqs(): Flow<RestClientResult<ApiResponseWrapper<FaqsResponse>>> {
        return graphManualBuyRepository.fetchFaqs()
    }
}