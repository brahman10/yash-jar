package com.jar.app.feature_sell_gold.shared.domain.use_cases

import com.jar.app.feature_sell_gold.shared.domain.models.DrawerDetailsResponse
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface FetchDrawerDetailsUseCase {
    suspend operator fun invoke(): Flow<RestClientResult<ApiResponseWrapper<DrawerDetailsResponse?>>>
}