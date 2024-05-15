package com.jar.app.feature_buy_gold_v2.shared.domain.use_case

import com.jar.app.feature_buy_gold_v2.shared.domain.model.BuyGoldBottomSheetV2Data
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface FetchBuyGoldBottomSheetV2UseCase {
    suspend fun fetchBuyGoldBottomSheetV2Data(): Flow<RestClientResult<ApiResponseWrapper<BuyGoldBottomSheetV2Data>>>
}