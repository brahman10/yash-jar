package com.jar.app.feature_gold_lease.shared.domain.use_case.impl

import com.jar.app.feature_gold_lease.shared.data.repository.GoldLeaseRepository
import com.jar.app.feature_gold_lease.shared.domain.use_case.FetchGoldLeaseJewellerListingsUseCase

internal class FetchGoldLeaseJewellerListingsImpl constructor(
    private val goldLeaseRepository: GoldLeaseRepository
): FetchGoldLeaseJewellerListingsUseCase {
    override suspend fun fetchGoldLeaseJewellerListings() = goldLeaseRepository.fetchGoldLeaseJewellerListings()
}