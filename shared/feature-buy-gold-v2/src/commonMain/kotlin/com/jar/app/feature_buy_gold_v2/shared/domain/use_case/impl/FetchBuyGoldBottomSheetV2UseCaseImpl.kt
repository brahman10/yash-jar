package com.jar.app.feature_buy_gold_v2.shared.domain.use_case.impl

import com.jar.app.feature_buy_gold_v2.shared.data.repository.BuyGoldV2Repository
import com.jar.app.feature_buy_gold_v2.shared.domain.use_case.FetchBuyGoldBottomSheetV2UseCase

internal class FetchBuyGoldBottomSheetV2UseCaseImpl constructor(
    private val buyGoldV2Repository: BuyGoldV2Repository
) : FetchBuyGoldBottomSheetV2UseCase {
    override suspend fun fetchBuyGoldBottomSheetV2Data() =
        buyGoldV2Repository.fetchBuyGoldBottomSheetV2Data()
}