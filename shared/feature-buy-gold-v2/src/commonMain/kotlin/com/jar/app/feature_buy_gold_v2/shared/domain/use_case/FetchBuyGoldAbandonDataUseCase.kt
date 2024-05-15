package com.jar.app.feature_buy_gold_v2.shared.domain.use_case

import com.jar.app.core_base.util.BaseConstants
import com.jar.app.feature_buy_gold_v2.shared.domain.model.BuyGoldAbandonResponse
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface FetchBuyGoldAbandonDataUseCase {
    suspend fun fetchBuyGoldAbandonInfo(staticContentType: BaseConstants.StaticContentType):
            Flow<RestClientResult<ApiResponseWrapper<BuyGoldAbandonResponse>>>
}