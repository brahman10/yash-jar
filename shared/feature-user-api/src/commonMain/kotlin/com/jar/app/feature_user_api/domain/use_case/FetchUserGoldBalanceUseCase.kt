package com.jar.app.feature_user_api.domain.use_case

import com.jar.app.core_base.data.dto.GoldBalanceDTO
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface FetchUserGoldBalanceUseCase {

    suspend fun fetchUserGoldBalance(includeView: Boolean = false): Flow<RestClientResult<ApiResponseWrapper<GoldBalanceDTO?>>>
}