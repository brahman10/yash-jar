package com.jar.app.feature_spends_tracker.shared.domain.usecase

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_spends_tracker.shared.domain.model.spends_education.SpendsEducationData
import kotlinx.coroutines.flow.Flow

interface FetchSpendsEducationDataUseCase {
    suspend fun fetchSpendsEducationData(): Flow<RestClientResult<ApiResponseWrapper<SpendsEducationData?>>>
}