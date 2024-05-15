package com.myjar.app.feature_graph_manual_buy.domain.userCases.impl

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.myjar.app.feature_graph_manual_buy.data.model.CalanderModel
import com.myjar.app.feature_graph_manual_buy.data.repository.GraphManualBuyRepository
import com.myjar.app.feature_graph_manual_buy.domain.userCases.FetchCalenderUseCase
import kotlinx.coroutines.flow.Flow

internal class FetchCalenderUseCaseImpl constructor(
    private val graphManualBuyRepository: GraphManualBuyRepository
): FetchCalenderUseCase {
    override suspend fun fetchCalender(startDate: String, endDate: String): Flow<RestClientResult<ApiResponseWrapper<CalanderModel>>> {
        return graphManualBuyRepository.fetchCalenderData(startDate, endDate)
    }
}