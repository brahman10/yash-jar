package com.jar.app.feature_gold_redemption.shared.domain.use_case

import com.jar.app.feature_gold_redemption.shared.data.network.model.IntroScreenAPIDataPart2
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface VoucherGoldRedemptionIntroPart2UseCase {
    suspend fun fetchGoldRedemptionIntroPart2(): Flow<RestClientResult<ApiResponseWrapper<IntroScreenAPIDataPart2?>>>
}