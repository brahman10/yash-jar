package com.jar.app.feature_user_api.domain.use_case

import com.jar.app.feature_user_api.data.dto.UserGoldSipDetailsDTO
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface FetchGoldSipDetailsUseCase {

    suspend fun fetchGoldSipDetails(includeView: Boolean = false): Flow<RestClientResult<ApiResponseWrapper<UserGoldSipDetailsDTO>>>

}