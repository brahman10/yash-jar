package com.jar.app.feature.home.domain.usecase

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature.home.domain.model.IsKycRequiredData
import kotlinx.coroutines.flow.Flow

interface FetchIfKycRequiredUseCase {

    suspend fun fetchIfKycRequired(): Flow<RestClientResult<ApiResponseWrapper<IsKycRequiredData>>>

}