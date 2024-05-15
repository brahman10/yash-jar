package com.jar.app.feature_gifting.shared.domain.use_case

import com.jar.app.feature_gifting.shared.domain.model.GoldGiftReceivedResponse
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface FetchReceivedGiftsUseCase {

    suspend fun fetchReceivedGift(): Flow<RestClientResult<ApiResponseWrapper<List<GoldGiftReceivedResponse>>>>

}