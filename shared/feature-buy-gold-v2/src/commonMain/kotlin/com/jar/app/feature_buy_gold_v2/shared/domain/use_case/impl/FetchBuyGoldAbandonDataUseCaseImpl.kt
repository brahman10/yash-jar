package com.jar.app.feature_buy_gold_v2.shared.domain.use_case.impl

import com.jar.app.core_base.util.BaseConstants
import com.jar.app.feature_buy_gold_v2.shared.data.repository.BuyGoldV2Repository
import com.jar.app.feature_buy_gold_v2.shared.domain.use_case.FetchBuyGoldAbandonDataUseCase

internal class FetchBuyGoldAbandonDataUseCaseImpl constructor(
    private val buyGoldV2Repository: BuyGoldV2Repository
):FetchBuyGoldAbandonDataUseCase {
    override suspend fun fetchBuyGoldAbandonInfo(staticContentType: BaseConstants.StaticContentType) = buyGoldV2Repository.fetchBuyGoldAbandonInfo(staticContentType)
}