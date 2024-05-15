package com.myjar.app.feature_graph_manual_buy.data.repository

import com.jar.internal.library.jar_core_network.api.data.BaseRepository
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.myjar.app.feature_graph_manual_buy.data.model.CalanderModel
import com.myjar.app.feature_graph_manual_buy.data.model.FaqsResponse
import com.myjar.app.feature_graph_manual_buy.data.model.GraphManualBuyPriceGraphModel
import com.myjar.app.feature_graph_manual_buy.data.model.QuickActionResponse
import kotlinx.coroutines.flow.Flow

interface GraphManualBuyRepository: BaseRepository {
    suspend fun fetchFaqs(): Flow<RestClientResult<ApiResponseWrapper<FaqsResponse>>>
    suspend fun fetchQuickActions(): Flow<RestClientResult<ApiResponseWrapper<QuickActionResponse>>>
    suspend fun fetchGraphData(): Flow<RestClientResult<ApiResponseWrapper<GraphManualBuyPriceGraphModel>>>
    suspend fun fetchCalenderData(startDate: String, endDate: String): Flow<RestClientResult<ApiResponseWrapper<CalanderModel>>>
}