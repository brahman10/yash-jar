package com.jar.app.feature_settings.domain.use_case

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_settings.domain.model.CardBinInfo
import kotlinx.coroutines.flow.Flow

interface FetchCardBinInfoUseCase {
    suspend fun fetchCardBinInfo(cardBin: String): Flow<RestClientResult<ApiResponseWrapper<CardBinInfo>>>

}