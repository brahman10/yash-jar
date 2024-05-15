package com.jar.app.feature_gifting.shared.domain.use_case

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_gifting.shared.domain.model.GiftGoldOptions
import kotlinx.coroutines.flow.Flow

interface FetchGiftGoldOptionsUseCase {

    suspend fun fetchGiftGoldOptions(): Flow<RestClientResult<ApiResponseWrapper<GiftGoldOptions>>>

}