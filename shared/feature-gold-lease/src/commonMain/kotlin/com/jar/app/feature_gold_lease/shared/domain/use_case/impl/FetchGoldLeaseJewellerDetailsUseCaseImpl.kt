package com.jar.app.feature_gold_lease.shared.domain.use_case.impl

import com.jar.app.feature_gold_lease.shared.data.repository.GoldLeaseRepository
import com.jar.app.feature_gold_lease.shared.domain.use_case.FetchGoldLeaseJewellerDetailsUseCase

internal class FetchGoldLeaseJewellerDetailsUseCaseImpl constructor(
    private val goldLeaseRepository: GoldLeaseRepository
): FetchGoldLeaseJewellerDetailsUseCase {
    override suspend fun fetchJewellerDetails(jewellerId: String) = goldLeaseRepository.fetchJewellerDetails(
        jewellerId = jewellerId
    )
}