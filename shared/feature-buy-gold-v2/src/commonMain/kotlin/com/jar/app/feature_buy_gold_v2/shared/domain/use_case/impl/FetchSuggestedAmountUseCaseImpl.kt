package com.jar.app.feature_buy_gold_v2.shared.domain.use_case.impl

import com.jar.app.feature_buy_gold_v2.shared.data.repository.BuyGoldV2Repository
import com.jar.app.feature_buy_gold_v2.shared.domain.use_case.FetchSuggestedAmountUseCase

internal class FetchSuggestedAmountUseCaseImpl  constructor(
    private val buyGoldV2Repository: BuyGoldV2Repository
) : FetchSuggestedAmountUseCase {

    override suspend fun fetchSuggestedAmount(flowContext: String?, couponCode: String?) =
        buyGoldV2Repository.fetchSuggestedAmount(flowContext = flowContext, couponCode = couponCode)

}