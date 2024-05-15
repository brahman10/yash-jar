package com.myjar.app.feature_graph_manual_buy.domain.userCases.impl

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.myjar.app.feature_graph_manual_buy.data.model.QuickActionResponse
import com.myjar.app.feature_graph_manual_buy.data.repository.GraphManualBuyRepository
import com.myjar.app.feature_graph_manual_buy.domain.userCases.FetchQuickActionUseCase
import kotlinx.coroutines.flow.Flow

internal class FetchQuickActionUseCaseImpl constructor(
    private val graphManualBuyRepository: GraphManualBuyRepository
): FetchQuickActionUseCase{
    override suspend fun fetchQuickAction(): Flow<RestClientResult<ApiResponseWrapper<QuickActionResponse>>> {
        return graphManualBuyRepository.fetchQuickActions()
    }
}