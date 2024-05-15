package com.jar.app.feature_gold_price.shared.domain.use_case

import com.jar.app.feature_gold_price.shared.data.model.FetchCurrentGoldPriceResponse
import com.jar.app.feature_gold_price.shared.data.model.GoldPriceContext
import com.jar.app.feature_gold_price.shared.data.model.GoldPriceType
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface FetchCurrentGoldPriceUseCase {

    suspend fun fetchCurrentGoldPrice(goldPriceType: GoldPriceType, goldPriceContext: GoldPriceContext? = null): Flow<RestClientResult<ApiResponseWrapper<FetchCurrentGoldPriceResponse>>>

    suspend fun fetchCurrentGoldPriceSync(goldPriceType: GoldPriceType): RestClientResult<ApiResponseWrapper<FetchCurrentGoldPriceResponse>>
}