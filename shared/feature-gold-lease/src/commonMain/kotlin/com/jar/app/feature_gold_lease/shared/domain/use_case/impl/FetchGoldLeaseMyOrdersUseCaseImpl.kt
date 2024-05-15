package com.jar.app.feature_gold_lease.shared.domain.use_case.impl

import com.jar.app.feature_gold_lease.shared.data.repository.GoldLeaseRepository
import com.jar.app.feature_gold_lease.shared.domain.use_case.FetchGoldLeaseMyOrdersUseCase

internal class FetchGoldLeaseMyOrdersUseCaseImpl constructor(
    private val goldLeaseRepository: GoldLeaseRepository
): FetchGoldLeaseMyOrdersUseCase{

    override suspend fun fetchGoldLeaseV2MyOrders() = goldLeaseRepository.fetchGoldLeaseV2MyOrders()

}