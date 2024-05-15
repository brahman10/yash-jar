package com.jar.app.feature_settings.domain.use_case

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_settings.domain.model.VpaChips
import kotlinx.coroutines.flow.Flow

interface FetchVpaChipUseCase {

    suspend fun fetchVpaChips(): Flow<RestClientResult<ApiResponseWrapper<VpaChips>>>

}