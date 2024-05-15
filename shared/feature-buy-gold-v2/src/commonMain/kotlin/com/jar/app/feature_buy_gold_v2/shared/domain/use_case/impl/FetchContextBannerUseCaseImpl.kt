package com.jar.app.feature_buy_gold_v2.shared.domain.use_case.impl

import com.jar.app.feature_buy_gold_v2.shared.data.repository.BuyGoldV2Repository
import com.jar.app.feature_buy_gold_v2.shared.domain.model.ContextBannerResponse
import com.jar.app.feature_buy_gold_v2.shared.domain.use_case.FetchContextBannerUseCase
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

internal class FetchContextBannerUseCaseImpl( private val buyGoldV2Repository: BuyGoldV2Repository) : FetchContextBannerUseCase {
    override suspend fun fetchContextBanner(flowContext: String): Flow<RestClientResult<ApiResponseWrapper<ContextBannerResponse?>>>
    = buyGoldV2Repository.fetchContextBanner(flowContext)
}