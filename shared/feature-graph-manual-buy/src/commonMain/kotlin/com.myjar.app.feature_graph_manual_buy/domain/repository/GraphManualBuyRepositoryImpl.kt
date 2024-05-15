package com.myjar.app.feature_graph_manual_buy.domain.repository

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.myjar.app.feature_graph_manual_buy.data.model.CalanderModel
import com.myjar.app.feature_graph_manual_buy.data.model.FaqsResponse
import com.myjar.app.feature_graph_manual_buy.data.model.GraphManualBuyPriceGraphModel
import com.myjar.app.feature_graph_manual_buy.data.model.QuickActionResponse
import com.myjar.app.feature_graph_manual_buy.data.network.GraphManualBuyDataSource
import com.myjar.app.feature_graph_manual_buy.data.repository.GraphManualBuyRepository
import kotlinx.coroutines.flow.Flow

class GraphManualBuyRepositoryImpl constructor(
    private val graphManualBuyDataSource: GraphManualBuyDataSource
): GraphManualBuyRepository {
    override suspend fun fetchFaqs(): Flow<RestClientResult<ApiResponseWrapper<FaqsResponse>>> {
        return getFlowResult { graphManualBuyDataSource.fetchFaqs() }
    }

    override suspend fun fetchQuickActions(): Flow<RestClientResult<ApiResponseWrapper<QuickActionResponse>>> {
        return getFlowResult {
            graphManualBuyDataSource.fetchQuickAction()
        }
    }

    override suspend fun fetchGraphData(): Flow<RestClientResult<ApiResponseWrapper<GraphManualBuyPriceGraphModel>>> {
        return getFlowResult {
            graphManualBuyDataSource.fetchGraphData()
        }
    }

    override suspend fun fetchCalenderData(startDate: String, endDate: String): Flow<RestClientResult<ApiResponseWrapper<CalanderModel>>> {
        return getFlowResult {
            graphManualBuyDataSource.fetchCalenderData(startDate, endDate)
        }
    }

}