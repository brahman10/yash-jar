package com.jar.app.feature.home.domain.usecase

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature.home.domain.model.AdSourceData
import kotlinx.coroutines.flow.Flow

internal interface UpdateAdSourceDataUseCase {
    suspend fun updateAdSourceData(adSourceData: AdSourceData): Flow<RestClientResult<ApiResponseWrapper<Unit?>>>
}