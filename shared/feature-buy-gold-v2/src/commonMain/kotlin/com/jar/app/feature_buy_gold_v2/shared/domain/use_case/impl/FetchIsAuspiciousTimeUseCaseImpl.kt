package com.jar.app.feature_buy_gold_v2.shared.domain.use_case.impl

import com.jar.app.feature_buy_gold_v2.shared.data.repository.BuyGoldV2Repository
import com.jar.app.feature_buy_gold_v2.shared.domain.use_case.FetchAuspiciousTimeUseCase

internal class FetchIsAuspiciousTimeUseCaseImpl constructor(private val buyGoldV2Repository: BuyGoldV2Repository) :
    FetchAuspiciousTimeUseCase {
    override suspend fun fetchIsAuspiciousTime() = buyGoldV2Repository.fetchIsAuspiciousTime()

}