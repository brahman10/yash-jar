package com.jar.app.feature_spends_tracker.shared.domain.usecase

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_spends_tracker.shared.domain.model.spendsDetailsData.SpendsData
import kotlinx.coroutines.flow.Flow

interface FetchSpendsDataUseCase {
    suspend fun fetchSpendsData(): Flow<RestClientResult<ApiResponseWrapper<SpendsData>>>
}