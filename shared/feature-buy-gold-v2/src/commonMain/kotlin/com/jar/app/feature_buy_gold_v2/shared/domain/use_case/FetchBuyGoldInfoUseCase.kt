package com.jar.app.feature_buy_gold_v2.shared.domain.use_case

import com.jar.app.core_base.domain.model.InfoDialogResponse
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface FetchBuyGoldInfoUseCase {
    suspend fun fetchBuyGoldInfo(): Flow<RestClientResult<ApiResponseWrapper<InfoDialogResponse>>>
}