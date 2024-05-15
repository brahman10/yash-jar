package com.jar.feature_quests.shared.domain.use_case

import com.jar.feature_quests.shared.domain.model.QuestsDashboardData
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface FetchHomePageUseCase {
    suspend fun fetchHomePage(): Flow<RestClientResult<ApiResponseWrapper<QuestsDashboardData>>>
}