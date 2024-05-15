package com.jar.app.feature_buy_gold_v2.shared.domain.use_case

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_buy_gold_v2.shared.domain.model.SuggestedAmountData
import kotlinx.coroutines.flow.Flow

interface FetchSuggestedAmountUseCase {

    suspend fun fetchSuggestedAmount(
        flowContext: String?,
        couponCode: String? = null
    ): Flow<RestClientResult<ApiResponseWrapper<SuggestedAmountData?>>>

}