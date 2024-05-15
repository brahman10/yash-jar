package com.myjar.app.feature_graph_manual_buy.domain.userCases.impl

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.myjar.app.feature_graph_manual_buy.data.model.GraphManualBuyPriceGraphModel
import com.myjar.app.feature_graph_manual_buy.data.repository.GraphManualBuyRepository
import com.myjar.app.feature_graph_manual_buy.domain.userCases.FetchGraphDataUseCase
import kotlinx.coroutines.flow.Flow

class FetchGraphDataUseCaseImpl  constructor(
    private val graphManualBuyRepository: GraphManualBuyRepository
): FetchGraphDataUseCase {
    override suspend fun fetchGraphData(): Flow<RestClientResult<ApiResponseWrapper<GraphManualBuyPriceGraphModel>>> {
        return graphManualBuyRepository.fetchGraphData()
    }
}